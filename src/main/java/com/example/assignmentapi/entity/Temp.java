package com.example.assignmentapi.entity;

import lombok.*;

import javax.persistence.*;
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
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private Integer leftVal;
    @Column(nullable = false)
    private Integer rightVal;
    @OneToMany(mappedBy = "temp", fetch = FetchType.LAZY)
    private Set<Job> jobs;

    @Builder
    public Temp (String firstName, String lastName, Integer leftVal, Integer rightVal) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.leftVal = leftVal;
        this.rightVal = rightVal;
    }

}
