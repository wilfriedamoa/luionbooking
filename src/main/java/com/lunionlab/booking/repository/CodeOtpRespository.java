package com.lunionlab.booking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lunionlab.booking.models.CodeOtpModel;

public interface CodeOtpRespository extends JpaRepository<CodeOtpModel, UUID> {

    Boolean existsByCode(String code);

    Optional<CodeOtpModel> findFirstByCodeAndDeleted(String code, Boolean deleted);

}