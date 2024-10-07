package com.lunionlab.booking.services;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.emum.StatusEnum;
import com.lunionlab.booking.form.CreateProfileForm;
import com.lunionlab.booking.form.ProfileDocForm;
import com.lunionlab.booking.form.UpdateProfileForm;
import com.lunionlab.booking.models.ProfileModel;
import com.lunionlab.booking.models.UserModel;
import com.lunionlab.booking.repository.ProfileRepository;
import com.lunionlab.booking.repository.UserRepository;
import com.lunionlab.booking.utilities.ReportError;
import com.lunionlab.booking.utilities.Utility;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Service
@Slf4j
public class ProfileService {
    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GenericService genericService;

    public ProfileModel getProfileByEmail(String email) {
        return profileRepository.findFirstByEmailAndDeleted(email, DeletionEnum.DELETED_NO).orElse(null);
    }

    public ProfileModel getUserAuth() {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        String email = authUser.getName();
        System.out.println(email);
        return this.getProfileByEmail(email);
    }

    public Object createProfile(@Valid CreateProfileForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format de données");
            return ResponseEntity.badRequest().body(ReportError.getErrors(result));
        }
        ProfileModel profile = this.getUserAuth();
        if (profile == null) {
            String email = Utility.getUserAuthEmail();
            profile = new ProfileModel(form.getFirstName(), form.getLastName(), form.getCountry(), null,
                    Utility.dateFromString(form.getBirthDate()), email, null, null, form.getPhoneNumber(),
                    null, null, null, null);
            profile = profileRepository.save(profile);
        } else {
            profile.setFirstName(form.getFirstName());
            profile.setLastName(form.getLastName());
            profile.setCountry(form.getCountry());
            profile.setBirthDate(Utility.dateFromString(form.getBirthDate()));
            profile.setPhoneNumber(form.getPhoneNumber());
            profile = profileRepository.save(profile);
        }
        Optional<UserModel> userOpt = userRepository.findFirstByEmailAndDeleted(profile.getEmail(),
                DeletionEnum.DELETED_NO);
        userOpt.get().setStatus(StatusEnum.OLD_USER);
        userRepository.save(userOpt.get());
        log.info("creation du profile user" + " " + profile.getFirstName());
        return ResponseEntity.ok(profile);
    }

    public Object updateProfile(@Valid UpdateProfileForm form, UUID ProfileId) {
        Optional<ProfileModel> profileOpt = profileRepository.findFirstByIdAndDeleted(ProfileId,
                DeletionEnum.DELETED_NO);
        if (profileOpt.isEmpty()) {
            log.error("profile not found");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "cet profile n'existe pas", "code", "PF10"));
        }
        ProfileModel profile = profileOpt.get();

        if (form.getAddress() != null) {
            log.info("update adress");
            profile.setAddress(form.getAddress());
        }

        if (form.getBirthDate() != null) {
            log.info("update birthdate");
            profile.setBirthDate(Utility.dateFromString(form.getBirthDate()));
        }
        if (form.getCity() != null) {
            log.info("update city");
            profile.setCity(form.getCity());
        }
        if (form.getCountry() != null) {
            log.info("update country");
            profile.setCountry(form.getCountry());
        }
        if (form.getEmail() != null) {
            if (Utility.verifyEmail(form.getEmail()) == false) {
                log.error("email invalide");
                return ResponseEntity.badRequest()
                        .body(ReportError.message("message", "email invalide", "code", "E11"));
            }
            Optional<UserModel> userOpt = userRepository.findFirstByEmailAndDeleted(profile.getEmail(),
                    DeletionEnum.DELETED_NO);
            if (userOpt.isPresent()) {
                UserModel user = userOpt.get();
                user.setEmail(form.getEmail());
                userRepository.save(user);
            }
            log.info("update email");
            profile.setEmail(form.getEmail());
        }

        if (form.getGender() != null) {
            log.info("update gender");
            profile.setGender(form.getGender().toUpperCase());
        }
        if (form.getJob() != null) {
            log.info("update job");
            profile.setJob(form.getJob());
        }

        if (form.getFirstName() != null) {
            log.info("update firstname");
            profile.setFirstName(form.getFirstName());
        }
        if (form.getLastName() != null) {
            log.info("update lastname");
            profile.setLastName(form.getLastName());
        }
        if (form.getBio() != null) {
            log.info("update bio");
            profile.setBio(form.getBio());
        }
        if (form.getPhoneNumber() != null) {
            log.info("update phone number");
            profile.setPhoneNumber(form.getPhoneNumber());
        }
        profile = profileRepository.save(profile);
        return ResponseEntity.ok(profile);
    }

    public Object profileAvatar(MultipartFile avatar) {
        ProfileModel profile = genericService.getUserAuth();
        if (profile == null) {
            log.error("profile not found");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "cet profile n'existe pas", "code", "PF10"));
        }
        MultipartFile avatarForm = avatar;
        if (avatarForm.isEmpty() || avatarForm == null) {
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "Veuillez soumettre votre image de profile", "code", "AV10"));
        }
        String filename = null;
        if (avatarForm != null && !avatarForm.isEmpty()) {
            filename = genericService.generateFileName("avatar");
            File source = new File(filename);
            try {
                Thumbnails.of(avatarForm.getInputStream()).scale(1).outputQuality(0.5).toFile(source);
                // avatarForm.transferTo(source.toPath());
            } catch (IllegalStateException e) {
                log.error("file not save");
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(ReportError.message("message", "file not save", "code", "AV11"));
            } catch (IOException e) {
                log.error("file not save");
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(ReportError.message("message", "file not save", "code", "AV11"));
            }
        }
        // update profile avatar
        profile.setAvatarUrl(filename);
        profile = profileRepository.save(profile);
        log.info("update user avatar table");
        return ResponseEntity.ok(profile);
    }

    public Object profileDocument(MultipartFile documentUrl, @Valid ProfileDocForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format de données");
            return ResponseEntity.badRequest().body(ReportError.getErrors(result));
        }
        ProfileModel profile = genericService.getUserAuth();
        if (profile == null) {
            log.error("profile not found");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "cet profile n'existe pas", "code", "PF10"));
        }
        MultipartFile documentForm = documentUrl;
        if (documentForm.isEmpty() || documentForm == null) {
            log.error("file is missing in request");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "Veuillez soumettre le document pdf", "code", "DOC10"));
        }
        String documentName = null;
        if (!documentForm.isEmpty() && documentForm != null) {
            String extension = genericService.getFileExtension(documentForm.getOriginalFilename());
            if (!extension.equalsIgnoreCase("pdf")) {
                log.error("need pdf file");
                return ResponseEntity.badRequest()
                        .body(ReportError.message("message", "Veuillez soumettre un document au format pdf", "code",
                                "DOC19"));
            }
            try {
                documentName = genericService.generateFileName(form.getDocumentType());
                File documentPath = new File(documentName);
                documentForm.transferTo(documentPath.toPath());
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest()
                        .body(ReportError.message("message", "document not save", "code", "DOC11"));
            }
        }
        // update profile document url
        profile.setDocumentType(form.getDocumentType());
        profile.setDocumentUrl(documentName);
        profile = profileRepository.save(profile);
        log.info("update profile document");
        return ResponseEntity.ok(profile);
    }
}
