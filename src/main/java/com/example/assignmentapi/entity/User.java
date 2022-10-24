package com.example.assignmentapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @Column
    private String fingerprint;
    @OneToOne
    private Temp temp;

    @Builder
    public User(String username, String password, String email, String role, Temp temp) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fingerprint = UUID.randomUUID().toString();
        this.temp = temp;
    }
}
