package controllers;

import auth.CustomUserDetails;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.FileModel;
import models.FileVersionModel;
import models.LogModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Контроллер незаполненных файлов
 */
@Controller
public class UnfilledFile {
    /**
     * Список незаполненных файлов
     */
    @RequestMapping(value = {"/unfilled-files" }, method = RequestMethod.GET)
    public String filesUnfilled(
            @RequestParam(value="page", required=false, defaultValue = "1") int page,
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка просмотра незаполненных файлов (/unfilled-files) без прав модератора");
            throw new AccessDeniedException("Доступ запрещен");
        }

        int limit = FileModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        ArrayList<HashMap> unfilledFiles;
        int pageCount;

        unfilledFiles = FileModel.findUnfilled(limit, offset);
        pageCount = (int) Math.ceil((float)FileModel.getUnfilledCount() / limit);
        model.addAttribute("files", unfilledFiles);
        model.addAttribute("pageCount", pageCount);

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Незаполненные файлы");
        return "unfilled-file/files";
    }

    /**
     * Заполнение файла
     */
    @RequestMapping(value = {"/file-filling" }, method = RequestMethod.GET)
    public String filesFilling(
            @RequestParam int versionId,
            Principal principal,
            Model model
    ) {
        try {
            FileVersionModel fileVersion = FileVersionModel.findById(versionId);
            model.addAttribute("fileVersion", fileVersion);

            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            if (!UserHelper.isModerator(activeUser)) {
                LogModel.addWarning(activeUser.getEmployeeId(), "Попытка доступа на страницу заполнения файла (/file-filling) без прав модератора");
                throw new AccessDeniedException("Доступ запрещен");
            }

            FileModel file = null;
            if (fileVersion.getFileId() > 0) {
                file = FileModel.findById(fileVersion.getFileId());
            }
            model.addAttribute("file", file);

            model.addAttribute("pageTitle", "Заполнение файла");
            return "unfilled-file/file-filling";
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    /**
     * Обработчик формы по заполнению файла
     */
    @RequestMapping(value = {"/file-filling-handler" }, method = RequestMethod.POST)
    public String filesFillingHandler(@RequestParam int versionId,
                                      @RequestParam String title,
                                      @RequestParam String version,
                                      Principal principal) {
        try {
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            if (!UserHelper.isModerator(activeUser)) {
                LogModel.addWarning(activeUser.getEmployeeId(), "Попытка заполнения файла (/file-filling-handler) без прав модератора");
                throw new AccessDeniedException("Доступ запрещен");
            }

            // todo validation
            FileVersionModel fileVersion = FileVersionModel.findById(versionId);
            FileModel file = FileModel.findByTitle(title);
            if (file == null) {
                file = new FileModel();
                file.setTitle(title);
                file.add();
            }

            // если файл уже был заполнен, т.е. файл отредактировали
            if (fileVersion.isFilled()) {
                FileModel prevFileModel = FileModel.findById(fileVersion.getFileId());
                if (prevFileModel.getId() != file.getId()) {
                    ArrayList<FileVersionModel> versionList = prevFileModel.getVersionList();
                    boolean haveMoreVersions = false;
                    for (FileVersionModel line : versionList) {
                        if (versionId != line.getId()) {
                            haveMoreVersions = true;
                        }
                    }
                    // если у файла больше не осталось версий, то удаляем его
                    if (!haveMoreVersions) {
                        prevFileModel.delete();
                    }
                }
            }
            fileVersion.setFileId(file.getId());
            fileVersion.setVersion(version);
            fileVersion.setIsFilled(true);
            fileVersion.update();
            LogModel.addInfo(activeUser.getEmployeeId(), "Заполнен файл id=" + versionId);

            return "redirect:/file-view?id="+file.getId()+"&versionId="+fileVersion.getId();
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }
}
