package org.pedrozc90.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.pedrozc90.adapters.persistence.FileStorageRepository;
import org.pedrozc90.core.exceptions.AppException;
import org.pedrozc90.core.utils.DigestUtils;
import org.pedrozc90.core.utils.FileUtils;
import org.pedrozc90.domain.FileStorage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Optional;

@ApplicationScoped
public class FileStorageService {

    @Inject
    protected FileStorageRepository repository;

    @Inject
    protected DigestUtils digestUtils;

    @Inject
    protected FileUtils fileUtils;

    // QUERIES
    public FileStorage get(final Long id) throws AppException {
        return repository.findByIdOptional(id).orElseThrow(() -> AppException.of(Response.Status.NOT_FOUND, "File not found"));
    }

    // METHODS
    @Transactional(value = Transactional.TxType.REQUIRED)
    protected FileStorage create(final String filename, final byte[] content, String contentType, String charset, final long length) {
        assert (filename != null);
        assert (content != null);

        contentType = StringUtils.lowerCase(contentType);
        assert (contentType != null);

        charset = StringUtils.lowerCase(charset);
        assert (charset != null);

        // generate a unique hash for the file content
        final String hash = digestUtils.md5(content);

        // create FileStorage entity and store metadata in PostgreSQL
        final FileStorage fs = new FileStorage();
        fs.setHash(hash);
        fs.setFilename(filename);
        fs.setContent(content);
        fs.setContentType(contentType);
        fs.setCharset(charset);
        fs.setLength(length);

        repository.persistAndFlush(fs);

        return fs;
    }

    public FileStorage create(final String filename, final byte[] content, final String contentType, final String charset) {
        final boolean isText = fileUtils.isText(contentType) || fileUtils.isText(filename);
        final boolean isUtf8 = StringUtils.equalsIgnoreCase(charset, "UTF-8");
        if (isText || !isUtf8) {
            try {
                final byte[] contentUtf8 = fileUtils.toUTF8(content, charset);
                return create(filename, contentUtf8, contentType, "UTF-8", contentUtf8.length);
            } catch (UnsupportedEncodingException e) {
                throw AppException.of(Response.Status.INTERNAL_SERVER_ERROR, "Unable to convert filet o utf-8.");
            }
        }
        return create(filename, content, contentType, charset, content.length);
    }

    public FileStorage create(final FileUpload file) {
        try {
            if (file == null) {
                throw AppException.of(Response.Status.BAD_REQUEST, "file is missing");
            }

            final String filename = file.fileName();
            if (filename == null) {
                throw AppException.of(Response.Status.BAD_REQUEST, "filename is missing");
            }

            final byte[] content = Files.readAllBytes(file.uploadedFile());
            if (content.length == 0) {
                throw AppException.of(Response.Status.BAD_REQUEST, "file is empty");
            }

            final String contentType = file.contentType();
            final String chatset = Optional.ofNullable(file.charSet())
                .orElseGet(() -> fileUtils.charset(content));
            return create(filename, content, contentType, chatset);
        } catch (IOException e) {
            throw AppException.of(Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

}
