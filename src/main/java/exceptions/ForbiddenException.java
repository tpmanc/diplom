package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public ForbiddenException(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public ForbiddenException(String message) {
        this.code = "";
        this.message = message;
    }
}
