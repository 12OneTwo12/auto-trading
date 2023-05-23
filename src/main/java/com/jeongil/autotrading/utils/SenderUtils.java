package com.jeongil.autotrading.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongil.autotrading.common.exception.SendMessageException;
import com.jeongil.autotrading.common.properties.SlackProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class SenderUtils {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SlackProperties slackProperties;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendSlack(String message){
        try {
            URL url = new URL(slackProperties.getMessageUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            Map<String, String> body = new HashMap<>();
            body.put("text", message);
            String jsonString = objectMapper.writeValueAsString(body);

            OutputStream os = connection.getOutputStream();
            os.write(jsonString.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            throw SendMessageException.ofError("slack.SendException");
        }
    }
}
