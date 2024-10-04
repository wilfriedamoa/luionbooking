package com.lunionlab.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lunionlab.booking.models.ProfileModel;
import java.util.Optional;
import java.util.UUID;;

public interface ProfileRepository extends JpaRepository<ProfileModel, UUID> {

    Optional<ProfileModel> findFirstByEmailAndDeleted(String email, Boolean deleted);

    Optional<ProfileModel> findFirstByIdAndDeleted(UUID id, Boolean deleted);

}