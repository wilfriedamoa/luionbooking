package com.lunionlab.booking.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class GoogleLogin {
    @NotEmpty
    private String email;
    @NotEmpty
    private String avatarUrl;

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
