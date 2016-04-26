package models.helpers;

import java.util.ArrayList;

/**
 * Параметры при экспорте версии файла
 */
public class ExportParams {
    private ArrayList<ExportParam> params;
    private int templateId;
    private String templateTitle;

    public int getTemplateId() {
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
}
