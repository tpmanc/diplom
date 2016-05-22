package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.InternalException;
import exceptions.NotFoundException;
import helpers.CommandHelper;
import helpers.UserHelper;
import models.ExportTemplateModel;
import models.FileModel;
import models.FileVersionModel;
import models.helpers.ExportParam;
import models.helpers.ExportParamForUse;
import models.helpers.ExportParams;
import models.helpers.ExportParamsForUse;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Контроллер экспорта файлов
 */
@Controller
public class ExportController {
    private static final Logger logger = Logger.getLogger(ExportController.class);

    /**
     * Экспорт файла - выбор шаблона или создание нового
     * @param versionId Id версии файла
     */
    @RequestMapping(value = {"/file-export-template" }, method = RequestMethod.GET)
    public String fileExportTemplate(
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request,
            Model model
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка экспорта файла (/file-export-template) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        try {
            ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
            model.addAttribute("savedParameters", savedParameters);

            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            ArrayList<ExportTemplateModel> templates = ExportTemplateModel.findAll();
            model.addAttribute("templates", templates);

            model.addAttribute("pageTitle", "Экспорт файла - выбор шаблона");
            return "file/file-export-0";
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    @RequestMapping(value = {"/file-export-template-handler" }, method = RequestMethod.POST)
    public String fileExportTemplateHandler(
            @RequestParam(value="template", required=false) Integer template,
            @RequestParam(value="templateTitle", required=false) String templateTitle,
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request,
            Model model,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка экспотра файла (/file-export-template-handler) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        if ((template == null || template == 0) && (templateTitle == null || templateTitle.trim().equals(""))) {
            attr.addFlashAttribute("error", true);
            return "redirect:/file-export-template?versionId="+versionId;
        }

        if (template != null && template > 0) {
            ExportTemplateModel templateModel;
            try {
                templateModel = ExportTemplateModel.findById(template);
            } catch (SQLException e) {
                logger.error("Ошибка при чтении шаблона (/file-export-template-handler); служебный номер - " + activeUser.getEmployeeId());
                throw new InternalException("Ошибка при чтении шаблона");
            }
            ExportParams params = new ExportParams();
            params.setTemplateId(templateModel.getId());
            params.setTemplateTitle(templateModel.getTitle());
            params.setParams(templateModel.getParameters());
            params.setFinalCommand(templateModel.getFinalCommands());
            params.setFinalCommandInterpreter(templateModel.getFinalCommandsInterpreter());
            request.getSession().setAttribute("export"+versionId, params);
        } else {
            if (ExportTemplateModel.isTitleExist(templateTitle)) {
                attr.addFlashAttribute("titleError", "Шаблон с таким названием уже существует");
                return "redirect:/file-export-template?versionId="+versionId;
            }
            ExportParams params = new ExportParams();
            params.setTemplateTitle(templateTitle);
            request.getSession().setAttribute("export"+versionId, params);
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);

            return "redirect:/file-export?versionId="+versionId;
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    /**
     * Экспорт файла
     * @param versionId Id версии файла
     */
    @RequestMapping(value = {"/file-export" }, method = RequestMethod.GET)
    public String fileExport(@RequestParam int versionId, Principal principal, Model model, HttpServletRequest request) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка экспорта файла (/file-export) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
        if (savedParameters == null) {
            return "redirect:/file-export-template?versionId="+versionId;
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            model.addAttribute("savedParameters", savedParameters);

            model.addAttribute("pageTitle", "Экспорт файла - шаг 1");
            return "file/file-export-1";
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }

    @RequestMapping(value = {"/file-export-handler" }, method = RequestMethod.POST)
    public String fileExportHandler(
            @RequestParam(value="names[]", required=false) String[] names,
            @RequestParam(value="types[]", required=false) int[] types,
            @RequestParam(value="values[]", required=false) String[] values,
            @RequestParam(value="regexps[]", required=false) String[] regexps,
            @RequestParam(value="interpreter[]", required=false) int[] interpreters,
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request,
            Model model,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка экспотра файла (/file-export-handler) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
        if (savedParameters == null) {
            return "redirect:/file-export-template?versionId="+versionId;
        }

        if (names != null && (names.length != values.length || names.length != types.length)) {
            logger.error("При экспорте файла, на 1 шаге, не все поля были заполнены; служебный номер - " + activeUser.getEmployeeId());
            throw new InternalException("Ошибка при обработке запроса");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            HashMap<String, String> errors = new HashMap<String, String>();
            ArrayList<ExportParam> parameters = new ArrayList<ExportParam>();
            int count = 0;
            if (names != null) {
                count = names.length;
            }
            int commandsCounter = 0;
            for (int i = 0; i < count; i++) {
                ExportParam params = new ExportParam();
                params.setName(names[i]);
                params.setType(types[i]);
                if (types[i] == 2 || types[i] == 3) {
                    params.setCommands(values[i]);
                    params.setRegexp(regexps[commandsCounter]);
                    ArrayList<String> commandResult = null;
                    if (types[i] == 2) {
                        try {
                            commandResult = CommandHelper.executeLinux(values[i], regexps[commandsCounter]);
                            params.setVariants(commandResult);
                        } catch (Exception ex) {
                            errors.put(names[i], ex.getMessage());
                        }
                    } else if (types[i] == 3) {
                        try {
                            commandResult = CommandHelper.executeWindows(values[i], regexps[commandsCounter], interpreters[commandsCounter]);
                            params.setVariants(commandResult);
                            params.setInterpreter(interpreters[commandsCounter]);
                        } catch (Exception ex) {
                            errors.put(names[i], ex.getMessage());
                        }
                    }
                    commandsCounter++;
                } else {
                    params.setValue(values[i]);
                }
                parameters.add(params);
            }

            savedParameters.setParams(parameters);
            request.getSession().setAttribute("export"+versionId, savedParameters);

            if (errors.size() > 0) {
                attr.addFlashAttribute("errors", errors);
                return "redirect:/file-export?versionId="+versionId;
            }

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
            logger.warn("Попытка экспотра файла (/file-export-2) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
        if (savedParameters == null) {
            return "redirect:/file-export-template?versionId="+versionId;
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

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
            logger.warn("Попытка экспотра файла (/file-export-handler-2) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
        if (savedParameters == null) {
            return "redirect:/file-export-template?versionId="+versionId;
        }

        if (names != null && values != null && names.length != values.length) {
            logger.error("При экспорте файла, на 2 шаге, не все поля были заполнены; служебный номер - " + activeUser.getEmployeeId());
            throw new InternalException("Ошибка при обработке запроса");
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);

            HashMap<String, String> errors = new HashMap<String, String>();
            // проверяем, правильные ли значения переданы
            if (names != null && values != null) {
                int count = names.length;
                for (int i = 0; i < count; i++) {
                    for (ExportParam param : savedParameters.getParams()) {
                        if (param.getName().equals(names[i])) {
                            if (param.getType() == 2 || param.getType() == 3) {
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

            ExportParamsForUse parameters = new ExportParamsForUse();
            parameters.setTemplateTitle(savedParameters.getTemplateTitle());
            parameters.setTemplateId(savedParameters.getTemplateId());
            if (savedParameters.getFinalCommand() != null) {
                parameters.setFinalCommand(savedParameters.getFinalCommand());
            }
            if (savedParameters.getFinalCommandInterpreter() != null) {
                parameters.setFinalCommandInterpreter(savedParameters.getFinalCommandInterpreter());
            }

            if (errors.size() > 0) {
                attr.addFlashAttribute("errors", errors);
                return "redirect:/file-export-2?versionId="+versionId;
            } else {
                ArrayList<ExportParamForUse> params = new ArrayList<ExportParamForUse>();
                if (names != null && values != null) {
                    int count = names.length;
                    for (int i = 0; i < count; i++) {
                        params.add(new ExportParamForUse(names[i], values[i]));
                    }

                }
                parameters.setParams(params);
            }

            request.getSession().setAttribute("export-use" + versionId, parameters);

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
            logger.warn("Попытка экспотра файла (/file-export-3) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
        if (savedParameters == null) {
            return "redirect:/file-export-template?versionId="+versionId;
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            model.addAttribute("version", version);

            FileModel file = FileModel.findById(version.getFileId());
            model.addAttribute("file", file);

            ExportParamsForUse parameters = (ExportParamsForUse) request.getSession().getAttribute("export-use"+versionId);
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
            @RequestParam Integer commandsInterpreter,
            @RequestParam int versionId,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes attr
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isAdmin(activeUser)) {
            logger.warn("Попытка экспотра файла (/file-export-handler-3) без прав администратора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }

        ExportParams savedParameters = (ExportParams) request.getSession().getAttribute("export"+versionId);
        ExportParamsForUse savedParametersForUse = (ExportParamsForUse) request.getSession().getAttribute("export-use" + versionId);
        if (savedParametersForUse == null) {
            return "redirect:/file-export-template?versionId="+versionId;
        }

        savedParameters.setFinalCommand(commands);
        savedParameters.setFinalCommandInterpreter(commandsInterpreter);
        savedParametersForUse.setFinalCommand(commands);
        savedParametersForUse.setFinalCommandInterpreter(commandsInterpreter);

        if (savedParametersForUse.getTemplateId() == null) {
            ExportTemplateModel model = new ExportTemplateModel();
            model.setTitle(savedParameters.getTemplateTitle());
            model.setParameters(savedParameters.getParamJson());
            model.setFinalCommands(savedParameters.getFinalCommand());
            model.setFinalCommandsInterpreter(savedParameters.getFinalCommandInterpreter());
            try {
                if (model.add()) {
                    savedParameters.setTemplateId(model.getId());
                    savedParametersForUse.setTemplateId(model.getId());
                } else {
                    logger.error("Ошибка при добавлении шаблона (/file-export-handler-3); служебный номер - " + activeUser.getEmployeeId());
                    throw new InternalException("Ошибка при добавлении шаблона");
                }
            } catch (SQLException e) {
                logger.error("Ошибка при добавлении шаблона (/file-export-handler-3); служебный номер - " + activeUser.getEmployeeId());
                throw new InternalException("Ошибка при добавлении шаблона");
            }
        } else {
            logger.error("Ошибка при обновлении шаблона (/file-export-handler-3); служебный номер - " + activeUser.getEmployeeId());
            ExportTemplateModel model = null;
            try {
                model = ExportTemplateModel.findById(savedParameters.getTemplateId());
            } catch (SQLException e) {
                logger.error("Ошибка при обновлении шаблона (/file-export-handler-3); служебный номер - " + activeUser.getEmployeeId());
                throw new NotFoundException("Шаблон не найден");
            }
            model.setParameters(savedParameters.getParamJson());
            model.setFinalCommands(savedParameters.getFinalCommand());
            model.setFinalCommandsInterpreter(savedParameters.getFinalCommandInterpreter());
            try {
                if (!model.update()) {
                    logger.error("Ошибка при сохранении шаблона (/file-export-handler-3); служебный номер - " + activeUser.getEmployeeId());
                    throw new InternalException("Ошибка при сохранении шаблона");
                }
            } catch (SQLException e) {
                logger.error("Ошибка при сохранении шаблона (/file-export-handler-3); служебный номер - " + activeUser.getEmployeeId());
                throw new InternalException("Ошибка при сохранении шаблона");
            }
        }

        try {
            FileVersionModel version = FileVersionModel.findById(versionId);
            FileModel file = FileModel.findById(version.getFileId());

            String resultCommand = commands.replace("{title}", file.getTitle());
            resultCommand = resultCommand.replace("{version}", version.getVersion());
            for (ExportParamForUse param : savedParametersForUse.getParams()) {
                resultCommand = resultCommand.replace("{"+param.getName()+"}", param.getValue());
            }

            HashMap<String, String> errors = new HashMap<String, String>();

            String result = null;
            try {
                if (commandsInterpreter == 1 || commandsInterpreter == 2 || commandsInterpreter == 3 || commandsInterpreter == 4) {
                    result = CommandHelper.executeWindows(resultCommand, commandsInterpreter);
                } else {
                    result = CommandHelper.executeLinux(resultCommand);
                }
            } catch (Exception e) {
                errors.put("commands", e.getMessage());
            }

            if (errors.size() > 0) {
                attr.addFlashAttribute("result", errors.get("commands"));
                return "redirect:/file-export-3?versionId="+versionId;
            }
            logger.info("Экспорт файла id="+versionId+", команды: "+commands+", результат: "+result+"; служебный номер - " + activeUser.getEmployeeId());

            attr.addFlashAttribute("result", result);
            return "redirect:/file-export-3?versionId="+versionId;
        } catch (SQLException e) {
            throw new NotFoundException("Файл не найден");
        }
    }
}
