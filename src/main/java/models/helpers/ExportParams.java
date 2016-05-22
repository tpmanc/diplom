package models.helpers;

import exceptions.InternalException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

/**
 * Параметры при экспорте версии файла
 */
public class ExportParams {
    private ArrayList<ExportParam> params;
    private Integer templateId;
    private String templateTitle;
    private String finalCommand;
    private Integer finalCommandInterpreter;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public ArrayList<ExportParam> getParams() {
        return params;
    }

    public void setParams(ArrayList<ExportParam> variants) {
        this.params = variants;
    }

    public void setParams(String json) {
        ArrayList<ExportParam> params = new ArrayList<>();
        try {
            JSONArray arr = (JSONArray)new JSONParser().parse(json);
            for (Object obj : arr) {
                ExportParam param = new ExportParam();
                Integer type = Integer.parseInt(String.valueOf(((JSONObject) obj).get("type")));
                param.setType(type);
                param.setName(String.valueOf(((JSONObject) obj).get("name")));
                if (((JSONObject) obj).get("value") != null) {
                    param.setValue(String.valueOf(((JSONObject) obj).get("value")));
                }
                if (((JSONObject) obj).get("commands") != null) {
                    param.setCommands(String.valueOf(((JSONObject) obj).get("commands")));
                }
                if (((JSONObject) obj).get("regexp") != null) {
                    param.setRegexp(String.valueOf(((JSONObject) obj).get("regexp")));
                }
                if (((JSONObject) obj).get("interpreter") != null) {
                    param.setInterpreter(Integer.parseInt(String.valueOf(((JSONObject) obj).get("interpreter"))));
                }
                params.add(param);
            }
        } catch (ParseException e) {
            // todo log
            throw new InternalException("Ошибка при чтенни json");
        }
        this.params = params;
    }

    public String getFinalCommand() {
        return finalCommand;
    }

    public void setFinalCommand(String finalCommand) {
        this.finalCommand = finalCommand;
    }

    public String getParamJson() {
        JSONArray result = new JSONArray();
        for (ExportParam param : params) {
            JSONObject obj = new JSONObject();
            obj.put("type", param.getType());
            obj.put("name", param.getName());
            if (param.getValue() != null) {
                obj.put("value", param.getValue());
            }
            if (param.getCommands() != null) {
                obj.put("commands", param.getCommands());
            }
            if (param.getRegexp() != null) {
                obj.put("regexp", param.getRegexp());
            }
            if (param.getInterpreter() > 0) {
                obj.put("interpreter", param.getInterpreter());
            }
            result.add(obj);
        }
        return result.toString();
    }

    public Integer getFinalCommandInterpreter() {
        return finalCommandInterpreter;
    }

    public void setFinalCommandInterpreter(Integer finalCommandInterpreter) {
        this.finalCommandInterpreter = finalCommandInterpreter;
    }
}
