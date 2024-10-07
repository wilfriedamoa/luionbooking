package com.lunionlab.booking.services;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.models.ProfileModel;
import com.lunionlab.booking.repository.ProfileRepository;

@Service
public class GenericService {
    @Autowired
    ProfileRepository profileRepository;

    @Value("${path.root}")
    private String IMAGE_PATH;

    public String getUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return email;
    }

    public ProfileModel getUserAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return profileRepository.findFirstByEmailAndDeleted(email, DeletionEnum.DELETED_NO).orElse(null);
    }

    public String generateFileName(String folder) {
        File root = new File(IMAGE_PATH + File.separator + folder);
        if (!root.exists()) {
            root.mkdir();
        }
        String path = "";
        while (true) {
            path = root.getAbsolutePath() + File.separator + UUID.randomUUID().toString();
            File filePath = new File(path);
            if (filePath.exists() == false) {
                path = filePath.getAbsolutePath();
                break;
            }
        }
        return path;
    }

    public String getFileExtension(String orignalFilename) {
        int lastIndex = orignalFilename.lastIndexOf(".");
        if (lastIndex == -1) {
            return null;
        }
        return orignalFilename.substring(lastIndex + 1);
    }
}
