package com.example.assignmentapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @OneToOne
    private Temp temp;

    @Builder
    public User(String username, String password, String email, String role, Temp temp) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.temp = temp;
    }
}
