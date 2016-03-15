package controllers;

import models.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Контроллер файлов для администратора
 */
@Controller
@RequestMapping("/admin")
public class AdminFileController {

    /**
     * Список всех файлов, разбитый по страницам
     * @param page Номер страницы
     * @param model
     * @return Путь до представления
     */
    @RequestMapping(value = {"/files"}, method = RequestMethod.GET)
    public String index(@RequestParam(value="page", required=false, defaultValue = "1") int page, Model model) {
        int limit = FileModel.PAGE_COUNT;
        int offset = (page - 1) * limit;
        try {
            // массив файлов для нужной страницы
            ArrayList<HashMap> files = FileModel.findAll(limit, offset);
            model.addAttribute("files", files);

            int pageCount = (int) Math.ceil((float)FileModel.getCount() / limit);
            model.addAttribute("pageCount", pageCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Файлы");
        return "admin/file/files";
    }
}