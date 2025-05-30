package org.pedrozc90.adapters.persistence;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pedrozc90.domain.FileStorage;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@QuarkusTest
public class FileStorageRepositoryTest {

    @Inject
    protected FileStorageRepository repository;

    @Transactional
    @Test
    @DisplayName("find by hash")
    public void getFileStorageByHash() {
        final FileStorage persisted = createFileStorage();

        final Optional<FileStorage> optFs = repository.getByHash("hash");
        assertTrue(optFs.isPresent());

        final FileStorage retrieved = optFs.get();
        assertEquals(persisted.getId(), retrieved.getId());
        assertEquals(persisted.getHash(), retrieved.getHash());
        assertEquals(persisted.getFilename(), retrieved.getFilename());
        assertEquals(persisted.getContentType(), retrieved.getContentType());
        assertEquals(persisted.getCharset(), retrieved.getCharset());
    }

    private FileStorage createFileStorage() {
        final String text = "Sanity Check";
        final byte[] content = text.getBytes(StandardCharsets.UTF_8);

        final FileStorage fs = new FileStorage();
        fs.setHash("hash");
        fs.setFilename("file.txt");
        fs.setContent(content);
        fs.setContentType("text/plain");
        fs.setCharset("utf-8");
        fs.setLength((long) content.length);

        repository.persistAndFlush(fs);

        return fs;
    }

}
