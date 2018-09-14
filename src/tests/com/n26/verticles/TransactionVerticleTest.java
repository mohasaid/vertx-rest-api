package com.n26.verticles;

import io.vertx.core.json.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionVerticleTest {

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private static final String BASE_URL = "http://localhost:8080";

    @Test
    public void givenInvalidTransactionJsonRequest_whenAddTransaction_thenReturn400() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setAll(map);

        String validTransaction = "{test_fail";

        HttpEntity<String> request = new HttpEntity<>(validTransaction, headers);

        ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("JSON is invalid", transactionResponseEntity.getBody());
    }

    @Test
    public void givenMissingAmountTransactionJsonRequest_whenAddTransaction_thenReturn422() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setAll(map);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("timestamp", "123");

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction amount is not parseable", transactionResponseEntity.getBody());
    }

    @Test
    public void givenNegativeAmountTransactionJsonRequest_whenAddTransaction_thenReturn422() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setAll(map);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("amount", "-123");

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction amount cannot be negative", transactionResponseEntity.getBody());
    }

    @Test
    public void givenMissingTimestampTransactionJsonRequest_whenAddTransaction_thenReturn422() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setAll(map);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("amount", "123");

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction date is not parseable", transactionResponseEntity.getBody());
    }

    @Test
    public void givenFutureTimestampTransactionJsonRequest_whenAddTransaction_thenReturn422() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.setAll(map);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("amount", "123");
        jsonObject.put("timestamp", ZonedDateTime.now().plus(5, ChronoUnit.SECONDS).toString());

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

        ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction date cannot be future", transactionResponseEntity.getBody());
    }

}