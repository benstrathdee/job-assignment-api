package com.example.assignmentapi.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "temps")
public class Temp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private Integer leftVal;
    @NotNull
    private Integer rightVal;
    @OneToMany(mappedBy = "temp", fetch = FetchType.LAZY)
    private Set<Job> jobs;

    public Temp (String firstName, String lastName, Integer leftVal, Integer rightVal) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.leftVal = leftVal;
        this.rightVal = rightVal;
    }

}
