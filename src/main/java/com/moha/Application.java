package com.moha;

import com.moha.verticles.TransactionVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {

    @Autowired
    TransactionVerticle transactionVerticle;
    @Autowired
    Vertx vertx;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        vertx.deployVerticle(transactionVerticle);
        System.out.println("Deployment done successfully!");
    }
}
