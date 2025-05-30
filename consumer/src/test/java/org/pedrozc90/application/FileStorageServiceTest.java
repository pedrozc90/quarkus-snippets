package org.pedrozc90.application;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pedrozc90.domain.FileStorage;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class FileStorageServiceTest {

    @Inject
    protected FileStorageService service;

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

}
