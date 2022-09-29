package com.example.assignmentapi.Repositories;

import com.example.assignmentapi.Entities.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface TempRepository extends JpaRepository<Temp, Integer> {

    @Query(
            value = "SELECT DISTINCT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id WHERE NOT ( (jobs.start_date <= :end) AND (jobs.end_date >= :start) ) AND temp_id NOT IN ( SELECT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id WHERE ( (jobs.start_date <= :end) AND (jobs.end_date >= :start) ) ) UNION (SELECT id from temps WHERE id NOT IN (SELECT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id ));",
            nativeQuery = true
    )
    List<Integer> findByDates(@Param("start") Long startDate, @Param("end") Long endDate);

    // TODO
    // Query to get the whole tree
    // Query to get a manager's underlings

    @NotNull Optional<Temp> findById(@NotNull Integer id);
}