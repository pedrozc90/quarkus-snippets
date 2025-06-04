package org.pedrozc90.adapters.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.pedrozc90.core.utils.JsonUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class MessageService {

    private static final Logger logger = Logger.getLogger(MessageService.class);

    @Inject
    protected RabbitMQClient client;

    @Inject
    protected JsonUtils jsonUtils;

    private Connection connection;
    private Channel channel;

    // LIFE CYCLE
    public void onStart(@Observes final StartupEvent event) {
        setupQueues();
    }

    public void onStop(@Observes final ShutdownEvent event) {
        close();
    }

    // METHODS
    private void setupQueues() {
        try {
            connection = client.connect();
            channel = connection.createChannel();

            channel.confirmSelect();

            // setup exchange: router that receives messages from producers and routes them to queues
            channel.exchangeDeclare("images", BuiltinExchangeType.DIRECT, true);

            // setup queue called 'sample.queue'
            channel.queueDeclare("images.queue", true, false, false, null);

            // setup bind: connects the queue 'sample.queue' to the exchange 'test' with routing key '#' (all messages)
            channel.queueBind("images.queue", "images", "images.resize");
            channel.basicQos(1);

            channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
                final String message = new String(body, StandardCharsets.UTF_8);
                logger.errorf("Message not routed: %s", message);
            });

            logger.info("Queues setup successfully");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void close() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            if (connection != null && connection.isOpen()) connection.close();
        } catch (Exception e) {
            logger.error("Error closing RabbitMQ connection", e);
        }
    }

    public void resize(final UUID uuid) {
        final MessagePayload payload = MessagePayload.builder()
            .uuid(uuid)
            .build();
        send(payload);
    }

    public void send(final Object obj) {
        try {
            final String payload = jsonUtils.toString(obj);
            send(payload.getBytes(StandardCharsets.UTF_8));
            logger.infof("Message sent: %s", payload);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object to JSON", e);
            throw new RuntimeException("Failed to serialize message into JSON", e);
        }
    }

    public void send(final byte[] bytes) {
        try {
            channel.basicPublish("images", "images.resize", true, null, bytes);
            channel.waitForConfirmsOrDie(5_000);
        } catch (InterruptedException e) {
            logger.error("Error waiting for confirmation", e);
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for confirmation", e);
        } catch (IOException e) {
            logger.error("Failed to publish message", e);
        }
    }

}
