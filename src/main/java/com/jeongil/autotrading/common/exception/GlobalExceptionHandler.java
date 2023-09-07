package com.jeongil.autotrading.common.exception;

import com.jeongil.autotrading.utils.SenderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private SenderUtils senderUtils;

    @ExceptionHandler(JobException.class)
    public void jobException(JobException e) {
        senderUtils.sendSlack("jobException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        log.error("jobException", e);
    }

    @ExceptionHandler(SendMessageException.class)
    public void sendMessageException(SendMessageException e) {
        senderUtils.sendSlack("sendMessageException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        log.error("sendMessageException", e);
    }

    @ExceptionHandler(EncryptException.class)
    public void encryptException(EncryptException e) {
        senderUtils.sendSlack("encryptException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        log.error("encryptException", e);
    }

    @ExceptionHandler(RequestOrderException.class)
    public void requestOrderException(RequestOrderException e){
        senderUtils.sendSlack("requestOrderException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        log.error("requestOrderException", e);
    }
}
