package com.lunionlab.booking.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "refreshToken")
public class RefreshTokenModel extends BaseModel {
    private String token;
    private Date expiration;
    @OneToOne
    private UserModel user;

    public RefreshTokenModel(String token, Date expiration, UserModel user) {
        this.token = token;
        this.expiration = expiration;
        this.user = user;
    }

}
