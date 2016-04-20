package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.CommandHelper;
import helpers.UserHelper;
import models.FileModel;
import models.FileVersionModel;
import models.LogModel;
import models.helpers.ExportParam;
import models.helpers.ExportParamForUse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Контроллер экспорта файлов
 */
@Controller
public class ExportController {
    /**
     * Экспорт файла
     * @param versionId Id версии файла
     */
    @RequestMapping(value = {"/file-export" }, method = RequestMethod.GET)
    public String fileExport(@RequestParam int versionId, Principal principal, Model model, HttpServletRequest request) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка экспорта файла (/file-export) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            ArrayList<ExportParam> savedParameters = (ArrayList<ExportParam>) request.getSession().getAttribute("export"+versionId);
            model.addAttribute("savedParameters", savedParameters);

            model.addAttribute("pageTitle", "Экспорт файла - шаг 1");
            return "file/file-export-1";
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    @RequestMapping(value = {"/file-export-handler" }, method = RequestMethod.POST)
    public String fileExportHandler(
            @RequestParam("names[]") String[] names,
            @RequestParam("types[]") int[] types,
            @RequestParam("values[]") String[] values,
            @RequestParam("regexps[]") String[] regexps,
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка экспотра файла (/file-export-handler) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }
        if (names.length != values.length || names.length != types.length) {
            LogModel.addError(activeUser.getEmployeeId(), "При экспорте файла, на 1 шаге, не все поля были заполнены");
            throw new InternalException("Ошибка при обработке запроса");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            ArrayList<ExportParam> parameters = new ArrayList<ExportParam>();
            int count = names.length;
            for (int i = 0; i < count; i++) {
                ExportParam params = new ExportParam();
                params.setName(names[i]);
                params.setType(types[i]);
                if (types[i] == 2 || types[i] == 3) {
                    params.setCommands(values[i]);
                    params.setRegexp(regexps[i]);
                    // todo: execute commands and get result
                    ArrayList<String> commandResult = null;
                    if (types[i] == 2) {
                        commandResult = CommandHelper.executeLinux(values[i], regexps[i]);
                    } else if (types[i] == 3) {
                        commandResult = CommandHelper.execute(values[i]);
                    }
                    params.setVariants(commandResult);
                } else {
                    params.setValue(values[i]);
                }
                parameters.add(params);
            }

            request.getSession().setAttribute("export"+versionId, parameters);

            return "redirect:/file-export-2?versionId="+versionId;
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    /**
     * Экспорт файла - шаг 2
     * @param versionId Id версии файла
     */
    @RequestMapping(value = {"/file-export-2" }, method = RequestMethod.GET)
    public String fileExport2(
            @RequestParam int versionId,
            Principal principal,
            Model model,
            HttpServletRequest request
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка экспотра файла (/file-export-2) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            ArrayList<ExportParam> savedParameters = (ArrayList<ExportParam>) request.getSession().getAttribute("export"+versionId);
            model.addAttribute("savedParameters", savedParameters);

            model.addAttribute("parameters", savedParameters);
            model.addAttribute("pageTitle", "Экспорт файла - шаг 2");
            return "file/file-export-2";
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    @RequestMapping(value = {"/file-export-handler-2" }, method = RequestMethod.POST)
    public String fileExportHandler2(
            @RequestParam(value="names[]", required=false) String[] names,
            @RequestParam(value="values[]", required=false) String[] values,
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка экспотра файла (/file-export-handler-2) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }
        if (names != null && values != null && names.length != values.length) {
            LogModel.addError(activeUser.getEmployeeId(), "При экспорте файла, на 2 шаге, не все поля были заполнены");
            throw new InternalException("Ошибка при обработке запроса");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);

            ArrayList<ExportParam> savedParameters = (ArrayList<ExportParam>) request.getSession().getAttribute("export"+versionId);
            HashMap<String, String> errors = new HashMap<String, String>();
            // проверяем, правильные ли значения переданы
            if (names != null && values != null) {
                int count = names.length;
                for (int i = 0; i < count; i++) {
                    for (ExportParam param : savedParameters) {
                        if (param.getName().equals(names[i])) {
                            if (param.getType() == 2) {
                                boolean isFind = false;
                                for (String valueVariant : param.getVariants()) {
                                    if (valueVariant.equals(values[i])) {
                                        isFind = true;
                                    }
                                }
                                if (!isFind) {
                                    errors.put(names[i], "Недопустимое значение");
                                }
                            } else {
                                if (!param.getValue().equals(values[i])) {
                                    errors.put(names[i], "Недопустимое значение");
                                }
                            }
                        }
                    }
                }
            }

            if (errors.size() > 0) {
                attr.addFlashAttribute("errors", errors);
                return "redirect:/file-export-2?versionId="+versionId;
            } else {
                ArrayList<ExportParamForUse> parameters = new ArrayList<ExportParamForUse>();
                if (names != null && values != null) {
                    int count = names.length;
                    for (int i = 0; i < count; i++) {
                        parameters.add(new ExportParamForUse(names[i], values[i]));
                    }
                    request.getSession().setAttribute("export-use" + versionId, parameters);
                }
            }

            return "redirect:/file-export-3?versionId="+versionId;
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    /**
     * Экспорт файла - шаг 3
     * @param versionId Id версии файла
     */
    @RequestMapping(value = {"/file-export-3" }, method = RequestMethod.GET)
    public String fileExport3(
            @RequestParam int versionId,
            Principal principal,
            Model model,
            HttpServletRequest request
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка экспотра файла (/file-export-3) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            FileModel file = FileModel.findById(version.getFileId());
            model.addAttribute("file", file);

            ArrayList<ExportParamForUse> parameters = (ArrayList<ExportParamForUse>) request.getSession().getAttribute("export-use"+versionId);
            model.addAttribute("parameters", parameters);

            model.addAttribute("pageTitle", "Экспорт файла - шаг 3");
            return "file/file-export-3";
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    @RequestMapping(value = {"/file-export-handler-3" }, method = RequestMethod.POST)
    public String fileExportHandler3(
            @RequestParam String commands,
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            LogModel.addWarning(activeUser.getEmployeeId(), "Попытка экспотра файла (/file-export-handler-3) без прав администратора");
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            FileModel file = FileModel.findById(version.getFileId());

            String resultCommand = commands.replace("{title}", file.getTitle());
            resultCommand = resultCommand.replace("{version}", version.getVersion());
            ArrayList<ExportParamForUse> parameters = (ArrayList<ExportParamForUse>) request.getSession().getAttribute("export-use"+versionId);
            for (ExportParamForUse param : parameters) {
                resultCommand = resultCommand.replace("{"+param.getName()+"}", param.getValue());
            }

            // todo: execute resultCommand
            // todo: LogModel.addInfo

            return "redirect:/file-export-3?versionId="+versionId;
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }
}
