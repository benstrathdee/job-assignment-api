package com.example.assignmentapi.repository;

import com.example.assignmentapi.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {

    // Return a list of jobs with assigned temps
    @Query(value = "SELECT * FROM jobs WHERE jobs.temp_id IS NOT null", nativeQuery = true)
    List<Job> findByIsAssigned();

    // Return a list of jobs without assigned temps
    @Query(value = "SELECT * FROM jobs WHERE jobs.temp_id IS null", nativeQuery = true)
    List<Job> findByIsNotAssigned();

    @NotNull Optional<Job> findById(@NotNull Integer id);
}
