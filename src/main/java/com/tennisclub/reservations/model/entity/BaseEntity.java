package com.tennisclub.reservations.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
@ToString
public abstract class BaseEntity {

    public static final String FIELD_ID = "id";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_DELETED = "deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    private boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
        this.modificationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modificationDate = LocalDateTime.now();
    }

    public void softDelete() {
        this.deleted = true;
    }
}
