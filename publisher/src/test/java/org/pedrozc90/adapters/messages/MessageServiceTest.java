package org.pedrozc90.adapters.messages;

import com.google.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MessageServiceTest {

    @Inject
    protected MessageService service;

    @Test
    public void test() {
        service.send("Sanity Check");
    }

}
