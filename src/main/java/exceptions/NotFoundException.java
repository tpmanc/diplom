package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public NotFoundException(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public NotFoundException(String message) {
        this.code = "";
        this.message = message;
    }
}
