package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.FileHelper;
import helpers.UserHelper;
import models.*;
import org.apache.commons.io.FilenameUtils;
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

/**
 * Контроллер заявок
 */
@Controller
public class RequestController {
    /**
     * Список заявок от пользователя
     */
    @RequestMapping(value = {"/request-list"}, method = RequestMethod.GET)
    public String requests(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Principal principal,
            Model model
    ) {
        try {
            int limit = RequestModel.PAGE_COUNT;
            int offset = (page - 1) * limit;

            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            UserModel user = UserModel.findById(activeUser.getEmployeeId());

            int pageCount = (int) Math.ceil((float)RequestModel.getCountForUser(user.getId()) / limit);
            model.addAttribute("pageCount", pageCount);

            ArrayList<RequestModel> requests = null;
            if (UserHelper.isModerator(activeUser)) {
                requests = RequestModel.findAll(limit, offset);
            } else {
                requests = RequestModel.findAllByUser(user.getId(), limit, offset);
            }
            model.addAttribute("requests", requests);

            int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
            model.addAttribute("requestCount", requestCount);

            model.addAttribute("page", page);
            model.addAttribute("pageTitle", "Мои заявки");
            return "request/requests";
        } catch (SQLException e) {
            throw new NotFoundException("Пользователь не найден");
        }
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
        try {
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            RequestModel requestModel = RequestModel.findById(requestId);
            UserModel user = null;
            boolean isModerator = false;
            if (requestModel.getUserId() != activeUser.getEmployeeId()) {
                if (UserHelper.isModerator(activeUser)) {
                    isModerator = true;
                    user = UserModel.findById(requestModel.getUserId());
                } else {
                    throw new ForbiddenException("Доступ запрещен");
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
        } catch (SQLException e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    /**
     * Добавить заявку
     */
    @RequestMapping(value = {"/request-add"}, method = RequestMethod.GET)
    public String requestAdd(
            Principal principal,
            Model model
    ) {
        try {
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            UserModel user = UserModel.findById(activeUser.getEmployeeId());

            int pageCount = RequestModel.getCountForUser(user.getId());
            model.addAttribute("pageCount", pageCount);

            int requestCount = RequestModel.getNewCountForUser(activeUser.getEmployeeId());
            model.addAttribute("requestCount", requestCount);

            model.addAttribute("pageTitle", "Добавить заявку");
            return "request/request-add";
        } catch (SQLException e) {
            throw new NotFoundException("Пользователь не найден");
        }
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

            RequestModel requestModel = new RequestModel();
            requestModel.setStatus(RequestModel.NEW);
            long time = new Date().getTime();
            requestModel.setDate(time);
            requestModel.setText(text);
            requestModel.setUserId(user.getId());

            String uploadPath = request.getServletContext().getRealPath("requests");
            File uploadRootDir = new File(uploadPath);
            // Создаем основную директорию, если ее нет
            if (!uploadRootDir.exists()) {
                uploadRootDir.mkdirs();
            }
            ArrayList<String> names = new ArrayList<String>();
            ArrayList<String> hashes = new ArrayList<String>();
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
                    if (FileModel.isExist(hash, file.getSize())) {
                        errors.put("file", "Файл "+uploadedFileName+" уже существует");
                    } else if (RequestFileModel.isExist(hash, file.getSize())) {
                        errors.put("file", "Заявка с файлом "+uploadedFileName+" уже существует");
                    }
                }
            }

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
                            .append(uploadRootDir.getAbsolutePath())
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
}
