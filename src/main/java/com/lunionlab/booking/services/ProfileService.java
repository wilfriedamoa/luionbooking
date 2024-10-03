package com.lunionlab.booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.models.ProfileModel;
import com.lunionlab.booking.repository.ProfileRepository;

@Service
public class ProfileService {
    @Autowired
    ProfileRepository profileRepository;

    public ProfileModel getProfileByEmail(String email) {
        return profileRepository.findFirstByEmailAndDeleted(email, DeletionEnum.DELETED_NO).orElse(null);
    }
}
