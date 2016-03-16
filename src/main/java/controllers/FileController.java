package controllers;

import auth.CustomUserDetails;
import exceptions.CustomSQLException;
import exceptions.NotFoundException;
import helpers.FileCheckSum;
import helpers.PEProperties;
import helpers.UserHelper;
import models.*;
import models.helpers.FileFilling;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер файлов
 */
@Controller
public class FileController {
    /**
     * Просмотр файла
     * @param id Id файла
     * @param versionId Id версии
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-view" }, method = RequestMethod.GET)
    public String view(@RequestParam("id") int id,
                       @RequestParam(value="versionId", required=false, defaultValue = "0") int versionId,
                       Principal principal,
                       Model model) {
        try {
            FileModel file = FileModel.findById(id);
            model.addAttribute("file", file);

            FileVersionModel currentVersion;
            // если id версии не указан, то берем последнюю
            if (versionId == 0) {
                currentVersion = file.getLastVersion();
            } else {
                currentVersion = FileVersionModel.findByIdAndFile(versionId, id);
            }
            model.addAttribute("currentVersion", currentVersion);

            UserModel user = UserModel.findById(currentVersion.getUserId());
            model.addAttribute("user", user);

            boolean isFileOwner = false;
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            if (user.getId() == activeUser.getEmployeeId() || UserHelper.isAdmin(activeUser)) {
                isFileOwner = true;
            }
            model.addAttribute("isFileOwner", isFileOwner);

            // список версий файла
            ArrayList versionList = file.getVersionList();
            model.addAttribute("versionList", versionList);

            // свойства файла
            ArrayList fileVersionProperties = FileVersionPropertyModel.getProperties(currentVersion.getId());
            model.addAttribute("fileVersionProperties", fileVersionProperties);

            // свойства версии
            ArrayList fileProperties = FilePropertyModel.getProperties(file.getId());
            model.addAttribute("fileProperties", fileProperties);

            // преобразование даты загрузки для вывода на страницу
            Date date = new Date(currentVersion.getDate());
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            String downloadDate = df.format(date);
            model.addAttribute("downloadDate", downloadDate);
        } catch (SQLException e) {
            throw new NotFoundException("Файл не существует");
        }

        model.addAttribute("pageTitle", "Просмотр файла");
        return "file/file-view";
    }

    /**
     * Добавление файлов
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-add" }, method = RequestMethod.GET)
    public String fileAdd(Model model) {
        model.addAttribute("pageTitle", "Добавить файл");
        return "file/file-add";
    }

    /**
     * Обработчик ajax запроса по поиску названия файла
     * @param query Строка для поиска
     * @return json строка
     */
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

    /**
     * Обработчик ajax запроса на заполнение файлов
     * @param res Массив информации для запоолнения
     * @return json строка
     */
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
            // валидация полученных данных
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
                // если ошибок в данных нет, то сохраняем информацию
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

    /**
     * Обработчик ajax запроса на загрузку файлов
     * @param files Массив файлов
     * @param request
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = {"/file-add-handler" }, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileAddHandler(@RequestParam("file[]") MultipartFile[] files, HttpServletRequest request, Principal principal) {
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
                        // к исходному имени добавляется хэш файла
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
                        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
                        fileVersion.setUserId(activeUser.getEmployeeId());
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

    /**
     * Загрузка файла с сервера
     * @param id Id нужной версии
     * @param model
     * @param request
     * @param response
     */
    @RequestMapping(value = {"/file-download" }, method = RequestMethod.GET)
    public void fileDownload(@RequestParam("id") int id, Model model, HttpServletRequest request, HttpServletResponse response) {
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
        } catch (SQLException e) {
            throw new NotFoundException("Файла не существует");
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Файла не найден");
        } catch (IOException e) {
            throw new NotFoundException("Ошибка", "500");
        }
    }
}