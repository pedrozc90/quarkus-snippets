package org.pedrozc90.core.utils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

@ApplicationScoped
public class FileUtils {

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

}
