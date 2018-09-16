package com.moha.verticles.validator;

import com.moha.entity.Transaction;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TransactionValidator {

    private TransactionValidator() {
        throw new AssertionError("Ensuring noninstantibility");
    }

    public static Transaction isValidTransactionRequest(final RoutingContext routingContext, final HttpServerResponse response) {
        JsonObject transactionJson;
        try {
            transactionJson = routingContext.getBodyAsJson();
        } catch (Exception e) {
            response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                    .end("JSON is invalid");
            return null;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(transactionJson.getString("amount"));
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                response.setStatusCode(HttpResponseStatus.UNPROCESSABLE_ENTITY.code())
                        .end("Transaction amount cannot be negative");
                return null;
            }
        } catch (Exception e) {
            response.setStatusCode(HttpResponseStatus.UNPROCESSABLE_ENTITY.code())
                    .end("Transaction amount is not parseable");
            return null;
        }

        ZonedDateTime timestamp;
        try {
            timestamp = ZonedDateTime.parse(transactionJson.getString("timestamp"));
            if (timestamp.compareTo(ZonedDateTime.now()) > 0) {
                response.setStatusCode(HttpResponseStatus.UNPROCESSABLE_ENTITY.code())
                        .end("Transaction date cannot be future");
                return null;
            }
        } catch (Exception e) {
            response.setStatusCode(HttpResponseStatus.UNPROCESSABLE_ENTITY.code())
                    .end("Transaction date is not parseable");
            return null;
        }

        return new Transaction(amount, timestamp);
    }
}
