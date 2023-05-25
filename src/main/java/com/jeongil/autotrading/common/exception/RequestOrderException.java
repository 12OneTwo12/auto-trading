package com.jeongil.autotrading.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class RequestOrderException extends RuntimeException{

    public RequestOrderException(BindingResult bindingResult) {
        super(bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    public RequestOrderException(String message) {
        super(message);
    }

    public static RequestOrderException ofError(String error) {
        return new RequestOrderException(error);
    }
}
