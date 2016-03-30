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
    public String filesUnfilled(@RequestParam(value="page", required=false, defaultValue = "1") int page,
                                @RequestParam(value="all", required=false, defaultValue = "0") boolean all,
                                Model model,
                                Principal principal) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser) && all) {
            throw new AccessDeniedException("Доступ запрещен");
        }

        int limit = FileModel.PAGE_COUNT;
        int offset = (page - 1) * limit;

        ArrayList<HashMap> unfilledFiles;
        String allUrl;
        int pageCount;
        if (all) {
            allUrl = "all=true";
            unfilledFiles = FileModel.findUnfilled(limit, offset);
            pageCount = (int) Math.ceil((float)FileModel.getUnfilledCount() / limit);
        } else {
            allUrl = "";
            unfilledFiles = FileModel.findUnfilled(activeUser.getEmployeeId(), limit, offset);
            pageCount = (int) Math.ceil((float)FileModel.getUnfilledCount(activeUser.getEmployeeId()) / limit);
        }
        model.addAttribute("files", unfilledFiles);
        model.addAttribute("pageCount", pageCount);


        model.addAttribute("allFiles", all);
        model.addAttribute("allUrl", allUrl);
        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Незаполненные файлы");
        return "unfilled-file/files";
    }

    /**
     * Заполнение файла
     */
    @RequestMapping(value = {"/file-filling" }, method = RequestMethod.GET)
    public String filesFilling(@RequestParam int versionId,
                               Principal principal,
                               Model model) {
        try {
            FileVersionModel fileVersion = FileVersionModel.findById(versionId);
            model.addAttribute("fileVersion", fileVersion);

            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            if (fileVersion.getUserId() != activeUser.getEmployeeId() && !UserHelper.isAdmin(activeUser)) {
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
            // todo validation
            FileVersionModel fileVersion = FileVersionModel.findById(versionId);
            FileModel file = FileModel.findByTitle(title);
            if (file == null) {
                file = new FileModel();
                file.setTitle(title);
                file.add();
            }

            // проверка прав
            CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            if (fileVersion.getUserId() != activeUser.getEmployeeId() && !UserHelper.isAdmin(activeUser)) {
                LogModel.addWarning(activeUser.getEmployeeId(), "Попытка заполнения чужого файла id=" + versionId);
                throw new AccessDeniedException("Доступ запрещен");
            }

            // если файл уже был заполнен, т.е. файл отредактировали
            if (fileVersion.isFilled()) {
                FileModel prevFileModel = FileModel.findById(fileVersion.getFileId());
                if (prevFileModel.getId() != file.getId()) {
                    ArrayList<HashMap> versionList = prevFileModel.getVersionList();
                    boolean haveMoreVersions = false;
                    for (HashMap line : versionList) {
                        if (versionId != Integer.parseInt((String) line.get("id"))) {
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
