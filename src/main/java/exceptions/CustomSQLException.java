package exceptions;

public class CustomSQLException extends RuntimeException {
    private String code;
    private String message;
    private String stackTrace;

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public CustomSQLException(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public CustomSQLException(String message) {
        this.code = "";
        this.message = message;
    }
}
