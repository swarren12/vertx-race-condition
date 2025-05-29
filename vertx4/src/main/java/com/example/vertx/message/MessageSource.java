package com.example.vertx.message;

import java.util.function.Consumer;

/**
 * A source of {@link Message Messages}.
 */
public interface MessageSource {

    // Interface Methods

    /**
     * Send a {@link Message}.
     *
     * @param text the {@link Message} text.
     */
    void send(String text);

    /**
     * Subscribe a {@link Consumer} to this {@link MessageSource}.
     *
     * @param consumer the {@link Consumer} to add
     * @return the index of the {@link Consumer}
     */
    long subscribe(MessageHandler consumer);

    /**
     * Unsubscribe a {@link Consumer}.
     *
     * @param id the index of the {@link Consumer}
     */
    void unsubscribe(long id);
}
