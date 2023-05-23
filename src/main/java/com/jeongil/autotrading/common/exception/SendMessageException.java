package com.jeongil.autotrading.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class SendMessageException extends RuntimeException{

    public SendMessageException(BindingResult bindingResult) {
        super(bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    public SendMessageException(String message) {
        super(message);
    }

    public static SendMessageException ofError(String error) {
        return new SendMessageException(error);
    }
}
