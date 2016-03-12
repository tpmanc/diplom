package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class CustomWebException extends RuntimeException {
    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public CustomWebException(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public CustomWebException(String message) {
        this.code = "";
        this.message = message;
    }
}
