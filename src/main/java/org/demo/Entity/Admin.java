package org.demo.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admins")
public class Admin implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role; // default ADMIN

    @JsonIgnore
    private String otp;
    @JsonIgnore
    private Long otpExpiry;
}
