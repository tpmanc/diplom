package controllers;

import exceptions.CustomSQLException;
import exceptions.CustomWebException;
import helpers.FileCheckSum;
import helpers.PEProperties;
import jdk.nashorn.internal.parser.JSONParser;
import models.*;
import models.helpers.FileFilling;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.security.Timestamp;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminFileController {

    @RequestMapping(value = {"/files" }, method = RequestMethod.GET)
    public String index(Model model) {
        ArrayList<HashMap> files = null;
        String properties;
        try {
            files = FileModel.findAll();
            model.addAttribute("files", files);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("pageTitle", "Файлы");
        return "admin/file/files";
    }

    @RequestMapping(value = {"/file-view" }, method = RequestMethod.GET)
    public String view(@RequestParam("id") int id, @RequestParam(value="versionId", required=false, defaultValue = "0") int versionId, Model model) {
        try {
            FileModel file = FileModel.findById(id);
            model.addAttribute("file", file);

            FileVersionModel currentVersion;
            if (versionId == 0) {
                currentVersion = file.getLastVersion();
            } else {
                currentVersion = FileVersionModel.findByIdAndFile(versionId, id);
            }
            model.addAttribute("currentVersion", currentVersion);

            ArrayList versionList = file.getVersionList();
            model.addAttribute("versionList", versionList);

            ArrayList fileVersionProperties = FileVersionPropertyModel.getProperties(currentVersion.getId());
            model.addAttribute("fileVersionProperties", fileVersionProperties);

            ArrayList fileProperties = FilePropertyModel.getProperties(file.getId());
            model.addAttribute("fileProperties", fileProperties);

            Date date = new Date(currentVersion.getDate());
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String downloadDate = df.format(date);
            model.addAttribute("downloadDate", downloadDate);
        } catch (SQLException e) {
            throw new CustomWebException("Файл не существует");
        }

        model.addAttribute("pageTitle", "Просмотр файла");
        return "admin/file/file-view";
    }

    @RequestMapping(value = {"/file-edit" }, method = RequestMethod.GET)
    public String update(@RequestParam("id") int id, Model model) {
        FileModel file = null;
        try {
            file = FileModel.findById(id);
        } catch (SQLException e) {
            throw new CustomWebException("Файл не существует");
        }
        model.addAttribute("file", file);
        model.addAttribute("pageTitle", "Изменить файл");
        return "admin/file/file-edit";
    }

    @RequestMapping(value = {"/file-add" }, method = RequestMethod.GET)
    public String fileAdd(Model model) {
        try {
            // список свойств
            String properties = PropertyModel.getAllJson();
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            throw new CustomWebException("Файл не существует");
        }

        model.addAttribute("pageTitle", "Добавить файл");
        //returns the view name
        return "admin/file/file-add";
    }

    @ResponseBody
    @RequestMapping(value = {"/file-title-autocomplete"}, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String fileTitleAutocomplete(@RequestParam("query") String query) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.put("query", query);

        ArrayList<HashMap> res = FileModel.findTitles(query);
        for (HashMap row : res) {
            JSONObject obj = new JSONObject();
            obj.put("value", row.get("title"));
            obj.put("data", row.get("id"));
            array.add(obj);
        }
        result.put("suggestions", array);

        return result.toJSONString();
    }

    @ResponseBody
    @RequestMapping(value = {"/file-filling"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileFilling(@RequestBody FileFilling[] res) {
        JSONObject result = new JSONObject();
        JSONArray errors = new JSONArray();
        JSONArray success = new JSONArray();

        for (FileFilling model : res) {
            JSONObject file = new JSONObject();
            boolean isError = false;
            file.put("id", model.getId());
            if (model.getTitle().length() == 0) {
                isError = true;
                file.put("msgTitle", "Заполните название продукта");
            }
            if (model.getVersion().length() == 0) {
                isError = true;
                file.put("msgVersion", "Заполните версию файла");
            }
            if (isError) {
                errors.add(file);
            } else {
                FileModel fileModel = null;
                try {
                    FileVersionModel version = FileVersionModel.findById(model.getId());
                    version.setVersion(model.getVersion());
                    version.setIsFilled(true);
                    // ищем файл с таким названием
                    fileModel = FileModel.findByTitle(model.getTitle());
                    if (fileModel == null) {
                        // если такого файла нет, то создаем
                        fileModel = new FileModel();
                        fileModel.setTitle(model.getTitle());
                        fileModel.add();
                    }
                    version.setFileId(fileModel.getId());
                    version.update();
                } catch (SQLException e) {
                    throw new CustomSQLException(e.getMessage());
                }
                success.add(file);
            }
        }

        result.put("errors", errors);
        result.put("success", success);

        return result.toJSONString();
    }

    @ResponseBody
    @RequestMapping(value = {"/file-add-handler" }, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileAddHandler(@RequestParam("file[]") MultipartFile[] files, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        JSONArray errors = new JSONArray();
        JSONArray success = new JSONArray();

        String uploadPath = request.getServletContext().getRealPath("upload");
        File uploadRootDir = new File(uploadPath);
        // Создаем основную директорию, если ее нет
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }

        int fileCounter = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String uploadedFileName = new String(file.getOriginalFilename().getBytes("ISO-8859-1"), "UTF-8");
                    InputStream inputStream = file.getInputStream();
                    // формирование пути до файла
                    String hash = FileCheckSum.get(inputStream);
                    inputStream.close();
                    String firstDir = hash.substring(0, 2);
                    String secondDir = hash.substring(2, 4);
                    StringBuilder newFileName = new StringBuilder();
                    newFileName
                            .append(uploadRootDir.getAbsolutePath())
                            .append(File.separator)
                            .append(firstDir)
                            .append(File.separator)
                            .append(secondDir);

                    // проверяем дублирование файла
                    if (FileModel.isExist(hash, file.getSize())) {
                        JSONObject err = new JSONObject();
                        err.put("number", fileCounter);
                        err.put("msg", "Такой файл уже есть");
                        errors.add(err);
                    } else {
                        // проверка существования пути до файла
                        File uploadDir = new File(String.valueOf(newFileName));
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }

                        // добавление конечного имени файла
                        StringBuilder fileName = new StringBuilder()
                                .append(FilenameUtils.getBaseName(uploadedFileName))
                                .append("_")
                                .append(hash);
                        String extension = FilenameUtils.getExtension(uploadedFileName);
                        if (!extension.equals("")) {
                            fileName.append(".");
                            fileName.append(extension);
                        }
                        newFileName.append(File.separator).append(fileName);

                        // сохранение файла на диск
                        inputStream = file.getInputStream();
                        FileOutputStream stream = new FileOutputStream(new File(newFileName.toString()));
                        IOUtils.copy(inputStream, stream);
                        stream.close();
                        inputStream.close();
                        file.getInputStream().close();

                        // получение свойств файла
                        Map<Integer, String> properties = null;
                        try {
                            properties = PEProperties.parse(newFileName.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        boolean isFilled = false;
                        String fileTitle = null;
                        String versionValue = null;
                        if (properties != null) {
                            fileTitle = properties.get(PropertyModel.PRODUCT_NAME);
                            versionValue = properties.get(PropertyModel.FILE_VERSION);
                            if (fileTitle != null && !fileTitle.trim().equals("") && versionValue != null && !versionValue.trim().equals("")) {
                                isFilled = true;
                            }
                        }

                        // сохранение файла в бд, если свойства заполненены
                        FileModel fileModel = null;
                        if (isFilled) {
                            fileModel = FileModel.findByTitle(fileTitle);
                            if (fileModel == null) {
                                fileModel = new FileModel();
                                fileModel.setTitle(fileTitle);
                                fileModel.add();
                            }
                        }

                        // добавленией новой версии файла
                        FileVersionModel fileVersion = new FileVersionModel();
                        if (fileModel == null) {
                            fileVersion.setFileId(0);
                        } else {
                            fileVersion.setFileId(fileModel.getId());
                        }
                        fileVersion.setFileName(fileName.toString());
                        fileVersion.setHash(hash);
                        fileVersion.setIsFilled(isFilled);
                        long time = new Date().getTime();
                        fileVersion.setDate(time);
                        if (versionValue != null && !versionValue.trim().equals("")) {
                            fileVersion.setVersion(versionValue);
                        }
                        fileVersion.setFileSize(file.getSize());
                        fileVersion.add();

                        // добавление остальных свойств версии в БД
                        if (properties != null) {
                            for (Map.Entry entry : properties.entrySet()) {
                                int propertyId = (Integer) entry.getKey();
                                if (propertyId != PropertyModel.FILE_VERSION && propertyId != PropertyModel.PRODUCT_NAME) {
                                    FileVersionPropertyModel fileProperty = new FileVersionPropertyModel();
                                    fileProperty.setFileVersionId(fileVersion.getId());
                                    fileProperty.setPropertyId(propertyId);
                                    fileProperty.setValue(String.valueOf(entry.getValue()));
                                    fileProperty.add();
                                }
                            }
                        }
                        JSONObject succ = new JSONObject();
                        succ.put("fileVersionId", fileVersion.getId());

                        succ.put("fileVersionName", uploadedFileName);
                        succ.put("isFilled", isFilled);
                        success.add(succ);
                    }
                } catch (Exception e) {
                    JSONObject err = new JSONObject();
                    err.put("number", fileCounter);
                    err.put("msg", e.getMessage());
                    errors.add(err);
                }
            } else {
                JSONObject err = new JSONObject();
                err.put("number", fileCounter);
                err.put("msg", "Проблема с обработкой файла");
                errors.add(err);
            }
            fileCounter++;
        }
        result.put("errors", errors);
        result.put("success", success);
        return result.toJSONString();
    }

    @RequestMapping(value = {"/file-download" }, method = RequestMethod.GET)
    public String fileDownload(@RequestParam("id") int id, Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            FileVersionModel fileVersion = FileVersionModel.findById(id);
            StringBuilder filePath = new StringBuilder();
            // формируем путь до файла
            filePath.append(request.getServletContext().getRealPath("upload"));
            String hash = fileVersion.getHash();
            String firstDir = hash.substring(0, 2);
            String secondDir = hash.substring(2, 4);
            filePath
                    .append(File.separator)
                    .append(firstDir)
                    .append(File.separator)
                    .append(secondDir)
                    .append(File.separator)
                    .append(fileVersion.getFileName());
            File file = new File(filePath.toString());
            InputStream inputStream = new FileInputStream(file);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileVersion.getFileName() + "\"");
            OutputStream os = response.getOutputStream();
            FileCopyUtils.copy(inputStream, os);
            os.close();
            inputStream.close();
            return "redirect:/admin/file-view?id="+fileVersion.getFileId();
        } catch (SQLException e) {
            throw new CustomWebException("Файла не существует");
        } catch (FileNotFoundException e) {
            throw new CustomWebException("Файла не найден");
        } catch (IOException e) {
            throw new CustomWebException("Ошибка", "500");
        }
    }
}