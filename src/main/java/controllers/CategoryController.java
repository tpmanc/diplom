package controllers;

import auth.CustomUserDetails;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import helpers.UserHelper;
import models.CategoryModel;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Контроллер категорий для модератора
 */
@Controller
public class CategoryController {
    private static final Logger logger = Logger.getLogger(CategoryController.class);

    /**
     * Страница с деревом категорий
     */
    @RequestMapping(value = {"/categories" }, method = RequestMethod.GET)
    public String index(
            Model model,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка доступа на страницу /categories без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        try {
            ArrayList<CategoryModel> trees = CategoryModel.findAll();
            model.addAttribute("trees", trees);

            model.addAttribute("pageTitle", "Деревья категорий");
            return "category/categories";
        } catch (SQLException e) {
            throw new NotFoundException("Станица не найдена");
        }
    }

    /**
     * Обработчик ajax запроса на добавление категории
     * @param parent Id родителя; 0 - если родителя нет
     * @param title Название категории
     * @param position Позиция для сортировки
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = "/category/ajax-add-category", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String addNewCategory(
            @RequestParam("parent") int parent,
            @RequestParam("title") String title,
            @RequestParam("position") int position,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка добавления категории (/category/ajax-add-category) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();
        try {
            CategoryModel category = new CategoryModel(parent, position, title);
            if (category.add()) {
                result.put("title", title);
                result.put("id", category.getId());
                result.put("error", false);
                logger.info("Добавлена категория "+title+", id="+category.getId()+"; служебный номер - " + activeUser.getEmployeeId());
            } else {
                result.put("error", true);
                result.put("msg", category.errors);
            }
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    /**
     * Обработчик ajax запроса на переименование категории
     * @param id Id категории
     * @param title Новое название категории
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = "/category/ajax-rename", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String renameCategory(
            @RequestParam("id") int id,
            @RequestParam("title") String title,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка переименования категории (/category/ajax-rename) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();
        try {
            CategoryModel model = CategoryModel.findById(id);
            String oldTitle = model.getTitle();
            model.setTitle(title);
            model.update();
            result.put("title", title);
            result.put("error", false);
            logger.info("Категория "+oldTitle+" переименована в "+title+", id="+id+"; служебный номер - " + activeUser.getEmployeeId());
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    /**
     * Обработчик ajax запроса на изменение сортировки
     * @param id Id перемещенной категории
     * @param newParentId Id нового родителя
     * @param position Новая позиция
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = "/category/ajax-update-position", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String categoryTreeUpdatePositionUrl(
            @RequestParam("treeId") int id,
            @RequestParam("newParentId") int newParentId,
            @RequestParam("position") int position,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка изменения сортировки категорий (/category/ajax-update-position) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();
        try {
            CategoryModel model = CategoryModel.findById(id);
            if (model.getParent() != newParentId) {
                model.setParent(newParentId);
                model.update();
            }
            CategoryModel.updateSortingOfNode(newParentId, id, position);
            logger.info("Изменен порядок подкатегорий у родителя с id="+newParentId+"; служебный номер - " + activeUser.getEmployeeId());
            result.put("error", false);
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }

    /**
     * Обработчик ajax запроса на удаление категории
     * @param id Id категории
     * @return json строка
     */
    @ResponseBody
    @RequestMapping(value = "/category/ajax-delete", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String categoryTreeDeleteUrl(
            @RequestParam("id") int id,
            Principal principal
    ) {
        CustomUserDetails activeUser = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        if (!UserHelper.isModerator(activeUser)) {
            logger.warn("Попытка удаления категории (/category/ajax-delete) без прав модератора; служебный номер - "+activeUser.getEmployeeId());
            throw new ForbiddenException("Доступ запрещен");
        }
        JSONObject result = new JSONObject();
        try {
            CategoryModel model = CategoryModel.findById(id);
            String title = model.getTitle();
            model.delete();
            logger.info("Категория "+title+" удалена; служебный номер - " + activeUser.getEmployeeId());
            result.put("error", false);
        } catch (SQLException e) {
            result.put("error", true);
            result.put("msg", e.getMessage());
            e.printStackTrace();
        }
        return result.toJSONString();
    }
}
