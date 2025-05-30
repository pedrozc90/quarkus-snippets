package org.pedrozc90.core.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.pedrozc90.adapters.web.dtos.FileStorageDto;
import org.pedrozc90.core.utils.FileUtils;
import org.pedrozc90.domain.FileStorage;

@ApplicationScoped
public class FileStorageMapper implements EntityMapper<FileStorage, FileStorageDto> {

    @Inject
    protected FileUtils utils;

    @Override
    public FileStorageDto toDto(final FileStorage entity) {
        if (entity == null) return null;
        final FileStorageDto dto = new FileStorageDto();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid());
        dto.setInsertedAt(entity.getInsertedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setVersion(entity.getVersion());
        dto.setHash(entity.getHash());
        dto.setFilename(entity.getFilename());
        dto.setContentType(entity.getContentType());
        dto.setCharset(entity.getCharset());
        dto.setLength(entity.getLength());
        dto.setSpace(utils.toPrettySize(entity.getLength()));
        return dto;
    }

    @Override
    public FileStorage toEntity(final FileStorageDto dto, final FileStorage entity) {
        assert (dto != null);
        entity.setId(dto.getId());
        entity.setUuid(dto.getUuid());
        entity.setInsertedAt(dto.getInsertedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setVersion(dto.getVersion());
        entity.setHash(dto.getHash());
        entity.setFilename(dto.getFilename());
        entity.setContentType(dto.getContentType());
        entity.setCharset(dto.getCharset());
        entity.setLength(dto.getLength());
        return entity;
    }

    @Override
    public FileStorage toEntity(final FileStorageDto dto) {
        return toEntity(dto, new FileStorage());
    }

}
