package com.example.assignmentapi.Entities;

import com.example.assignmentapi.DTOs.TempReadDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private Integer startDate;
    @NotBlank
    private Integer endDate;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "temp_id")
    private Temp temp;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public TempReadDTO getTemp() {
        if (this.temp != null) {
            return new TempReadDTO(this.temp);
        }
        return null;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public Job(String name, Integer startDate, Integer endDate, Temp temp) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.temp = temp;
    }

    public Job() {
        super();
    }
}
