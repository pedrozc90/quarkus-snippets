package org.pedrozc90.adapters.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.pedrozc90.domain.FileStorage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class FileStorageRepository implements PanacheRepositoryBase<FileStorage, Long> {

    @Inject
    protected EntityManager em;

    public Optional<FileStorage> getByUUID(final UUID uuid) {
        final Parameters params = Parameters.with("uuid", uuid);
        return find("uuid = :uuid", params).firstResultOptional();
    }

    public Optional<FileStorage> getByHash(final String hash) {
        final Parameters params = Parameters.with("hash", hash);
        return find("hash = :hash", params).firstResultOptional();
    }

    public Optional<FileStorage> getByFilename(final String filename) {
        final Parameters params = Parameters.with("filename", filename);
        return find("filename = :filename", params).firstResultOptional();
    }

    public List<FileStorage> fetchByUUID(final UUID uuid) {
        final Parameters params = Parameters.with("uuid", uuid);
        return find("uuid = :uuid", params).list();
    }
}
