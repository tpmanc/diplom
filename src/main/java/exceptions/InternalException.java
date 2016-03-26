package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends RuntimeException {
    private String code;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public InternalException(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public InternalException(String message) {
        this.code = "";
        this.message = message;
    }
}
