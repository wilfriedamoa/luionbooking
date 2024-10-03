package com.lunionlab.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lunionlab.booking.models.UserModel;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findFirstByEmailAndDeleted(String email, Boolean deleted);
}
