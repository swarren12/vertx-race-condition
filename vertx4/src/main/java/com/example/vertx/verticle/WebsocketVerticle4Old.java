package com.example.vertx.verticle;

import com.example.vertx.message.Message;
import com.example.vertx.message.MessageHandler;
import com.example.vertx.message.MessageSource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.ServerWebSocketHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketVerticle4Old extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Private Members

    private final MessageSource messageSource;
    private final HttpServer httpServer;

    // Constructors

    public WebsocketVerticle4Old(final MessageSource messageSource, final HttpServer httpServer) {
        this.messageSource = messageSource;
        this.httpServer = httpServer;
    }

    // Lifecycle Methods

    @Override
    public void start(final Promise<Void> startPromise) {
        httpServer
                .webSocketHandler(this::connect)
                .listen()
                .onSuccess(ok -> startPromise.complete())
                .onFailure(startPromise::fail);
    }

    @Override
    public void stop(final Promise<Void> stopPromise) {
        httpServer.close()
                .onSuccess(ok -> stopPromise.complete())
                .onFailure(stopPromise::fail);
    }

    private void connect(final ServerWebSocket ws) {
        logger.info("Websocket connection started");
        logger.info("Subscribing to `MessageSource`...");
        final long subId = messageSource.subscribe(WebsocketMessageHandler.create(ws));
        logger.info("Subscription completed: ID = {}", subId);
        logger.info("Adding closeHandler...");
        ws.closeHandler(nil -> messageSource.unsubscribe(subId));
        logger.info("Websocket connection completed!");
        ws.accept();
    }

    // Helper Classes

    /*
     * A `MessageHandler` that dispatches to a `ServerWebSocket`.
     */
    public static class WebsocketMessageHandler implements MessageHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        // Private Members

        private final ServerWebSocket ws;

        // Constructors

        WebsocketMessageHandler(final ServerWebSocket ws) {
            this.ws = ws;
        }

        // MessageHandler Methods

        @Override
        public void dispatch(final Message message) {
            logger.info("Dispatching message: {}", message.text());
            ws.writeTextMessage(message.text());
        }

        // Helper Methods

        /*
         * Create a new `WebsocketMessageHandler`.
         */
        static MessageHandler create(final ServerWebSocket ws) {
            return new WebsocketMessageHandler(ws);
        }
    }

}
