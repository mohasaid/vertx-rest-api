package com.moha.utils.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moha.entity.Transaction;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.moha.TransactionFactory.getTransaction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ZonedDateTimeSerializerTest {

    @Test
    public void givenValidTransaction_whenSerializing_thenIsInISO_8601_Format() throws JsonProcessingException {
        final ZonedDateTime transactionTimestamp = ZonedDateTime.now();
        final Transaction transaction = getTransaction(10, transactionTimestamp);

        final String transactionString = new ObjectMapper().writeValueAsString(transaction);
        final ZonedDateTime parsedTimestamp = ZonedDateTime.parse(new JsonObject(transactionString).getString("timestamp"));

        final String formattedTransactionTimestamp = DateTimeFormatter.ISO_INSTANT.format(transactionTimestamp);
        final ZonedDateTime parsedTransactionTimestamp = ZonedDateTime.parse(formattedTransactionTimestamp);

        assertNotNull(parsedTimestamp);
        assertEquals(parsedTransactionTimestamp, parsedTimestamp);
    }
}
