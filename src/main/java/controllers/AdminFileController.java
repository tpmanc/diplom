package controllers;

import exceptions.CustomWebException;
import helpers.FileCheckSum;
import helpers.PEProperties;
import models.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public String view(@RequestParam("id") int id, Model model) {
        try {
            FileModel file = FileModel.findById(id);
            model.addAttribute("file", file);

            ArrayList fileVersionProperties = FileVersionPropertyModel.getProperties(file.getId());
            model.addAttribute("fileVersionProperties", fileVersionProperties);

            ArrayList fileProperties = FilePropertyModel.getProperties(file.getId());
            model.addAttribute("fileProperties", fileProperties);
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

    @RequestMapping(value = {"/file-add-property" }, method = RequestMethod.GET)
    public String fileAddProperty(@RequestParam("id") int id, Model model) {
        try {
            FileModel file = FileModel.findById(id);
            model.addAttribute("file", file);

            ArrayList<HashMap> properties = PropertyModel.findAllNotUsedCustom(file.getId());
            model.addAttribute("properties", properties);
        } catch (SQLException e) {
            throw new CustomWebException("Файл не существует");
        }

        model.addAttribute("pageTitle", "Добавить свойство");
        //returns the view name
        return "admin/file/file-add-property";
    }

    @RequestMapping(value = {"/file-edit-property" }, method = RequestMethod.GET)
    public String fileEditProperty(@RequestParam("id") int id, Model model) {
        try {
            FilePropertyModel fileProperty = FilePropertyModel.findById(id);
            model.addAttribute("fileProperty", fileProperty);
        } catch (SQLException e) {
            throw new CustomWebException("Свойство файла не существует");
        }

        model.addAttribute("pageTitle", "Изменить свойство файла");
        //returns the view name
        return "admin/file/file-edit-property";
    }

    @RequestMapping(value = {"/file-property-handler" }, method = RequestMethod.POST)
    public String fileAddPropertyHandler(
            @RequestParam("fileId") int fileId,
            @RequestParam("propertyId") int propertyId,
            @RequestParam("value") String value,
            @RequestParam(value="id", required=false, defaultValue = "0") int id
    ) {
        if (id == 0) {
            FilePropertyModel fileProperty = new FilePropertyModel(fileId, propertyId, value);
            try {
                if (fileProperty.add()) {
                    return "redirect:/admin/file-view?id="+fileId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FilePropertyModel fileProperty = FilePropertyModel.findById(id);
                fileProperty.setValue(value);
                if (fileProperty.update()) {
                    return "redirect:/admin/file-view?id="+fileId;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //returns the view name
        return "admin/file/file-add-property?id="+fileId;
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
                        newFileName.append(File.separator)
                                .append(FilenameUtils.getBaseName(file.getOriginalFilename()))
                                .append("_")
                                .append(hash);
                        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                        if (!extension.equals("")) {
                            newFileName.append(".");
                            newFileName.append(extension);
                        }

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

                        // сохранение файла в бд
                        FileModel fileModel = new FileModel();
                        if (properties != null) {
                            String fileTitle = properties.get(PropertyModel.PRODUCT_NAME);
                            if (fileTitle != null && !fileTitle.trim().equals("")) {
                                // если в свойствах есть название и оно не пустое
                                fileModel.setTitle(fileTitle);
                            } else {
                                // иначе берем название самого файла
                                fileModel.setTitle(file.getOriginalFilename());
                            }
                        } else {
                            fileModel.setTitle(file.getOriginalFilename());
                        }
                        fileModel.add();

                        // добавленией новой версии файла
                        boolean needVersion = false;
                        FileVersionModel fileVersion = new FileVersionModel();
                        fileVersion.setFileId(fileModel.getId());
                        fileVersion.setHash(hash);
                        if (properties != null) {
                            String versionValue = properties.get(PropertyModel.FILE_VERSION);
                            if (versionValue != null && !versionValue.trim().equals("")) {
                                fileVersion.setVersion(versionValue);
                            } else {
                                needVersion = true;
                            }
                        }
                        fileVersion.setFileSize(file.getSize());
                        fileVersion.add();

                        // добавление остальных свойств в БД
                        if (properties != null) {
                            for (Map.Entry entry : properties.entrySet()) {
                                int propertyId = (Integer) entry.getKey();
                                if (propertyId != 2 && propertyId != 3) {
                                    FileVersionPropertyModel fileProperty = new FileVersionPropertyModel();
                                    fileProperty.setFileId(fileModel.getId());
                                    fileProperty.setPropertyId(propertyId);
                                    fileProperty.setValue(String.valueOf(entry.getValue()));
                                    fileProperty.add();
                                }
                            }
                        }
                        JSONObject succ = new JSONObject();
                        succ.put("fileId", fileModel.getId());
                        succ.put("fileName", fileModel.getTitle());
                        succ.put("needCategory", true);
                        succ.put("needVersion", needVersion);
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
}