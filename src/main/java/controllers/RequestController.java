package controllers;

import auth.CustomUserDetails;
import config.Settings;
import exceptions.ForbiddenException;
import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.FileHelper;
import helpers.PEProperties;
import helpers.UserHelper;
import models.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.Principal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер заявок
 */
@Controller
public class RequestController {
    private static final Logger logger = Logger.getLogger(RequestController.class);

    /**
     * Список заявок от пользователя
     */
    @RequestMapping(value = {"/request-list"}, method = RequestMethod.GET)
    public String requests(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Principal principal,
            Model model
    ) {
        int limit = RequestModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        UserModel user = UserModel.findById(activeUser.getEmployeeId());
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        int pageCount = 0;
        ArrayList<RequestModel> requests = null;
        if (UserHelper.isModerator(activeUser)) {
            // для модератора выбираем все заявки
            requests = RequestModel.findAll(limit, offset);
            pageCount = (int) Math.ceil((float)RequestModel.getCount() / limit);
        } else {
            // для пользователя выбираем только его заявки
            requests = RequestModel.findAllByUser(user.getId(), limit, offset);
            pageCount = (int) Math.ceil((float)RequestModel.getCountForUser(user.getId()) / limit);
        }
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("requests", requests);

        // количество необработанных заявок текущего пользователя
        int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
        model.addAttribute("requestCount", requestCount);

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Заявки");
        return "request/requests";
    }

    /**
     * Просмотр заявки
     */
    @RequestMapping(value = {"/request-view"}, method = RequestMethod.GET)
    public String requestView(
            @RequestParam int requestId,
            Principal principal,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        RequestModel requestModel = RequestModel.findById(requestId);
        UserModel user = null;
        boolean isModerator = false;
        if (requestModel.getUserId() != activeUser.getEmployeeId()) {
            if (UserHelper.isModerator(activeUser)) {
                isModerator = true;
                user = UserModel.findById(requestModel.getUserId());
                if (user == null) {
                    throw new NotFoundException("Пользователь не найден");
                }
            } else {
                logger.warn("Попытка просмотра чужой заявки /request-view без прав модератора; служебный номер - "+activeUser.getEmployeeId());
                throw new ForbiddenException("Доступ запрещен");
            }
        } else {
            if (UserHelper.isModerator(activeUser)) {
                isModerator = true;
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("requestModel", requestModel);

        ArrayList<RequestFileModel> files = requestModel.getFiles();
        model.addAttribute("files", files);

        Date date = new Date(requestModel.getDate());
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String requestDate = df.format(date);
        model.addAttribute("requestDate", requestDate);

        int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
        model.addAttribute("requestCount", requestCount);

        model.addAttribute("isModerator", isModerator);
        model.addAttribute("pageTitle", "Просмотр заявки");
        return "request/request-view";
    }

    /**
     * Добавить заявку
     */
    @RequestMapping(value = {"/request-add"}, method = RequestMethod.GET)
    public String requestAdd(
            Principal principal,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        UserModel user = UserModel.findById(activeUser.getEmployeeId());
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        int pageCount = RequestModel.getCountForUser(user.getId());
        model.addAttribute("pageCount", pageCount);

        int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
        model.addAttribute("requestCount", requestCount);

        model.addAttribute("pageTitle", "Добавить заявку");
        return "request/request-add";
    }

    @RequestMapping(value = {"/request-add-handler" }, method = RequestMethod.POST)
    public String fileExportHandler(
            @RequestParam String text,
            @RequestParam("file[]") MultipartFile[] files,
            HttpServletRequest request,
            RedirectAttributes attr,
            Principal principal
    ) {
        try {
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            UserModel user = UserModel.findById(activeUser.getEmployeeId());

            HashMap<String, String> errors = new HashMap<String, String>();
            if (text.trim().length() == 0) {
                errors.put("text", "Заполните текст заявки");
            }
            if (files.length == 0) {
                errors.put("file", "Добавить файлы");
            }

            RequestModel requestModel = new RequestModel();
            requestModel.setStatus(RequestModel.NEW);
            long time = new Date().getTime();
            requestModel.setDate(time);
            requestModel.setText(text);
            requestModel.setUserId(user.getId());

            String uploadPath = Settings.getRequestUploadPath();
            ArrayList<String> names = new ArrayList<String>();
            ArrayList<String> hashes = new ArrayList<String>();
            // проверка файлов на дублирование
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String uploadedFileName = new String(file.getOriginalFilename().getBytes("ISO-8859-1"), "UTF-8");
                    InputStream inputStream = file.getInputStream();
                    // формирование пути до файла
                    String hash = FileHelper.getHash(inputStream);
                    inputStream.close();

                    names.add(uploadedFileName);
                    hashes.add(hash);

                    // проверяем дублирование файла
                    if (FileVersionModel.isExist(hash, file.getSize())) {
                        errors.put("file", "Файл "+uploadedFileName+" уже существует");
                    } else if (RequestFileModel.isExist(hash, file.getSize())) {
                        errors.put("file", "Заявка с файлом "+uploadedFileName+" уже существует");
                    }
                }
            }

            // если ошибок не было, то сохраняем
            if (errors.size() == 0) {
                // сохраняем заявку в БД
                requestModel.add();

                int counter = 0;
                for (MultipartFile file : files) {
                    String hash = hashes.get(counter);
                    String uploadedFileName = names.get(counter);
                    String firstDir = hash.substring(0, 2);
                    String secondDir = hash.substring(2, 4);
                    StringBuilder newFileName = new StringBuilder();
                    newFileName
                            .append(uploadPath)
                            .append(File.separator)
                            .append(firstDir)
                            .append(File.separator)
                            .append(secondDir);
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
                        fileName.append(".txt");
                    }
                    newFileName.append(File.separator).append(fileName);

                    // сохранение файла на диск
                    InputStream inputStream = file.getInputStream();
                    FileHelper.encodeBase64(inputStream, newFileName.toString());
                    inputStream.close();
                    file.getInputStream().close();

                    // сохранение файла заявки в БД
                    RequestFileModel fileModel = new RequestFileModel();
                    fileModel.setRequestId(requestModel.getId());
                    fileModel.setHash(hash);
                    fileModel.setFileName(fileName.toString());
                    fileModel.setFileSize(file.getSize());
                    fileModel.setExtension(extension);
                    fileModel.add();
                    counter++;
                }
                logger.info("Добавлена заявка, id="+requestModel.getId()+"; служебный номер - " + activeUser.getEmployeeId());
                return "redirect:/request-view?requestId="+requestModel.getId();
            } else{
                attr.addFlashAttribute("errors", errors);
                attr.addFlashAttribute("text", text);
                return "redirect:/request-add";
            }
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(e.getMessage());
        } catch (IOException e) {
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Страница отклонение заявки
     */
    @RequestMapping(value = {"/request-cancel"}, method = RequestMethod.GET)
    public String requestCancel(
            @RequestParam int requestId,
            Principal principal,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка отклонения заявки (/request-cancel) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        RequestModel request = RequestModel.findById(requestId);
        model.addAttribute("request", request);

        model.addAttribute("pageTitle", "Отклонить заявку");
        return "request/request-cancel";
    }

    /**
     * Обработчик отклонения заявки
     */
    @RequestMapping(value = {"/request-cancel-handler"}, method = RequestMethod.POST)
    public String requestCancelHandler(
            @RequestParam int requestId,
            @RequestParam String comment,
            Principal principal,
            RedirectAttributes attr,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка отклонения заявки (/request-cancel-handler) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        RequestModel request = RequestModel.findById(requestId);
        request.setComment(comment);
        request.setStatus(RequestModel.CANCELED);
        try {
            if (request.update()) {
                return "redirect:/request-view?requestId="+request.getId();
            } else {
                attr.addFlashAttribute("errors", true);
                return "redirect:/request-cancel?requestId="+request.getId();
            }
        } catch (SQLException e) {
            throw new InternalException("Ошибка при сохранении");
        }
    }

    /**
     * Обработчик удаления заявки
     */
    @RequestMapping(value = {"/request-delete"}, method = RequestMethod.GET)
    public String requestDelete(
            @RequestParam int requestId,
            Principal principal,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка удаления заявки (/request-delete) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        RequestModel request = RequestModel.findById(requestId);
        try {
            if (request.delete()) {
                return "redirect:/request-list";
            } else {
                attr.addFlashAttribute("errors", true);
                return "redirect:/request-view?requestId="+request.getId();
            }
        } catch (SQLException e) {
            throw new InternalException("Ошибка при удалении");
        }
    }

    /**
     * Страница принятия заявки
     */
    @RequestMapping(value = {"/request-accept"}, method = RequestMethod.GET)
    public String requestAccept(
            @RequestParam int requestId,
            Principal principal,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка принятия заявки (/request-accept) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        RequestModel request = RequestModel.findById(requestId);
        model.addAttribute("request", request);

        model.addAttribute("pageTitle", "Принять заявку");
        return "request/request-accept";
    }

    /**
     * Обработчик принятия заявки
     */
    @RequestMapping(value = {"/request-accept-handler"}, method = RequestMethod.POST)
    public String requestAcceptHandler(
            @RequestParam int requestId,
            @RequestParam String comment,
            Principal principal,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка принятия заявки (/request-accept-handler) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        RequestModel request = RequestModel.findById(requestId);
        request.setComment(comment);
        request.setStatus(RequestModel.ACCEPTED);
        try {
            if (request.update()) {
                ArrayList<RequestFileModel> files = request.getFiles();
                String catalogDirectory = Settings.getUploadPath();
                String requestDirectory = Settings.getRequestUploadPath();
                for (RequestFileModel file : files) {
                    String firstDir = file.getHash().substring(0, 2);
                    String secondDir = file.getHash().substring(2, 4);
                    StringBuilder requestFile = new StringBuilder();
                    requestFile
                            .append(requestDirectory)
                            .append(File.separator)
                            .append(firstDir)
                            .append(File.separator)
                            .append(secondDir)
                            .append(File.separator)
                            .append(file.getFileName());
                    StringBuilder catalogFile = new StringBuilder();
                    catalogFile
                            .append(catalogDirectory)
                            .append(File.separator)
                            .append(firstDir)
                            .append(File.separator)
                            .append(secondDir)
                            .append(File.separator);

                    // проверка существования пути до файла
                    File uploadDir = new File(String.valueOf(catalogFile));
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }

                    String resultFilename = FilenameUtils.removeExtension(file.getFileName()) + "." + file.getExtension();
                    String resultFilePath = catalogFile.toString() + resultFilename;
                    try {
                        FileHelper.decodeBase64(requestFile.toString(), resultFilePath);

                        // получение свойств файла
                        Map<Integer, String> properties = null;
                        try {
                            properties = PEProperties.parse(resultFilePath);
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
                        fileVersion.setFileName(resultFilename);
                        fileVersion.setHash(file.getHash());
                        fileVersion.setIsFilled(isFilled);
                        fileVersion.setUserId(activeUser.getEmployeeId());
                        long time = new Date().getTime();
                        fileVersion.setDate(time);
                        if (versionValue != null && !versionValue.trim().equals("")) {
                            fileVersion.setVersion(versionValue);
                        }
                        fileVersion.setFileSize(file.getFileSize());
                        if (fileVersion.add()) {
                            logger.info("Добавлена версия из заявки, id=" + fileVersion.getId()+"; служебный номер - " + activeUser.getEmployeeId());
                        } else {
                            logger.error("Ошибка при добавлении версии из заявки; служебный номер - " + activeUser.getEmployeeId());
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
                                        logger.info("Версии id=" + fileVersion.getId()+" добавлено новое свойство id="+propertyId+", значение - "+fileProperty.getValue()+"; служебный номер - " + activeUser.getEmployeeId());
                                    } else {
                                        logger.error("Ошибка при добавлении свойства id"+propertyId+" версии id="+fileVersion.getId()+"; служебный номер - " + activeUser.getEmployeeId());
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new InternalException(e.getMessage());
                    }
                }
                return "redirect:/request-view?requestId="+request.getId();
            } else {
                attr.addFlashAttribute("errors", true);
                return "redirect:/request-accept?requestId="+request.getId();
            }
        } catch (SQLException e) {
            throw new InternalException("Ошибка при сохранении");
        }
    }
}
