package com.lunionlab.booking.services;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.form.RefreshTokenForm;
import com.lunionlab.booking.models.RefreshTokenModel;
import com.lunionlab.booking.models.UserModel;
import com.lunionlab.booking.repository.RefreshTokenRepository;
import com.lunionlab.booking.repository.UserRepository;
import com.lunionlab.booking.response.AuthResponse;
import com.lunionlab.booking.utilities.ReportError;
import com.lunionlab.booking.utilities.Utility;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Value("${refresh.token.exp}")
    private Integer REFRESH_DELAY;

    public RefreshTokenModel getRefreshTokenByToken(String token) {
        return refreshTokenRepository.findFirstByTokenAndDeleted(token, DeletionEnum.DELETED_NO).orElse(null);
    }

    public RefreshTokenModel createRefreshToken(String email) {
        Optional<UserModel> userModel = userRepository.findFirstByEmailAndDeleted(email, DeletionEnum.DELETED_NO);
        Optional<RefreshTokenModel> refreshOpt = refreshTokenRepository.findFirstByUserAndDeleted(userModel.get(),
                DeletionEnum.DELETED_NO);
        if (refreshOpt.isPresent()) {
            return refreshOpt.get();
        }
        RefreshTokenModel refreshTokenModel = new RefreshTokenModel(UUID.randomUUID().toString(),
                Utility.dateFromInteger(REFRESH_DELAY, ChronoUnit.DAYS), userModel.get());
        refreshTokenModel = refreshTokenRepository.save(refreshTokenModel);
        return refreshTokenModel;
    }

    public Boolean verificationRefreshToken(String token) {
        RefreshTokenModel refreshTokenOpt = this.getRefreshTokenByToken(token);
        if (refreshTokenOpt == null) {
            return false;
        }
        Date now = new Date();
        if (refreshTokenOpt.getExpiration().compareTo(now) < 0) {
            refreshTokenRepository.delete(refreshTokenOpt);
            return false;
        } else {
            return true;
        }
    }

    public Object refreshToken(@Valid RefreshTokenForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format de données");
            return ResponseEntity.badRequest().body(ReportError.getErrors(result));
        }
        RefreshTokenModel refreshTokenModel = this.getRefreshTokenByToken(form.getRefreshToken());
        if (refreshTokenModel == null) {
            log.error("refresh token invalide");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "refresh token invalide", "code", "RT12"));
        }
        if (!this.verificationRefreshToken(form.getRefreshToken())) {
            log.error("refresh token expiré");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message",
                            form.getRefreshToken() + " "
                                    + "votre refresh token a expiré. SVP veuillez vous connecter à nouveau",
                            "code", "RT10"));
        }
        log.info("get new token from refreshToken=>" + "  " + form.getRefreshToken());
        AuthResponse authResponse = new AuthResponse(jwtService.generateToken(refreshTokenModel.getUser().getEmail()),
                form.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

}
