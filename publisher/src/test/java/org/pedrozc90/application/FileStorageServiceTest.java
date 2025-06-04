package org.pedrozc90.application;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pedrozc90.domain.FileStorage;
import org.pedrozc90.helpers.ResourceHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class FileStorageServiceTest {

    @Inject
    protected FileStorageService service;

    @Inject
    protected ResourceHelper helper;

    @Test
    @DisplayName("Creates new file with correct metadata and content")
    public void createFileWithContent() {
        final String text = "Sanity Check";
        final byte[] content = text.getBytes(StandardCharsets.UTF_8);
        final FileStorage fs = service.create("file.txt", content, "text/plain", "utf-8");
        assertNotNull(fs);
        assertNotNull(fs.getId());
        assertNotNull(fs.getUuid());
        assertNotNull(fs.getInsertedAt());
        assertNotNull(fs.getUpdatedAt());
        assertEquals(fs.getInsertedAt().truncatedTo(ChronoUnit.SECONDS), fs.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(0, fs.getVersion().intValue());
        assertNotNull(fs.getHash());
        assertEquals("file.txt", fs.getFilename());
        assertArrayEquals(content, fs.getContent());
        assertEquals("text/plain", fs.getContentType());
        assertEquals("utf-8", fs.getCharset());
        assertEquals(content.length, fs.getLength());
    }

    @Test
    @DisplayName("Creates new file with correct metadata and content")
    public void createImageFile() throws IOException {
        try (InputStream stream = helper.getResourceAsStream("files/black-labrador-3500x2095.jpg") ) {
            final byte[] content = stream.readAllBytes();
            final FileStorage fs = service.create("image.jpg", content, "image/jpeg", "none");
            assertNotNull(fs);
            assertNotNull(fs.getId());
            assertNotNull(fs.getUuid());
            assertNotNull(fs.getInsertedAt());
            assertNotNull(fs.getUpdatedAt());
            assertEquals(fs.getInsertedAt().truncatedTo(ChronoUnit.SECONDS), fs.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS));
            assertEquals(0, fs.getVersion().intValue());
            assertNotNull(fs.getHash());
            assertEquals("image.jpg", fs.getFilename());
            assertArrayEquals(content, fs.getContent());
            assertEquals("image/jpeg", fs.getContentType());
            assertEquals("none", fs.getCharset());
            assertEquals(content.length, fs.getLength());
            assertEquals(3500, fs.getWidth());
            assertEquals(2095, fs.getHeight());
        }
    }

}
