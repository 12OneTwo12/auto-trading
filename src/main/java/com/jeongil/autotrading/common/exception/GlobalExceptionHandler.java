package com.jeongil.autotrading.common.exception;

import com.jeongil.autotrading.utils.SenderUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SenderUtils senderUtils;

    @ExceptionHandler(JobException.class)
    public void jobException(JobException e) {
        logger.error("jobException", e);
        senderUtils.sendSlack("jobException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
    }

    @ExceptionHandler(SendMessageException.class)
    public void sendMessageException(SendMessageException e) {
        logger.error("sendMessageException", e);
        senderUtils.sendSlack("sendMessageException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
    }

    @ExceptionHandler(EncryptException.class)
    public void encryptException(EncryptException e) {
        logger.error("encryptException", e);
        senderUtils.sendSlack("encryptException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
    }

    @ExceptionHandler(RequestOrderException.class)
    public void requestOrderException(RequestOrderException e){
        logger.error("requestOrderException", e);
        senderUtils.sendSlack("requestOrderException : " + e.getMessage() + "| 실행 시각 : " + LocalDateTime.now().toString());
    }
}
