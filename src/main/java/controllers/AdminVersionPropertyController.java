package controllers;

import exceptions.CustomWebException;
import models.FilePropertyModel;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * Контроллер для версий файлов
 */
@Controller
@RequestMapping("/admin")
public class AdminVersionPropertyController {
    @RequestMapping(value = {"/file-version-property-add" }, method = RequestMethod.GET)
    public String fileAddProperty(@RequestParam("id") int id, Model model) {
        // TODO
        return "admin/file-version-property/file-version-property-add";
    }

    @RequestMapping(value = {"/file-version-property-edit" }, method = RequestMethod.GET)
    public String fileEditProperty(@RequestParam("id") int id, Model model) {
        // TODO
        return "admin/file-version-property/file-version-property-edit";
    }

    @ResponseBody
    @RequestMapping(value = {"/file-version-property-delete"}, method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public String filePropertyDelete(@RequestParam("propertyLink") int propertyLink) {
        JSONObject result = new JSONObject();
        // TODO
        return result.toJSONString();
    }

    @RequestMapping(value = {"/file-version-property-handler" }, method = RequestMethod.POST)
    public String fileAddPropertyHandler(
            @RequestParam("versionId") int fileId,
            @RequestParam("propertyId") int propertyId,
            @RequestParam("value") String value,
            @RequestParam(value="id", required=false, defaultValue = "0") int id
    ) {
        if (id == 0) {
            // TODO: add property
        } else {
            // TODO: edit property
        }
        return "admin/file-version-property/file-property-add?id="+fileId;
    }
}
