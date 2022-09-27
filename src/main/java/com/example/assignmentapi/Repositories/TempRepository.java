package com.example.assignmentapi.Repositories;

import com.example.assignmentapi.Entities.Temp;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TempRepository extends JpaRepository<Temp, Integer> {

    @Query(
            value = "SELECT DISTINCT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id WHERE NOT ( (jobs.start_date <= 6) AND (jobs.end_date >= 3) ) AND temp_id NOT IN ( SELECT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id WHERE ( (jobs.start_date <= 6) AND (jobs.end_date >= 3) ) ) UNION (SELECT id from temps WHERE id NOT IN (SELECT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id ));",
            nativeQuery = true
    )
    List<Integer> findByDates(Integer startDate, Integer endDate);

    @NotNull Optional<Temp> findById(@NotNull Integer id);
}