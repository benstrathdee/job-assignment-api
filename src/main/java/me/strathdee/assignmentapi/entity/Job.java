package com.example.assignmentapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long startDate;
    @Column(nullable = false)
    private Long endDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temp_id")
    private Temp temp;

    @Builder
    public Job(String name, Long startDate, Long endDate, Temp temp) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.temp = temp;
    }
}
