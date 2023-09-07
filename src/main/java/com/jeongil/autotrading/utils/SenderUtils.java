package com.jeongil.autotrading.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongil.autotrading.common.exception.SendMessageException;
import com.jeongil.autotrading.common.properties.BinanceProperties;
import com.jeongil.autotrading.common.properties.SlackProperties;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@Component
@Slf4j
public class SenderUtils {

    @Autowired
    private SlackProperties slackProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected WebClient.Builder webClientBuilder;

    @Autowired
    private BinanceProperties binanceProperties;

    public void sendSlack(String message){
        Slack slack = Slack.getInstance();
        String token = slackProperties.getToken();
        MethodsClient methods = slack.methods(token);

        // Build a request object
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel("#자동-매매") // 채널명 or 채널 ID
                .text(message)
                .build();

        // Get a response as a Java object
        try {
            ChatPostMessageResponse response = methods.chatPostMessage(request);
        } catch (Exception e) {
            throw new SendMessageException(e.getMessage());
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

        log.info("url : " + uri);

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

            log.info("return json : "+json);

            T responseModel = (T) objectMapper.readValue(json, responseClass.getClass());

            log.debug("response = {}", responseModel.toString());

            return responseModel;
        }  catch (Exception e) {
            log.error("e", e);
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

        log.info("url : " + uri);

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

            log.info("response json = {}", json);

            T responseModel = (T) objectMapper.readValue(json, responseClass.getClass());

            log.info("response = {}", ToStringBuilder.reflectionToString(responseModel));

            return responseModel;
        } catch (WebClientException e){
            log.error("e", e);
            throw SendMessageException.ofError("webclient.SendException");
        } catch (Exception e) {
            log.error("e", e);
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

        log.info("url : " + uri);

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

            List<T> responses = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                T response = (T) parseJsonObjectToObject(jsonObject, responseClass.getClass());
                responses.add(response);
            }

            return responses;
        }  catch (Exception e) {
            log.error("e", e);
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

        return jobName + " : " + message + " \n 실행 시각 : " + LocalDateTime.now().toString() + " \n "
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
