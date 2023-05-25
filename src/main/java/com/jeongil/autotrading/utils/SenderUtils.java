package com.jeongil.autotrading.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongil.autotrading.common.exception.SendMessageException;
import com.jeongil.autotrading.common.properties.BinanceProperties;
import com.jeongil.autotrading.common.properties.SlackProperties;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@Component
public class SenderUtils {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SlackProperties slackProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected WebClient.Builder webClientBuilder;

    @Autowired
    private BinanceProperties binanceProperties;

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
            sendSlack(getErrorMessage("SendMessageException","slack.SendException"));
            throw SendMessageException.ofError("slack.SendException");
        }
    }

    /**
     * 내부 서버 통신
     *
     * @param method
     * @param uri
     * @param jsonData
     * @param responseClass
     * @param <T>
     * @return
     */
    public <T> T send(HttpMethod method, String uri, Object jsonData, T responseClass) {
        WebClient client = webClientBuilder.build();
        return retrieve(client, method, uri, jsonData, makeHeader(getHeader()), responseClass);
    }

    /**
     * Make Hashmap Header
     *
     * @return
     */
    private Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<String, String>();

        headers.put("X-MBX-APIKEY", binanceProperties.getAccessKey());

        return headers;
    }

    /**
     * 통신
     *
     * @param client
     * @param method
     * @param uri
     * @param jsonData
     * @param headers
     * @param responseClass
     * @param <T>
     * @return
     */
    private <T> T retrieve(WebClient client, HttpMethod method, String uri, Object jsonData, Consumer<HttpHeaders> headers, T responseClass) {

        System.out.println("url : " + uri);

        try {
            WebClient.RequestHeadersSpec<?> request = client.method(method)
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(jsonData);

            if (headers != null) {
                request.headers(headers);
            }

            Mono<String> jsonMono = request.exchangeToMono(response ->
                    response.bodyToMono(String.class)
            );
            String json = jsonMono.block();

            System.out.println("return json : "+json);

            T responseModel = (T) objectMapper.readValue(json, responseClass.getClass());

            logger.debug("response = {}", responseModel.toString());

            return responseModel;
        }  catch (Exception e) {
            logger.error("e", e);
            sendSlack(getErrorMessage("SendMessageException","webclient.SendException"));
            throw SendMessageException.ofError("webclient.SendException");
        }
    }

    /**
     * 내부 서버 통신
     *
     * @param method
     * @param uri
     * @param responseClass
     * @param <T>
     * @return
     */
    public <T> T sendGet(HttpMethod method, String uri, T responseClass) {
        WebClient client = webClientBuilder.build();
        return retrieveGet(client, method, uri, makeHeader(getHeader()), responseClass);
    }

    /**
     * 통신
     *
     * @param client
     * @param method
     * @param uri
     * @param headers
     * @param responseClass
     * @param <T>
     * @return
     */
    private <T> T retrieveGet(WebClient client, HttpMethod method, String uri, Consumer<HttpHeaders> headers, T responseClass) {

        System.out.println("url : " + uri);

        try {
            WebClient.RequestHeadersSpec<?> request = client.method(method)
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            if (headers != null) {
                request.headers(headers);
            }

            Mono<String> jsonMono = request.exchangeToMono(response ->
                    response.bodyToMono(String.class)
            );
            String json = jsonMono.block();

            System.out.println("return json : "+json);

            T responseModel = (T) objectMapper.readValue(json, responseClass.getClass());

            logger.debug("response = {}", responseModel.toString());

            return responseModel;
        }  catch (Exception e) {
            logger.error("e", e);
            sendSlack(getErrorMessage("SendMessageException","webclient.SendException"));
            throw SendMessageException.ofError("webclient.SendException");
        }
    }

    /**
     * 내부 서버 통신
     *
     * @param method
     * @param uri
     * @param responseClass
     * @param <T>
     * @return
     */
    public <T> List<T> sendList(HttpMethod method, String uri, T responseClass) {
        WebClient client = webClientBuilder.build();
        return retrieveList(client, method, uri, makeHeader(getHeader()), responseClass);
    }

    /**
     * 통신
     *
     * @param client
     * @param method
     * @param uri
     * @param headers
     * @param responseClass
     * @param <T>
     * @return
     */
    private <T> List<T> retrieveList(WebClient client, HttpMethod method, String uri, Consumer<HttpHeaders> headers, T responseClass) {

        System.out.println("url : " + uri);

        try {
            WebClient.RequestHeadersSpec<?> request = client.method(method)
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

            if (headers != null) {
                request.headers(headers);
            }

            Mono<String> jsonMono = request.exchangeToMono(response ->
                    response.bodyToMono(String.class)
            );
            String json = jsonMono.block();

            System.out.println("return json : "+json);

            List<T> responses = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                T response = (T) parseJsonObjectToObject(jsonObject, responseClass.getClass());
                responses.add(response);
            }

            return responses;
        }  catch (Exception e) {
            logger.error("e", e);
            sendSlack(getErrorMessage("SendMessageException","webclient.SendException"));
            throw SendMessageException.ofError("webclient.SendException");
        }
    }

    /**
     * Make http header
     *
     * @param headerMap
     * @return
     */
    private Consumer<HttpHeaders> makeHeader(Map<String, String> headerMap) {
        Consumer<HttpHeaders> headers = new Consumer<HttpHeaders>() {
            @Override
            public void accept(HttpHeaders t) {
                for (String key : headerMap.keySet()) {
                    t.add(key, headerMap.get(key));
                }
            }
        };
        return headers;
    }

    public String getErrorMessage(String jobName, String message){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        String methodName = "i don't know";

        if (stackTrace.length >= 3) {
            StackTraceElement callingMethod = stackTrace[2];
            methodName = callingMethod.getMethodName();
        }

        return jobName + " : " + message + " | 실행 시각 : " + LocalDateTime.now().toString() + " /n "
                + "method name : " + methodName;
    }

    private <T> T parseJsonObjectToObject(JSONObject jsonObject, Class<T> clazz) throws Exception {
        T object = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (jsonObject.has(fieldName)) {
                Object value = jsonObject.get(fieldName);
                field.set(object, value);
            }
        }
        return object;
    }
}
