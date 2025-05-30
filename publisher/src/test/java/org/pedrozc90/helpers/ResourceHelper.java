package org.pedrozc90.helpers;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@ApplicationScoped
public class ResourceHelper {

    private final ClassLoader loader;

    public ResourceHelper() {
        loader = getClass().getClassLoader();
    }

    public File getResourceFile(final String filename) throws IOException, URISyntaxException {
        final URL url = loader.getResource(filename);
        final URI uri = url.toURI();
        return new File(uri);
    }

    public String readResourceFile(final String filename) throws IOException {
        try (InputStream inputStream = getResourceAsStream(filename)) {
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        }
    }

    public InputStream getResourceAsStream(final String filename) {
        final InputStream inputStream = loader.getResourceAsStream(filename);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + filename);
        }
        return inputStream;
    }

}