package models.helpers;

import java.util.ArrayList;

/**
 * Параметры при экспорте версии файла
 */
public class ExportParam {
    private String name;
    private int type;
    private int interpreter;
    private String value;
    private ArrayList<String> variants;
    private String commands;
    private String regexp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<String> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<String> variants) {
        this.variants = variants;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public int getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(int interpreter) {
        this.interpreter = interpreter;
    }
}
