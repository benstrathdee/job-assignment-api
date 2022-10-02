package com.example.assignmentapi.Repositories;

import com.example.assignmentapi.Entities.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface TempRepository extends JpaRepository<Temp, Integer> {

    // Gets all temps that don't have a job within the given date range
    @Query(value = "SELECT DISTINCT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id WHERE NOT ( (jobs.start_date <= :end) AND (jobs.end_date >= :start) ) AND temp_id NOT IN ( SELECT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id WHERE ( (jobs.start_date <= :end) AND (jobs.end_date >= :start) ) ) UNION (SELECT id from temps WHERE id NOT IN (SELECT temp_id FROM temps INNER JOIN jobs ON temps.id = jobs.temp_id ));", nativeQuery = true)
    List<Integer> findByDates(@Param("start") Long startDate, @Param("end") Long endDate);

    // Updates all values above the given value by 2
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE temps SET left_val = CASE WHEN left_val >= :above_right THEN left_val + 2 ELSE left_val END, right_val = right_val + 2 WHERE id > 0 AND right_val >= :above_right ;", nativeQuery = true)
    void updateNestValues(@Param("above_right") Integer aboveRight);

    // Gets the highest rightVal in the table
    @Query(value = "SELECT MAX(right_val) FROM temps;", nativeQuery = true)
    Optional<Integer> findHighestRightVal();

    // Find the temp with given leftVal
    Optional<Temp> findByLeftVal(Integer leftVal);

    // Gets all items nested between two values
    @Query(value = "SELECT * FROM temps WHERE (temps.left_val > :left_val AND temps.right_val < :right_val);", nativeQuery = true)
    List<Temp> findNested(@Param("left_val") Integer leftVal, @Param("right_val") Integer rightVal);

    @NotNull Optional<Temp> findById(@NotNull Integer id);
}