package com.lunionlab.booking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.lunionlab.booking.emum.DeletionEnum;
import com.lunionlab.booking.form.AuthForm;
import com.lunionlab.booking.form.VerifyOPT;
import com.lunionlab.booking.models.CodeOtpModel;
import com.lunionlab.booking.models.RefreshTokenModel;
import com.lunionlab.booking.models.UserModel;
import com.lunionlab.booking.repository.CodeOtpRespository;
import com.lunionlab.booking.repository.UserRepository;
import com.lunionlab.booking.response.AuthResponse;
import com.lunionlab.booking.utilities.ReportError;
import com.lunionlab.booking.utilities.Utility;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
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
                    Utility.hashPassword(codeOtp));
            userRepository.save(userM);
        }
        log.info("generation de code otp");
        boolean sendMail = Utility.sendMailWithResend("onboarding@resend.dev", form.getEmail(),
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
        Optional<CodeOtpModel> codeOpt = codeOtpRespository.findFirstByCodeAndDeleted(form.getCode(),
                DeletionEnum.DELETED_NO);
        if (codeOpt.isEmpty()) {
            log.error("code incorrecte");
            return ResponseEntity.badRequest()
                    .body(ReportError.message("message", "code saisi incorrecte", "code", "C11"));
        }
        CodeOtpModel codeOtpModel = codeOpt.get();
        Date now = new Date();

        if (codeOtpModel.getExpiration().compareTo(now) < 0) {
            log.error("code expiré");
            return ResponseEntity.badRequest().body(ReportError.message("message", "le code a expiré", "code", "C10"));
        }
        String token = jwtService.generateToken(codeOtpModel.getEmail());
        RefreshTokenModel refreshTokenModel = refreshTokenService.createRefreshToken(codeOtpModel.getEmail());
        AuthResponse authResponse = new AuthResponse(token, refreshTokenModel.getToken());
        codeOtpRespository.delete(codeOtpModel);

        return ResponseEntity.ok(authResponse);
    }

    public Object getUserAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(profileService.getProfileByEmail(email));
    }
}
