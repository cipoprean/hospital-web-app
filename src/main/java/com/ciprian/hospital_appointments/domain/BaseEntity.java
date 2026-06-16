package com.ciprian.hospital_appointments.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedBy
    protected String createdBy;
    @CreatedDate
    protected LocalDateTime createdAt;
    @LastModifiedBy
    protected String updatedBy;
    @LastModifiedDate
    protected LocalDateTime updatedAt;
}
