package com.lunionlab.booking.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class AuthForm {
    @NotEmpty
    private String email;

    public void setEmail(String email) {
        this.email = email.trim();
    }

}
