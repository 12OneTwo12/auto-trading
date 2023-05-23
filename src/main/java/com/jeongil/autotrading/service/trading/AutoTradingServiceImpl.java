package com.jeongil.autotrading.service.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Service
public class AutoTradingServiceImpl implements AutoTradingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void process() throws ParseException, IOException {
        logger.info("배치 돌았습니다요~");
    }
}
