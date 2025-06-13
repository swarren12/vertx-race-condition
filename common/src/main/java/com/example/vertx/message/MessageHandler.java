package com.example.vertx.message;

/**
 * Handle received {@link Message Messages}.
 */
public interface MessageHandler {

    // Interface Methods

    /**
     * Dispatch a {@link Message}.
     *
     * @param message the {@link Message} to dispatch
     */
    void dispatch(Message message);

}
