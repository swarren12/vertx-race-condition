package com.example.vertx.message.stub;

import com.example.vertx.message.Message;
import com.example.vertx.message.MessageHandler;
import com.example.vertx.message.MessageSource;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

public class StubMessageSource implements MessageSource {

    // Private Members

    private final Set<MessageConsumer> consumers = new CopyOnWriteArraySet<>();

    private AtomicLong id = new AtomicLong(0);

    // MessageSource Methods

    @Override
    public synchronized void send(final String text) {
        final Message message = new StubMessage(text);
        consumers.forEach(c -> c.consumer.dispatch(message));
    }

    @Override
    public synchronized long subscribe(final MessageHandler consumer) {
        final long nextId = id.incrementAndGet();
        consumers.add(new MessageConsumer(nextId, consumer));
        return nextId;
    }

    @Override
    public synchronized void unsubscribe(final long id) {
        consumers.removeIf(consumer -> consumer.id == id);
    }

    // Helper Classes

    /*
     * Bundle of a `Consumer` with associated ID.
     */
    private static final class MessageConsumer {
        // Members

        private final long id;
        private final MessageHandler consumer;

        // Constructors

        MessageConsumer(long id, MessageHandler consumer) {
            this.id = id;
            this.consumer = consumer;
        }

    }
}
