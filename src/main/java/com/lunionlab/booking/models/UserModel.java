package com.lunionlab.booking.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class UserModel extends BaseModel {
    private String email;
    private String password;

    public UserModel(String email, String password, Integer status) {
        this.email = email;
        this.password = password;
        this.status = status;
    }

}
