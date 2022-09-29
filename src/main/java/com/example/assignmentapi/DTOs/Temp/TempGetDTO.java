package com.example.assignmentapi.DTOs.Temp;

import com.example.assignmentapi.DTOs.Job.JobAsChildDTO;
import com.example.assignmentapi.Entities.Job;
import com.example.assignmentapi.Entities.Temp;

import java.util.ArrayList;

public class TempGetDTO {
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final ArrayList<JobAsChildDTO> jobs;

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ArrayList<JobAsChildDTO> getJobs() {
        return jobs;
    }

    public TempGetDTO(Temp temp) {
        this.id = temp.getId();
        this.firstName = temp.getFirstName();
        this.lastName = temp.getLastName();
        this.jobs = new ArrayList<>();
        if (temp.getJobs() != null) {
            for (Job job : temp.getJobs()) {
                this.jobs.add(new JobAsChildDTO(job));
            }
        }
    }
}
