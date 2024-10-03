package com.lunionlab.booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lunionlab.booking.form.AuthForm;
import com.lunionlab.booking.form.RefreshTokenForm;
import com.lunionlab.booking.form.VerifyOPT;
import com.lunionlab.booking.services.RefreshTokenService;
import com.lunionlab.booking.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "api/auth")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/user")
    public Object registerOrLogin(@Valid @RequestBody AuthForm form, BindingResult result) {
        return userService.registerOrLogin(form, result);
    }

    @PostMapping("/code/verification")
    public Object verificationOPT(@Valid @RequestBody VerifyOPT form, BindingResult result) {
        return userService.verificationOPT(form, result);
    }

    @Secured("ROLE_USER")
    @GetMapping("/profile")
    public Object getUersAuth() {
        return userService.getUserAuth();
    }

    @PostMapping("/refresh/token")
    public Object refreshToken(@Valid @RequestBody RefreshTokenForm form, BindingResult result) {
        return refreshTokenService.refreshToken(form, result);
    }
}
