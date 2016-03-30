package models.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Модель для вывода логов
 */
public class LogOutput {
    private int id;
    private int userId;
    private long date;
    private String level;
    private String message;
    private String username;

    public LogOutput(int id, int userId, long date, String level, String message ,String username) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.level = level;
        this.message = message;
        this.username = username;
    }

    public String getStringDate() {
        Date d = new Date(date);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return df.format(d);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
