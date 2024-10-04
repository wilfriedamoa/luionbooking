package com.lunionlab.booking.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lunionlab.booking.form.CreateProfileForm;
import com.lunionlab.booking.form.UpdateProfileForm;
import com.lunionlab.booking.services.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "api/profile")
public class ProfileController {
    @Autowired
    ProfileService profileService;

    @Secured("ROLE_USER")
    @PostMapping("/create")
    public Object createProfile(@Valid @RequestBody CreateProfileForm form, BindingResult result) {
        return profileService.createProfile(form, result);
    }

    @Secured("ROLE_USER")
    @PostMapping("/update/{profileId}")
    public Object updateProfile(@Valid @RequestBody UpdateProfileForm form, @PathVariable UUID profileId) {
        return profileService.updateProfile(form, profileId);
    }

}
