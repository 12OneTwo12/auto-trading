package com.jeongil.autotrading.service;

import java.io.IOException;
import java.text.ParseException;

public interface TaskletService {
    public void process() throws ParseException, IOException;
}
