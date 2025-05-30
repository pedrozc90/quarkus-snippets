package org.pedrozc90.adapters.messages;

import com.rabbitmq.client.*;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class MessageService {

    private static final Logger logger = Logger.getLogger(MessageService.class);

    @Inject
    protected RabbitMQClient client;

    private Channel channel;

    public void onApplicationStart(@Observes final StartupEvent event) {
        setupQueues();
        setupReceiving();
    }

    private void setupQueues() {
        try (Connection connect = client.connect()) {
            channel = connect.createChannel();
            channel.exchangeDeclare("test", BuiltinExchangeType.TOPIC, true);
            channel.queueDeclare("sample.queue", true, false, false, null);
            channel.queueBind("sample.queue", "test", "#");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setupReceiving() {
        try {
            channel.basicConsume("sample.queue", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
                    final String payload = new String(body, StandardCharsets.UTF_8);
                    logger.infof("Received: %s", payload);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void send(final String message) {
        send(message.getBytes(StandardCharsets.UTF_8));
    }

    public void send(final byte[] bytes) {
        try {
            channel.basicPublish("test", "#", null, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
