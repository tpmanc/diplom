package controllers;

import auth.CustomUserDetails;
import exceptions.CustomSQLException;
import exceptions.CustomWebException;
import helpers.FileCheckSum;
import helpers.PEProperties;
import jdk.nashorn.internal.parser.JSONParser;
import models.*;
import models.helpers.FileFilling;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.security.Principal;
import java.security.Timestamp;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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