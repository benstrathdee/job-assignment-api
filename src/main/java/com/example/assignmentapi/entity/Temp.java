package com.example.assignmentapi.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

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
    @NotBlank
    private Integer leftVal;
    @NotBlank
    private Integer rightVal;
    @OneToMany(mappedBy = "temp", fetch = FetchType.LAZY)
    private Set<Job> jobs;

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getLeftVal() {
        return leftVal;
    }

    public void setLeftVal(Integer leftVal) {
        this.leftVal = leftVal;
    }

    public Integer getRightVal() {
        return rightVal;
    }

    public void setRightVal(Integer rightVal) {
        this.rightVal = rightVal;
    }

    public Set<Job> getJobs() {
        return this.jobs;
    }

    public Temp (String firstName, String lastName, Integer leftVal, Integer rightVal) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.leftVal = leftVal;
        this.rightVal = rightVal;
    }

    public Temp () {
        super();
    }
}
