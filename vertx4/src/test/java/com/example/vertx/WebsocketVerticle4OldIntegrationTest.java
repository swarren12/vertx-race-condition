package com.example.vertx;

import com.example.vertx.message.MessageSource;
import com.example.vertx.message.stub.StubMessageSource;
import com.example.vertx.verticle.WebsocketVerticle4;
import com.example.vertx.verticle.WebsocketVerticle4Old;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.WebSocketClient;
import io.vertx.core.http.WebSocketClientOptions;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class WebsocketVerticle4OldIntegrationTest {

    private final MessageSource messageSource = new StubMessageSource();

    private HttpServer httpServer;

    // Set-up & Tear-down

    @BeforeEach
    public void beforeTest(final Vertx vertx, final VertxTestContext context) {
        httpServer = vertx.createHttpServer(new HttpServerOptions().setPort(0));
        final WebsocketVerticle4Old verticle = new WebsocketVerticle4Old(messageSource, httpServer);

        final Checkpoint checkpoint = context.checkpoint();
        vertx.deployVerticle(verticle)
                .onSuccess(ok -> checkpoint.flag())
                .onFailure(context::failNow);
    }

    // Tests

    @Test
    void shouldReceiveMessageAfterConnection(final Vertx vertx, final VertxTestContext testContext) {
        final WebSocketClientOptions clientOptions = new WebSocketClientOptions().setDefaultPort(httpServer.actualPort());
        final WebSocketClient client = vertx.createWebSocketClient(clientOptions);

        final BlockingDeque<String> received = new LinkedBlockingDeque<>();
        client.connect("/")
                .onSuccess(ws -> messageSource.send("Hello World"))
                .onSuccess(ws -> ws.textMessageHandler(received::add));

        testContext.verify(() -> {
            final String message = received.poll(10, TimeUnit.SECONDS);
            assertEquals("Hello World", message);
            testContext.completeNow();
        });
    }

}
