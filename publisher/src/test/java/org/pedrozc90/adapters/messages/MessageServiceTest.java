package org.pedrozc90.adapters.messages;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MessageServiceTest {

    @Inject
    protected MessageService service;

    @Test
    @DisplayName("Send message to rabbitmq")
    public void publishRabbitmqMessage() {
        final String text = "Sanity Check";
        final byte[] bytes = text.getBytes();
        service.send(bytes);
    }

}
