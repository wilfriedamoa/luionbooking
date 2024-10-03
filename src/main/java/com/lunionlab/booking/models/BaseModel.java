package com.lunionlab.booking.models;

import java.util.Date;
import java.util.UUID;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.emum.StatusEnum;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
    protected Integer status;
    protected Boolean deleted;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date dateCreation;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date dateEdition;

    protected BaseModel(Integer status) {
        this.dateCreation = new Date();
        this.dateEdition = new Date();
        this.deleted = DeletionEnum.DELETED_NO;
        this.status = StatusEnum.DEFAULT_ENABLE;
    }

    protected BaseModel() {
        this(1);
    }
}
