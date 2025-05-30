package org.pedrozc90.core.mappers;

public interface EntityMapper<T, R> {

    R toDto(final T entity);

    T toEntity(final R dto, final T entity);

    T toEntity(final R dto);

}
