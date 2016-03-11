package models.helpers;

import java.io.Serializable;

/**
 * Вспомогательный класс для заполнения основных свойств файла
 */
public class FileFilling implements Serializable {
    private Integer id;
    private String title;
    private String version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
