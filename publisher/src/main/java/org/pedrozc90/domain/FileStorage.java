package org.pedrozc90.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "file_storage", uniqueConstraints = {
    @UniqueConstraint(name = "file_storage_uuid_ukey", columnNames = { "uuid" })
})
public class FileStorage implements Serializable {

    @Id
    @SequenceGenerator(name = "file_storage_id_seq", sequenceName = "file_storage_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_storage_id_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "uuid", columnDefinition = "UUID DEFAULT gen_random_uuid()", nullable = false)
    private UUID uuid = UUID.randomUUID();

    @CreationTimestamp
    @ColumnDefault("localtimestamp")
    @Column(name = "inserted_at", nullable = false, updatable = false)
    private Instant insertedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @NotNull
    @Column(name = "hash", length = 32, nullable = false)
    private String hash;

    @NotNull
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content", columnDefinition = "bytea", nullable = false)
    private byte[] content;

    @NotNull
    @ColumnDefault("'application/octet-stream'")
    @Column(name = "content_type", length = 64, nullable = false)
    private String contentType;

    @NotNull
    @ColumnDefault("'utf-8'")
    @Column(name = "charset", length = 16, nullable = false)
    private String charset;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "length", columnDefinition = "bigint", nullable = false)
    private Long length = 0L;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    public boolean isImage() {
        return StringUtils.startsWith(contentType, "image/");
    }

    public boolean isVideo() {
        return StringUtils.startsWith(contentType, "video/");
    }

}
