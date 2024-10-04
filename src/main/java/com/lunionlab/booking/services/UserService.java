package com.lunionlab.booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.emum.StatusEnum;
import com.lunionlab.booking.form.AuthForm;
import com.lunionlab.booking.form.GoogleLogin;
import com.lunionlab.booking.form.VerifyOPT;
import com.lunionlab.booking.models.CodeOtpModel;
import com.lunionlab.booking.models.ProfileModel;
import com.lunionlab.booking.models.RefreshTokenModel;
import com.lunionlab.booking.models.UserModel;
import com.lunionlab.booking.repository.CodeOtpRespository;
import com.lunionlab.booking.repository.ProfileRepository;
import com.lunionlab.booking.repository.UserRepository;
import com.lunionlab.booking.response.AuthResponse;
import com.lunionlab.booking.utilities.ReportError;
import com.lunionlab.booking.utilities.Utility;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CodeOtpRespository codeOtpRespository;

    @Autowired
    CodeOptService codeOptService;

    @Autowired
    JwtService jwtService;

    @Value("${code.opt.lenght}")
    private Integer CODEOPTLENGTH;

    @Value("${code.opt.exp}")
    private Integer CODEOPTEXP;

    @Autowired
    ProfileService profileService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    ProfileRepository profileRepository;

    public UserModel getUserByEmail(String email) {
        return userRepository.findFirstByEmailAndDeleted(email, DeletionEnum.DELETED_NO).orElse(null);
    }

    public Object registerOrLogin(@Valid AuthForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format de données");
            return ResponseEntity.badRequest().body(ReportError.getErrors(result));
        }
        if (Utility.verifyEmail(form.getEmail()) == false) {
            log.error("email non valide");
            return ResponseEntity.badRequest().body(ReportError.message("message", "email invalide", "code", "E11"));
        }
        Optional<UserModel> userModel = userRepository.findFirstByEmailAndDeleted(form.getEmail(),
                DeletionEnum.DELETED_NO);
        String codeOtp = codeOptService.generateCodeOtp(CODEOPTLENGTH);
        CodeOtpModel saveCode = new CodeOtpModel(Utility.dateFromInteger(CODEOPTEXP,
                ChronoUnit.MINUTES), codeOtp,
                form.getEmail());
        if (userModel.isPresent()) {
            codeOtpRespository.save(saveCode);
        } else {
            codeOtpRespository.save(saveCode);
            UserModel userM = new UserModel(form.getEmail(),
                    Utility.hashPassword(codeOtp), StatusEnum.NEW_USER);
            userRepository.save(userM);
        }
        log.info("generation de code otp");
        boolean sendMail = Utility.sendMailWithResend("support.lunionbooking@lunion-lab.com", form.getEmail(),
                "Verify Email", codeOtp);
        if (sendMail) {
            log.info("send mail");
        } else {
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "mail non distribué", "code", "E10"));
        }
        return ResponseEntity.ok(codeOtp);
    }

    public Object verificationOPT(@Valid VerifyOPT form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format de données");
            return ResponseEntity.badRequest().body(ReportError.getErrors(result));
        }
        Map<String, Object> authRes = new HashMap<>();
        Optional<CodeOtpModel> codeOpt = codeOtpRespository.findFirstByCodeAndDeleted(form.getCode(),
                DeletionEnum.DELETED_NO);
        if (codeOpt.isEmpty()) {
            log.error("code incorrecte");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "code saisi incorrecte", "code", "C11"));
        }
        CodeOtpModel codeOtpModel = codeOpt.get();
        UserModel userModel = this.getUserByEmail(codeOtpModel.getEmail());
        Date now = new Date();

        if (codeOtpModel.getExpiration().compareTo(now) < 0) {
            log.error("code expiré");
            return ResponseEntity.badRequest().body(ReportError.message("message", "le code a expiré", "code", "C10"));
        }
        if (userModel.getStatus().intValue() == StatusEnum.NEW_USER.intValue()) {
            authRes.put("userState", "new");
            userModel.setStatus(StatusEnum.OLD_USER);
            userRepository.save(userModel);

            String token = jwtService.generateToken(codeOtpModel.getEmail());
            RefreshTokenModel refreshTokenModel = refreshTokenService.createRefreshToken(codeOtpModel.getEmail());
            AuthResponse authResponse = new AuthResponse(token, refreshTokenModel.getToken());
            codeOtpRespository.delete(codeOtpModel);
            authRes.put("accessToken", authResponse.getAccessToken());
            authRes.put("refreshToken", authResponse.getRefreshToken());
            return ResponseEntity.ok(authRes);
        }

        if (userModel.getStatus().intValue() == StatusEnum.OLD_USER.intValue()) {
            authRes.put("userState", "old");
        }
        String token = jwtService.generateToken(codeOtpModel.getEmail());
        RefreshTokenModel refreshTokenModel = refreshTokenService.createRefreshToken(codeOtpModel.getEmail());
        AuthResponse authResponse = new AuthResponse(token, refreshTokenModel.getToken());
        codeOtpRespository.delete(codeOtpModel);
        authRes.put("accessToken", authResponse.getAccessToken());
        authRes.put("refreshToken", authResponse.getRefreshToken());
        return ResponseEntity.ok(authRes);
    }

    public Object getUserAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(profileService.getProfileByEmail(email));
    }

    public Object googleLogin(@Valid GoogleLogin form, BindingResult result) {
        if (result.hasErrors()) {
            log.error("mauvais format des données");
            return ResponseEntity.badRequest().body(ReportError.getErrors(result));
        }
        Map<String, Object> response = new HashMap<>();
        if (Utility.verifyEmail(form.getEmail()) == false) {
            log.error("email non valide");
            return ResponseEntity.badRequest().body(ReportError.message("message", "email invalide", "code", "E11"));
        }
        UserModel userModel = this.getUserByEmail(form.getEmail());
        if (userModel == null) {
            userModel = new UserModel(form.getEmail(), Utility.hashPassword(form.getEmail()), StatusEnum.NEW_USER);
            userModel = userRepository.save(userModel);
        }
        ProfileModel profileModel = profileService.getProfileByEmail(userModel.getEmail());
        if (profileModel == null) {
            profileModel = new ProfileModel(null, null, null, null, null, form.getEmail(), null, null, null, null, null,
                    null, form.getAvatarUrl());
            profileModel = profileRepository.save(profileModel);
        }
        if (userModel.getStatus().intValue() == StatusEnum.NEW_USER.intValue()) {
            response.put("userState", "new");
            userModel.setStatus(StatusEnum.OLD_USER);
            userRepository.save(userModel);

            String token = jwtService.generateToken(userModel.getEmail());
            RefreshTokenModel refreshTokenModel = refreshTokenService.createRefreshToken(userModel.getEmail());
            AuthResponse authResponse = new AuthResponse(token, refreshTokenModel.getToken());
            response.put("accessToken", authResponse.getAccessToken());
            response.put("refreshToken", authResponse.getRefreshToken());
            return ResponseEntity.ok(response);
        }
        if (userModel.getStatus().intValue() == StatusEnum.OLD_USER.intValue()) {
            response.put("userState", "old");
        }

        String token = jwtService.generateToken(userModel.getEmail());
        RefreshTokenModel refreshTokenModel = refreshTokenService.createRefreshToken(userModel.getEmail());
        AuthResponse authResponse = new AuthResponse(token, refreshTokenModel.getToken());
        response.put("accessToken", authResponse.getAccessToken());
        response.put("refreshToken", authResponse.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
