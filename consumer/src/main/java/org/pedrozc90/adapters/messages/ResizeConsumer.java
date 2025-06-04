package org.pedrozc90.adapters.messages;

import com.rabbitmq.client.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.pedrozc90.application.FileStorageService;
import org.pedrozc90.core.utils.ImageUtils;
import org.pedrozc90.core.utils.JsonUtils;
import org.pedrozc90.domain.FileStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@ApplicationScoped
public class ResizeConsumer {

    private static final Logger logger = Logger.getLogger(ResizeConsumer.class);

    @Inject
    protected FileStorageService fileStorageService;

    @Inject
    protected JsonUtils jsonUtils;

    @Inject
    protected ImageUtils imageUtils;

    public Consumer create(final Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(final String consumerTag,
                                       final Envelope envelope,
                                       final AMQP.BasicProperties properties,
                                       final byte[] bytes) throws IOException {
                try {
                    final String text = new String(bytes, StandardCharsets.UTF_8);
                    logger.infof("Received: %s", text);

                    final MessagePayload payload = jsonUtils.toObject(text, MessagePayload.class);
                    resizeImage(payload);

                    // message acknowledgment - tells RabbitMQ message was successfully processed
                    channel.basicAck(
                        envelope.getDeliveryTag(),  // message identifier
                        false                       // multiple - false means ack only this message, not all unacknowledged messages
                    );
                } catch (Exception e) {
                    logger.error("Failed to process message: %s", e.getMessage(), e);

                    // reject and requeue the message
                    if (channel != null && channel.isOpen()) {
                        // message negative acknowledgment (NACK)
                        channel.basicNack(
                            envelope.getDeliveryTag(),  // message identifier
                            false,  // multiple - false means nack only this message
                            true    // requeue - true means put the message back in queue
                        );
                    }
                }
            }
        };
    }

    @Transactional
    public void resizeImage(final MessagePayload payload) {
        final Optional<FileStorage> optFs = fileStorageService.get(payload.getUuid());
        if (optFs.isEmpty()) {
            logger.errorf("File %s not found", payload.getUuid());
            return;
        }

        final FileStorage original = optFs.get();
        if (!original.isImage()) {
            logger.errorf("File %s is not an image", original.getFilename());
            return;
        }

        // parallel resize processing
        final ResizeStrategy strategy = payload.getStrategy();
        final CompletableFuture<FileStorage> preview120x160 = CompletableFuture.supplyAsync(() -> resizeImage(strategy, original, 120, 160));
        final CompletableFuture<FileStorage> preview240x320 = CompletableFuture.supplyAsync(() -> resizeImage(strategy, original, 240, 320));
        final CompletableFuture<FileStorage> preview800x600 = CompletableFuture.supplyAsync(() -> resizeImage(strategy, original, 800, 600));

        // wait for all resizes to complete
        try {
            CompletableFuture.allOf(preview120x160, preview240x320, preview800x600).join();
        } catch (CompletionException e) {
            throw new RuntimeException(e);
        }

        logger.infof("File %s resized successfully", original.getFilename());
    }

    @Transactional
    protected FileStorage resizeImage(final ResizeStrategy strategy, final FileStorage original, final int targetWidth, final int targetHeight) throws CompletionException {
        try {
            logger.infof("Resizing file %s from %dx%d to %dx%d", original.getFilename(), original.getWidth(), original.getHeight(), targetWidth, targetHeight);

            final String filename = generateResizedFilename(original.getFilename(), targetWidth, targetHeight);

            final byte[] content = imageUtils.resize(strategy, original.getContent(), targetWidth, targetHeight);

            return fileStorageService.create(filename, content, original.getContentType(), original.getCharset());
        } catch (IOException e) {
            throw new CompletionException("Failed to resize image to %dx%d".formatted(targetWidth, targetHeight), e);
        }
    }

    public String generateResizedFilename(final String filename, int targetWidth, int targetHeight) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            // No extension found
            return "%s_%dx%d".formatted(filename, targetWidth, targetHeight);
        }

        final String basename = filename.substring(0, lastDotIndex);
        final String extension = filename.substring(lastDotIndex + 1);
        return "%s_%dx%d.%s".formatted(basename, targetWidth, targetHeight, extension);
    }

}
