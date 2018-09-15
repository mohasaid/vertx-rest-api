package com.n26.verticles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.entity.Transaction;
import com.n26.entity.TransactionsStatistics;
import com.n26.repository.TransactionRepository;
import com.n26.verticles.validator.TransactionValidator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class TransactionVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionVerticle.class.getName());

    private HttpServer httpServer;
    private ObjectMapper objectMapper;
    private final TransactionRepository transactionRepository;
    private static final int PORT = 8080;

    @Autowired
    public TransactionVerticle(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void start() {
        this.httpServer = vertx.createHttpServer(new HttpServerOptions().setPort(PORT));
        this.httpServer.requestHandler(createRouter()::accept).listen(PORT);
    }

    @Override
    public void stop() {
        httpServer.close();
    }

    private Router createRouter() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/transactions")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .handler(this::handleAddTransaction);

        router.get("/statistics")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .handler(this::handleGetTransactionStatistics);

        router.delete("/transactions")
                .handler(this::handleDeleteTransactions);

        return router;
    }

    private void handleAddTransaction(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();

        final Transaction transaction = TransactionValidator.isValidTransactionRequest(routingContext, response);

        if (transaction == null) {
            return;
        }

        final boolean transactionAdded = transactionRepository.addTransaction(transaction);
        response.setStatusCode(transactionAdded ? HttpResponseStatus.CREATED.code() : HttpResponseStatus.NO_CONTENT.code());
        response.end();
    }

    private void handleGetTransactionStatistics(final RoutingContext routingContext) {
        final TransactionsStatistics statistics = transactionRepository.getStatistics();

        String statisticsToJSON = "";
        try {
            statisticsToJSON = objectMapper.writeValueAsString(statistics);
        } catch (JsonProcessingException e) {
            LOG.error("There was an error while mapping transaction statistics to JSON");
        }

        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setStatusCode(HttpResponseStatus.OK.code())
                .end(statisticsToJSON);
    }

    private void handleDeleteTransactions(final RoutingContext routingContext) {
        final HttpServerResponse response = routingContext.response();

        transactionRepository.deleteTransactions();

        response.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
        response.end();
    }
}
