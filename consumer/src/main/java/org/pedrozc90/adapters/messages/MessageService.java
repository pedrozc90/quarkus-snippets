package org.pedrozc90.adapters.messages;

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

import java.io.IOException;
import java.io.UncheckedIOException;

@ApplicationScoped
public class MessageService {

    private static final Logger logger = Logger.getLogger(MessageService.class);

    @Inject
    protected RabbitMQClient client;

    @Inject
    protected ResizeConsumer consumer;

    private Connection connection;
    private Channel channel;

    // LIFE CYCLE
    public void onStart(@Observes final StartupEvent event) {
        setupQueues();
        setupReceiving();
    }

    public void onStop(@Observes final ShutdownEvent event) {
        close();
    }

    // METHODS
    private void setupQueues() {
        try {
            connection = client.connect();
            channel = connection.createChannel();

            // declares a direct exchange named "images" that's durable (survives broker restart)
            channel.exchangeDeclare("images", BuiltinExchangeType.DIRECT, true);

            // declares a queue with following parameters:
            channel.queueDeclare(
                "images.queue", // queue name
                true,              // durable - queue survives broker restart
                false,             // exclusive - false means queue can be accessed by other connections
                false,             // autoDelete - false means queue won't be deleted when last consumer unsubscribes
                null               // arguments - additional queue settings
            );

            // binds the queue to the exchange with routing key "images.resize"
            channel.queueBind("images.queue", "images", "images.resize");

            // sets quality of service - only 1 unacknowledged message at a time
            channel.basicQos(1);

            logger.info("Queues setup successfully");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setupReceiving() {
        try {
            // starts consuming messages with following parameters:
            channel.basicConsume(
                "images.queue",         // queue name
                true,                      // autoAck - true means automatic message acknowledgment
                consumer.create(channel)   // consumer that will handle messages
            );
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

}
