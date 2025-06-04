package org.pedrozc90.adapters.messages;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pedrozc90.application.FileStorageService;
import org.pedrozc90.core.utils.ImageUtils;
import org.pedrozc90.core.utils.JsonUtils;
import org.pedrozc90.domain.FileStorage;
import org.pedrozc90.helpers.ResourceHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

@QuarkusTest
public class ResizeConsumerTest {

    @Inject
    protected ResourceHelper helper;

    @Inject
    protected FileStorageService fileStorageService;

    @Inject
    protected ResizeConsumer resizeConsumer;

    private static final UUID TEST_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Transactional
    @BeforeEach
    public void setUp() {
        fileStorageService.removeAll();
    }

    @Test
    @Transactional
    @DisplayName("Should resize image successfully")
    void shouldResizeImage() throws IOException {
        // Given
        FileStorage original = createAndStoreTestImage();
        MessagePayload payload = createMessagePayload();

        // When
        resizeConsumer.resizeImage(payload);

        // Then
        assertResizedImagesExist(original.getFilename());
    }

    @Test
    @Transactional
    @DisplayName("Should handle non-existent file gracefully")
    void shouldHandleNonExistentFile() {
        // Given
        MessagePayload payload = createMessagePayload();

        // When
        resizeConsumer.resizeImage(payload);

        // Then
        assertNoResizedImages();
    }

    @Test
    @DisplayName("Should generate correct filenames for resized images")
    void shouldGenerateCorrectFilenames() {
        final int width = 800;
        final int height = 600;

        final String filename1 = resizeConsumer.generateResizedFilename("image.jpg", width, height);
        assertEquals("image_800x600.jpg", filename1);

        final String filename2 = resizeConsumer.generateResizedFilename("complex.name.jpg", width, height);
        assertEquals("complex.name_800x600.jpg", filename2);

        final String filename3 = resizeConsumer.generateResizedFilename("noext", width, height);
        assertEquals("noext_800x600",            filename3);
    }

    private FileStorage createAndStoreTestImage() throws IOException {
        byte[] bytes = helper.getResourceAsBytes("files/black-labrador-3500x2095.jpg");

        final FileStorage fs = new FileStorage();
        fs.setUuid(TEST_UUID);
        fs.setHash(TEST_UUID.toString().substring(0, 32));
        fs.setFilename("black-labrador-3500x2095.jpg");
        fs.setContent(bytes);
        fs.setContentType("image/jpeg");
        fs.setCharset("none");
        fs.setLength((long) bytes.length);
        fs.setWidth(3500);
        fs.setHeight(2095);
        
        fileStorageService.persist(fs);

        return fs;
    }

    private byte[] getTestImageBytes() throws IOException {
        // Create a small test image or load from resources
        return Files.readAllBytes(Path.of("src/test/resources/test-image.jpg"));
    }

    private MessagePayload createMessagePayload() {
        return MessagePayload.builder()
            .uuid(TEST_UUID)
            .strategy(ResizeStrategy.MAINTAIN_ASPECT_RATIO)
            .build();
    }

    private void assertResizedImagesExist(String originalFilename) {
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        
        assertResizedImageExists(baseName + "_120x160" + ext);
        assertResizedImageExists(baseName + "_240x320" + ext);
        assertResizedImageExists(baseName + "_800x600" + ext);
    }

    private void assertResizedImageExists(final String filename) {
        Optional<FileStorage> resized = fileStorageService.getByFilename(filename);
        assertTrue(resized.isPresent(), "Resized image " + filename + " should exist");
        assertTrue(resized.get().isImage(), "Resized file should be an image");
    }

    private void assertNoResizedImages() {
        // Verify no resized images were created
        assertTrue(fileStorageService.findResizedImages(TEST_UUID).isEmpty());
    }
}