package com.example.assignmentapi.Repositories;

import com.example.assignmentapi.Entities.Job;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Integer> {
    @Query(value = "SELECT * FROM jobs WHERE jobs.temp_id IS NOT null", nativeQuery = true)
    List<Job> findByIsAssigned();

    @Query(value = "SELECT * FROM jobs WHERE jobs.temp_id IS null", nativeQuery = true)
    List<Job> findByIsNotAssigned();

    @NotNull Optional<Job> findById(@NotNull Integer id);
}
