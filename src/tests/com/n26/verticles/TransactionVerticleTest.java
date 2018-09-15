package com.n26.verticles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.entity.TransactionsStatistics;
import io.vertx.core.json.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.n26.TestUtils.DELTA;
import static com.n26.TestUtils.sleepUntilStatisticsAreGenerated;
import static com.n26.TransactionFactory.getInvalidTransaction;
import static com.n26.TransactionFactory.getValidTransaction;
import static com.n26.utils.RoundUtils.roundHalfUp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionVerticleTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private static final String BASE_URL = "http://localhost:8080";
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void givenInvalidTransactionJsonRequest_whenAddTransaction_thenReturnCode400() {
        final String invalidJsonTransaction = "{test_fail";

        final HttpEntity<String> request = new HttpEntity<>(invalidJsonTransaction, getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("JSON is invalid", transactionResponseEntity.getBody());
    }

    @Test
    public void givenMissingAmountTransactionJsonRequest_whenAddTransaction_thenReturnCode422() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.put("timestamp", "123");

        final HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction amount is not parseable", transactionResponseEntity.getBody());
    }

    @Test
    public void givenNegativeAmountTransactionJsonRequest_whenAddTransaction_thenReturnCode422() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.put("amount", "-123");

        final HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction amount cannot be negative", transactionResponseEntity.getBody());
    }

    @Test
    public void givenMissingTimestampTransactionJsonRequest_whenAddTransaction_thenReturnCode422() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.put("amount", "123");

        final HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction date is not parseable", transactionResponseEntity.getBody());
    }

    @Test
    public void givenFutureTimestampTransactionJsonRequest_whenAddTransaction_thenReturnCode422() {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.put("amount", "123");
        jsonObject.put("timestamp", ZonedDateTime.now().plus(5, ChronoUnit.SECONDS).toString());

        final HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), transactionResponseEntity.getStatusCodeValue());
        assertEquals("Transaction date cannot be future", transactionResponseEntity.getBody());
    }

    @Test
    public void givenPostRequest_whenReceivedValidTransaction_ThenSuccess200() throws Exception {
        final String validTransaction = objectMapper.writeValueAsString(getValidTransaction(10));
        HttpEntity<String> request = new HttpEntity<>(validTransaction, getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.CREATED.value(), transactionResponseEntity.getStatusCodeValue());
    }

    @Test
    public void givenPostRequest_whenReceivedInValidTransaction_ThenNoContent204() throws Exception {
        final String invalidTransaction = objectMapper.writeValueAsString(getInvalidTransaction(10));
        HttpEntity<String> request = new HttpEntity<>(invalidTransaction, getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), transactionResponseEntity.getStatusCodeValue());
    }

    @Test
    public void givenGetRequest_whenHavingValidTransactions_ThenReturnDefaultStatistics() {
        final ResponseEntity<TransactionsStatistics> statisticsResponseEntity =
                restTemplate.getForEntity(BASE_URL + "/statistics", TransactionsStatistics.class);

        assertEquals(HttpStatus.OK.value(), statisticsResponseEntity.getStatusCodeValue());

        final TransactionsStatistics transactionsStatistics = statisticsResponseEntity.getBody();
        assertNotNull(transactionsStatistics);
        assertEquals(0, transactionsStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), transactionsStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), transactionsStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), transactionsStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), transactionsStatistics.getSum());
    }

    @Test
    public void givenGetRequest_whenHavingValidTransactions_ThenReturnCorrectStatistics() throws JsonProcessingException, InterruptedException {
        final String validTransaction = objectMapper.writeValueAsString(getValidTransaction(10));
        HttpEntity<String> request = new HttpEntity<>(validTransaction, getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.CREATED.value(), transactionResponseEntity.getStatusCodeValue());

        sleepUntilStatisticsAreGenerated();

        final ResponseEntity<TransactionsStatistics> statisticsResponseEntity =
                restTemplate.getForEntity(BASE_URL + "/statistics", TransactionsStatistics.class);

        assertEquals(HttpStatus.OK.value(), statisticsResponseEntity.getStatusCodeValue());

        assertNotNull(statisticsResponseEntity);
        TransactionsStatistics transactionsStatistics = statisticsResponseEntity.getBody();
        assertNotNull(transactionsStatistics);
        assertEquals(1, transactionsStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getSum());
    }

    @Test
    public void givenDeleteRequest_whenHavingValidTransactions_ThenReturnCorrectCode() throws JsonProcessingException, InterruptedException {
        final String validTransaction = objectMapper.writeValueAsString(getValidTransaction(10));
        final HttpEntity<String> request = new HttpEntity<>(validTransaction, getHeaders());

        final ResponseEntity<?> transactionResponseEntity =
                restTemplate.postForEntity(BASE_URL + "/transactions", request, String.class);

        assertEquals(HttpStatus.CREATED.value(), transactionResponseEntity.getStatusCodeValue());

        sleepUntilStatisticsAreGenerated();

        final ResponseEntity<TransactionsStatistics> statisticsResponseEntity =
                restTemplate.getForEntity(BASE_URL + "/statistics", TransactionsStatistics.class);

        assertEquals(HttpStatus.OK.value(), statisticsResponseEntity.getStatusCodeValue());

        assertNotNull(statisticsResponseEntity);
        final TransactionsStatistics transactionsStatistics = statisticsResponseEntity.getBody();
        assertNotNull(transactionsStatistics);
        assertEquals(1, transactionsStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(10)), transactionsStatistics.getSum());

        final ResponseEntity<?> transactionDeleteResponseEntity =
                restTemplate.exchange(BASE_URL + "/transactions", HttpMethod.DELETE, getEmptyRequest(), String.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), transactionDeleteResponseEntity.getStatusCodeValue());

        final ResponseEntity<TransactionsStatistics> statisticsResponseEntityUpdated =
                restTemplate.getForEntity(BASE_URL + "/statistics", TransactionsStatistics.class);

        assertEquals(HttpStatus.OK.value(), statisticsResponseEntityUpdated.getStatusCodeValue());

        assertNotNull(statisticsResponseEntityUpdated);
        final TransactionsStatistics updatedStatistics = statisticsResponseEntityUpdated.getBody();
        assertNotNull(updatedStatistics);
        assertEquals(0, updatedStatistics.getCount(), DELTA);
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getAvg());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getMax());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getMin());
        assertEquals(roundHalfUp(BigDecimal.valueOf(0)), updatedStatistics.getSum());
    }

    private static HttpHeaders getHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private static HttpEntity<String> getEmptyRequest() {
        return new HttpEntity<>(getHeaders());
    }
}