package com.example.assignmentapi.Entities;

import com.example.assignmentapi.DTOs.JobReadDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
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

    public ArrayList<JobReadDTO> getJobs() {
        ArrayList<JobReadDTO> jobDTOs = new ArrayList<>();
        if (this.jobs != null) {
            for (Job job : this.jobs) {
                jobDTOs.add(new JobReadDTO(job));
            }
        }
        return jobDTOs;
    }

    public void addJob(Job job) {
        this.jobs.add(job);
    }

    public void removeJob(Job job) {
        this.jobs.remove(job);
    }

    public Temp (String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Temp () {
        super();
    }
}
