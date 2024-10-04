package com.lunionlab.booking.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;;

@Entity
@Table(name = "profiles")
@NoArgsConstructor
@Getter
@Setter
public class ProfileModel extends BaseModel {
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String country;
    private String city;
    private Date birthDate;
    private String email;
    private String bio;
    private String address;
    private String phoneNumber;
    private String job;
    private String gender;
    private String documentUrl;
    private String documentType;

    public ProfileModel(String firstName, String lastName, String country, String city, Date birthDate, String email,
            String bio, String address, String phoneNumber, String job, String gender, String documentType,
            String avatarUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.birthDate = birthDate;
        this.email = email;
        this.bio = bio;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.job = job;
        this.gender = gender;
        this.documentType = documentType;
        this.avatarUrl = avatarUrl;
    }

}
