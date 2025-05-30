package org.pedrozc90.adapters.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.pedrozc90.domain.FileStorage;

import java.util.Optional;

@ApplicationScoped
public class FileStorageRepository implements PanacheRepositoryBase<FileStorage, Long> {

    @Inject
    protected EntityManager em;

    public Optional<FileStorage> getByHash(final String hash) {
        final Parameters params = Parameters.with("hash", hash);
        return find("hash = :hash", params).firstResultOptional();
    }

}
