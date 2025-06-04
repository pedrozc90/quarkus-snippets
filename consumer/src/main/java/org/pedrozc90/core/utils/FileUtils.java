package org.pedrozc90.core.utils;

import com.drew.imaging.FileType;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.png.PngDirectory;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final CharsetDetector detector = new CharsetDetector();

    /**
     * Guess the 'content-type' based on filename.
     *
     * @param filename -- filename
     * @return -- content-type string
     */
    public String contentType(final String filename) {
        final String contentType = URLConnection.guessContentTypeFromName(filename);
        return Optional.ofNullable(contentType).orElse(DEFAULT_CONTENT_TYPE);
    }

    /**
     * Identify the encoding charset
     *
     * @param bytes -- file content (byte[])
     * @return - charset (string)
     */
    public String charset(final byte[] bytes) {
        detector.setText(bytes);
        final CharsetMatch match = detector.detect();
        return match.getName();
    }

    public String charset(final InputStream stream) throws IOException {
        return charset(stream.readAllBytes());
    }

    /**
     * Convert bytes from one charset to another.
     *
     * @param bytes  -- source content as byte[]
     * @param source -- source charset
     * @return
     */
    public byte[] toUTF8(final byte[] bytes, final String source) throws UnsupportedEncodingException {
        final String content = new String(bytes, source);
        return content.getBytes(StandardCharsets.UTF_8);
    }

    public boolean isText(final String s) {
        if (s == null) return false;
        // check by content_type
        return s.startsWith("text/")
            // check by extension
            || s.endsWith(".csv")
            || s.endsWith(".html")
            || s.endsWith(".htm")
            || s.endsWith(".java")
            || s.endsWith(".js")
            || s.endsWith(".json")
            || s.endsWith(".md")
            || s.endsWith(".properties")
            || s.endsWith(".txt")
            || s.endsWith(".xml")
            || s.endsWith(".yml")
            || s.endsWith(".yaml");
    }

    public String toPrettySize(final long bytes) {
        if (bytes < 1024) return bytes + " bytes";

        final String[] units = { "bytes", "KB", "MB", "GB", "TB", "PB", "EB" };
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return formatter.format(size) + " " + units[unitIndex];
    }

    public Dimensions getImageDimensions(final byte[] content, final String contentType) {
        try {
            final FileType fileType = Arrays.stream(FileType.values())
                .filter(v -> StringUtils.equals(v.getMimeType(), contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported content type: " + contentType));

            final Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(content), content.length, fileType);

            // Try to get dimensions from JPEG/TIFF/PNG metadata
            for (Directory directory : metadata.getDirectories()) {
                if (directory.containsTag(ExifDirectoryBase.TAG_IMAGE_WIDTH) &&
                    directory.containsTag(ExifDirectoryBase.TAG_IMAGE_HEIGHT)) {
                    int width = directory.getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH);
                    int height = directory.getInt(ExifDirectoryBase.TAG_IMAGE_HEIGHT);
                    return new Dimensions(width, height);
                }
            }

            // If not found in EXIF, try PNG/JPEG specific directories
            Directory directory = metadata.getFirstDirectoryOfType(PngDirectory.class);
            if (directory != null) {
                int width = directory.getInt(PngDirectory.TAG_IMAGE_WIDTH);
                int height = directory.getInt(PngDirectory.TAG_IMAGE_HEIGHT);
                return new Dimensions(width, height);
            }

            directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            if (directory != null) {
                int width = directory.getInt(JpegDirectory.TAG_IMAGE_WIDTH);
                int height = directory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT);
                return new Dimensions(width, height);
            }

        } catch (IOException e) {
            logger.error("Error reading image metadata", e);
        } catch (ImageProcessingException e) {
            logger.error("Error reading image metadata", e);
        } catch (MetadataException e) {
            logger.error("Error reading image metadata", e);
        }
        return null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Dimensions {

        public int width;

        public int height;

    }

}
