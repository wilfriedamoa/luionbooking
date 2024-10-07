package com.lunionlab.booking.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDocForm {
    @NotEmpty
    private String documentType;
}
