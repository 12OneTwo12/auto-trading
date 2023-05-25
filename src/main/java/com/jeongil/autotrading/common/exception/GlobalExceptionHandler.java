package com.jeongil.autotrading.common.exception;

import com.jeongil.autotrading.utils.SenderUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SenderUtils senderUtils;

    @ExceptionHandler(JobException.class)
    public void jobException(JobException e) {
        senderUtils.sendSlack("jobException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        logger.error("jobException", e);
    }

    @ExceptionHandler(SendMessageException.class)
    public void sendMessageException(SendMessageException e) {
        senderUtils.sendSlack("sendMessageException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        logger.error("sendMessageException", e);
    }

    @ExceptionHandler(EncryptException.class)
    public void encryptException(EncryptException e) {
        senderUtils.sendSlack("encryptException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        logger.error("encryptException", e);
    }

    @ExceptionHandler(RequestOrderException.class)
    public void requestOrderException(RequestOrderException e){
        senderUtils.sendSlack("requestOrderException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
        logger.error("requestOrderException", e);
    }
}
