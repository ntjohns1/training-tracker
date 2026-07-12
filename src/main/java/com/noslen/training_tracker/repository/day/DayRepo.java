package com.noslen.training_tracker.repository.day;

import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.model.day.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DayRepo extends JpaRepository<Day, Long> {
    List<Day> findByMesocycleId(Long mesocycleId);
    
    Optional<Day> findByMesocycleIdAndWeekAndPosition(Long mesocycleId, Integer week,
            Integer position);
    
    /**
     * Finds the next day in the mesocycle sequence that contains the same muscle group.
     * Given a current day and muscle group, this method looks for the next day (by week/position)
     * in the same mesocycle that has a DayMuscleGroup targeting the same MuscleGroup.
     *
     * @param currentDayId The ID of the current day
     * @param muscleGroupId The ID of the muscle group to find in the next day
     * @return Optional containing the next day with the same muscle group, or empty if not found
     */
    @Query(value = """
        SELECT DISTINCT next_day.*
        FROM days current_day
        JOIN days next_day ON next_day.meso_id = current_day.meso_id
        JOIN day_muscle_groups dmg ON dmg.day_id = next_day.id
        WHERE current_day.id = :currentDayId 
        AND dmg.muscle_group_id = :muscleGroupId
        AND (next_day.week > current_day.week 
             OR (next_day.week = current_day.week AND next_day.position > current_day.position))
        ORDER BY next_day.week ASC, next_day.position ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Day> findNextDayWithSameMuscleGroup(@Param("currentDayId") Long currentDayId,
                                                 @Param("muscleGroupId") Long muscleGroupId);
}
