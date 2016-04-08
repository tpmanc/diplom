package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.FileHelper;
import helpers.PEProperties;
import helpers.UserHelper;
import models.*;
import models.helpers.FileCategory;
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
import sun.plugin.cache.FileVersion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Контроллер файлов
 */
@Controller
public class FileController {
    /**
     * Список всех файлов, разбитый по страницам
     * @param page Номер страницы
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/files"}, method = RequestMethod.GET)
    public String index(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка доступа на страницу /files без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        int limit = FileModel.PAGE_COUNT;
        int offset = (page - 1) * limit;
        try {
            // массив файлов для нужной страницы
            ArrayList<FileModel> files = FileModel.findAll(limit, offset);
            model.addAttribute("files", files);

            int pageCount = (int) Math.ceil((float)FileModel.getCount() / limit);
            model.addAttribute("pageCount", pageCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Файлы");
        return "file/files";
    }
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
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
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

            if (!UserHelper.isModerator(activeUser) && currentVersion.getIsDisabled()) {
                throw new NotFoundException("Старинца не найдена");
            }

            UserModel user = UserModel.findById(currentVersion.getUserId());
            model.addAttribute("user", user);

            // список версий файла (для модератора добавляются отключенные файлы)
            if (UserHelper.isModerator(activeUser)) {
                ArrayList<FileVersionModel> versionList = file.getVersionList(false);
                model.addAttribute("versionList", versionList);
            } else {
                ArrayList<FileVersionModel> versionList = file.getVersionList(true);
                model.addAttribute("versionList", versionList);
            }

            // свойства файла
            ArrayList<FileVersionPropertyModel> fileVersionProperties = FileVersionPropertyModel.getProperties(currentVersion.getId());
            model.addAttribute("fileVersionProperties", fileVersionProperties);

            // свойства версии
            ArrayList<FilePropertyModel> fileProperties = FilePropertyModel.getProperties(file.getId());
            model.addAttribute("fileProperties", fileProperties);

            // категории файла
            ArrayList<FileCategory> fileCategories = FileCategoryModel.findByFile(file.getId());
            model.addAttribute("fileCategories", fileCategories);

            int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
            model.addAttribute("requestCount", requestCount);

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
    public String fileAdd(
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка доступа на страницу /file-add без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        model.addAttribute("pageTitle", "Добавить файл");
        return "file/file-add";
    }

    /**
     * Изменение категорий фалов
     * @return Путь до представления
     */
    @RequestMapping(value = {"/file-categories" }, method = RequestMethod.GET)
    public String fileCategories(
            @RequestParam("fileId") int fileId,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка доступа на страницу /file-categories без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        try {
            FileModel file = FileModel.findById(fileId);
            model.addAttribute("pageTitle", "Редактировать категории");

            ArrayList<CategoryModel> categories = CategoryModel.findAll();
            model.addAttribute("categories", categories);

            ArrayList<FileCategory> fileCategories = FileCategoryModel.findByFile(file.getId());
            model.addAttribute("fileCategories", fileCategories);

            model.addAttribute("fileId", fileId);
            model.addAttribute("pageTitle", "Редактировать категории");
            return "file/file-categories";
        } catch (SQLException e) {
            throw new NotFoundException("Ошибка");
        }
    }

    /**
     * Обработчик ajax запроса по поиску названия файла
     * @param query Строка для поиска
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = {"/file-title-autocomplete"}, method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String fileTitleAutocomplete(
            @RequestParam("query") String query,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка запроса /file-title-autocomplete без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.put("query", query);

        ArrayList<FileModel> res = FileModel.findTitles(query);
        for (FileModel row : res) {
            JSONObject obj = new JSONObject();
            obj.put("value", row.getTitle());
            obj.put("data", row.getId());
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
    public String fileFilling(
            @RequestBody FileFilling[] res,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка запроса /file-filling без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }

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
                    throw new InternalException(e.getMessage());
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
    public String fileAddHandler(
            @RequestParam("file[]") MultipartFile[] files,
            HttpServletRequest request,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка загрузки файлов (/file-add-handler) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }

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
                    String hash = FileHelper.getHash(inputStream);
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
                    if (FileVersionModel.isExist(hash, file.getSize())) {
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
                        fileVersion.setUserId(activeUser.getEmployeeId());
                        long time = new Date().getTime();
                        fileVersion.setDate(time);
                        if (versionValue != null && !versionValue.trim().equals("")) {
                            fileVersion.setVersion(versionValue);
                        }
                        fileVersion.setFileSize(file.getSize());
                        if (fileVersion.add()) {
                            LogModel.addInfo(activeUser.getEmployeeId(), "Загружена новая версия, id=" + fileVersion.getId());
                        } else {
                            LogModel.addError(activeUser.getEmployeeId(), "Ошибка при загрузке новой версии");
                        }

                        // добавление остальных свойств версии в БД
                        if (properties != null) {
                            for (Map.Entry entry : properties.entrySet()) {
                                int propertyId = (Integer) entry.getKey();
                                if (propertyId != PropertyModel.FILE_VERSION && propertyId != PropertyModel.PRODUCT_NAME) {
                                    FileVersionPropertyModel fileProperty = new FileVersionPropertyModel();
                                    fileProperty.setFileVersionId(fileVersion.getId());
                                    fileProperty.setPropertyId(propertyId);
                                    fileProperty.setValue(String.valueOf(entry.getValue()));
                                    if (fileProperty.add()) {
                                        LogModel.addInfo(activeUser.getEmployeeId(), "Версии id=" + fileVersion.getId()+" добавлено новое свойство id="+propertyId+", значение - "+fileProperty.getValue());
                                    } else {
                                        LogModel.addError(activeUser.getEmployeeId(), "Ошибка при добавлении свойства id"+propertyId+" версии id="+fileVersion.getId());
                                    }
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
     * Обработчик сохранения категорий файла
     */
    @RequestMapping(value = {"/file-categories-handler" }, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileCategoriesHandler(
            @RequestParam int fileId,
            @RequestParam("categoriesId[]") int[] categoriesId,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка добавления категорий к файлу (/file-categories-handler) без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        try {
            FileModel file = FileModel.findById(fileId);
            FileCategoryModel.deleteByFile(fileId);
            for (int categoryId : categoriesId) {
                CategoryModel category = CategoryModel.findById(categoryId);
                FileCategoryModel fileCategory = new FileCategoryModel();
                fileCategory.setFileId(fileId);
                fileCategory.setCategoryId(categoryId);
                if (fileCategory.add()) {
                    LogModel.addInfo(activeUser.getEmployeeId(), "Файл "+file.getTitle()+" привязан к категории "+category.getTitle());
                } else {
                    LogModel.addError(activeUser.getEmployeeId(), "Ошибка при привязке файла "+file.getTitle()+" категории "+category.getTitle());
                }
            }
            return "redirect:/file-view?id=" + file.getId();
        } catch (SQLException e) {
            throw new InternalException("Ошибка при сохранении");
        }
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

    /**
     * Обработчик ajax запроса на удаление версии
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = {"/file-version-delete"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileVersionDelete(
            @RequestParam("versionId") int versionId,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка удаления версии /file-version-delete без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();

        try {
            FileVersionModel model = FileVersionModel.findById(versionId);
            model.setIsDisabled(true);
            if (model.update()) {
                result.put("error", false);
            } else {
                result.put("error", true);
            }
            return result.toJSONString();
        } catch (SQLException e) {
            throw new NotFoundException("Версия не найдена");
        }
    }

    /**
     * Обработчик ajax запроса на восстановление версии
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = {"/file-version-recover"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileVersionRecover(
            @RequestParam("versionId") int versionId,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка восстановления версии /file-version-recover без прав модератора");
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();

        try {
            FileVersionModel model = FileVersionModel.findById(versionId);
            model.setIsDisabled(false);
            if (model.update()) {
                result.put("error", false);
            } else {
                result.put("error", true);
            }
            return result.toJSONString();
        } catch (SQLException e) {
            throw new NotFoundException("Версия не найдена");
        }
    }
}