package com.lunionlab.booking.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProfileForm {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String birthDate;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String country;

}
