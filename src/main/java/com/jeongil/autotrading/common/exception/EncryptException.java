package com.jeongil.autotrading.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class EncryptException extends RuntimeException{

    private static final long serialVersionUID = -1314871354637859546L;

    public EncryptException(BindingResult bindingResult) {
        super(bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    public EncryptException(String message) {
        super(message);
    }

    public static EncryptException ofError(String error) {
        return new EncryptException(error);
    }
}
