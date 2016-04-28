package models.helpers;

import java.util.ArrayList;

/**
 * Параметры при экспорте версии файла
 */
public class ExportParamsForUse {
    private ArrayList<ExportParamForUse> params;
    private Integer templateId;
    private String templateTitle;
    private String finalCommand;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public ArrayList<ExportParamForUse> getParams() {
        return params;
    }

    public void setParams(ArrayList<ExportParamForUse> variants) {
        this.params = variants;
    }

    public String getFinalCommand() {
        return finalCommand;
    }

    public void setFinalCommand(String finalCommand) {
        this.finalCommand = finalCommand;
    }
}
