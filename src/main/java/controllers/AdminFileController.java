package controllers;

import exceptions.CustomWebException;
import helpers.FileCheckSum;
import helpers.PEProperties;
import models.*;
import org.apache.commons.io.IOUtils;
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
    public String index(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        ArrayList<HashMap> files = null;
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
        FileModel file = null;
        ArrayList fileProperties = null;
        try {
            file = FileModel.findById(id);
            fileProperties = FilePropertyModel.getProperties(file.getId());
        } catch (SQLException e) {
            throw new CustomWebException("Файл не существует");
        }
        model.addAttribute("file", file);
        model.addAttribute("fileProperties", fileProperties);
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
        model.addAttribute("pageTitle", "Добавить файл");
        //returns the view name
        return "admin/file/file-add";

    }

    @RequestMapping(value = {"/file-download" }, method = RequestMethod.GET)
    public String fileDownload(Model model) {
        model.addAttribute("pageTitle", "Добавить файл");
        //returns the view name
        return "admin/file/file-add";

    }

    @ResponseBody
    @RequestMapping(value = {"/file-add-handler" }, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String fileAddHandler(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String result;

        String uploadPath = request.getServletContext().getRealPath("upload");
        File uploadRootDir = new File(uploadPath);
        // Создаем основную директорию, если ее нет
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }

        if (!file.isEmpty()) {
            try {
                InputStream inputStream = file.getInputStream();
                // формирование пути до файла
                String hash = FileCheckSum.get(inputStream);
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
                    // TODO: file already exist
                    result = "{\"error\": \"Такой файл уже есть\"}";
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
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(newFileName.toString())));
                    IOUtils.copy(inputStream, stream);
                    stream.close();
                    inputStream.close();

                    // получение свойств файла
                    Map<Integer, String> properties = PEProperties.parse(newFileName.toString());

                    // сохранение файла в бд
                    FileModel fileModel = new FileModel();
                    String fileTitle = properties.get(PropertyModel.PRODUCT_NAME);
                    if (fileTitle != null && !fileTitle.trim().equals("")) {
                        // если в свойствах есть название и оно не пустое
                        fileModel.setTitle(fileTitle);
                    } else {
                        // иначе берем название самого файла
                        fileModel.setTitle(file.getOriginalFilename());
                    }
                    fileModel.add();

                    // добавленией новой версии файла
                    FileVersionModel fileVersion = new FileVersionModel();
                    fileVersion.setFileId(fileModel.getId());
                    fileVersion.setHash(hash);
                    String versionValue = properties.get(PropertyModel.FILE_VERSION);
                    if (versionValue != null && !versionValue.trim().equals("")) {
                        fileVersion.setVersion(versionValue);
                    }
                    fileVersion.setFileSize(file.getSize());
                    fileVersion.add();

                    // добавление остальных свойств в БД
                    for (Map.Entry entry : properties.entrySet()) {
                        int propertyId = (Integer) entry.getKey();
                        if (propertyId != 2 && propertyId != 3) {
                            FilePropertyModel fileProperty = new FilePropertyModel();
                            fileProperty.setFileId(fileModel.getId());
                            fileProperty.setPropertyId(propertyId);
                            fileProperty.setValue(String.valueOf(entry.getValue()));
                            fileProperty.add();
                        }
                    }
                    result = "{\"error\": false}";
                }
            } catch (Exception e) {
                result = "{\"error\": true, \"msg\":\"" + e.getMessage() + "\"}";
            }
        } else {
            result = "{\"error\": true, \"msg\":\"File is empty\"}";
        }
        return result;
    }

}