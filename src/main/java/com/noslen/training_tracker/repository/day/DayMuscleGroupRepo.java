package com.noslen.training_tracker.repository.day;

import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DayMuscleGroupRepo extends JpaRepository<DayMuscleGroup, Long> {
    @Query(""" 
            SELECT dmgNext
            FROM DayMuscleGroup dmgCurrent
                 JOIN dmgCurrent.day currentDay
                 JOIN DayMuscleGroup dmgNext ON dmgNext.muscleGroup.id = dmgCurrent.muscleGroup.id
                 JOIN dmgNext.day nextDay 
            WHERE dmgCurrent.id = :currentDmgId 
              AND nextDay.mesocycle.id = currentDay.mesocycle.id 
              AND ( 
                    nextDay.week > currentDay.week OR 
                    (nextDay.week = currentDay.week AND nextDay.position > currentDay.position) 
                   ) 
              AND dmgNext.status = :status 
            ORDER BY nextDay.week, nextDay.position 
            """)
    Optional<DayMuscleGroup> findNextWithSameMuscleGroupByStatus(Long currentDmgId, Status status);

    @Query("""
            SELECT dmgNext
            FROM DayMuscleGroup dmgCurrent
                 JOIN dmgCurrent.day currentDay
                 JOIN DayMuscleGroup dmgNext ON dmgNext.muscleGroup.id = dmgCurrent.muscleGroup.id
                 JOIN dmgNext.day nextDay
            WHERE dmgCurrent.id = :currentDmgId
              AND nextDay.mesocycle.id = currentDay.mesocycle.id
              AND (
                   nextDay.week > currentDay.week OR
                   (nextDay.week = currentDay.week AND nextDay.position > currentDay.position)
                  )
            ORDER BY nextDay.week, nextDay.position
            """)
    Optional<DayMuscleGroup> findNextWithSameMuscleGroup(Long currentDmgId);


    @Query("""
            SELECT dmg
            FROM DayMuscleGroup dmg
                 JOIN Day day ON day.id = dmg.day.id
            WHERE day.mesocycle.mesocycleKey = :mesoKey
              AND day.week = :week
              AND day.position = :position
              AND dmg.muscleGroup.id = :mgId
            """)
    Optional<DayMuscleGroup> findDayMuscleGroupAt(String mesoKey, int week, int position,
            Long mgId);

    List<DayMuscleGroup> findByDay_Id(Long dayId);


}
