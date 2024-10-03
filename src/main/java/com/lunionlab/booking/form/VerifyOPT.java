package com.lunionlab.booking.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VerifyOPT {
    @NotEmpty
    @Size(max = 6, min = 6)
    private String code;

    public void setCode(String code) {
        this.code = code.trim();
    }

}
