package com.lunionlab.booking.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "codeOtp")
@NoArgsConstructor
@Getter
@Setter
public class CodeOtpModel extends BaseModel {
    private Date expiration;
    private String code;
    private String email;

    public CodeOtpModel(Date expiration, String code, String email) {
        this.expiration = expiration;
        this.code = code;
        this.email = email;
    }

}
