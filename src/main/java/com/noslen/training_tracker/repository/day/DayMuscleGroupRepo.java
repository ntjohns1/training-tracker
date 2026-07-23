package com.noslen.training_tracker.repository.day;

import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

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
    Optional<DayMuscleGroup> findNextWithSameMuscleGroupByStatus(@Param("currentDmgId") Long currentDmgId,@Param("status") Status status);

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
    Optional<DayMuscleGroup> findNextWithSameMuscleGroup(@Param("currentDmgId") Long currentDmgId);

    @Query("""
            SELECT dmgPrev
            FROM DayMuscleGroup dmgCurrent
                 JOIN dmgCurrent.day currentDay
                 JOIN DayMuscleGroup dmgPrev ON dmgPrev.muscleGroup.id = dmgCurrent.muscleGroup.id
                 JOIN dmgPrev.day prevDay
            WHERE dmgCurrent.id = :currentDmgId
              AND prevDay.mesocycle.id = currentDay.mesocycle.id
              AND (
                   prevDay.week < currentDay.week OR
                   (prevDay.week = currentDay.week AND prevDay.position < currentDay.position)
                  )
              AND dmgPrev.status = COMPLETE
            ORDER BY prevDay.week DESC, prevDay.position DESC
            """)
    List<DayMuscleGroup> findPreviousWithSameMuscleGroup(@Param("currentDmgId") Long currentDmgId, Pageable pageable);


    @Query("""
            SELECT dmg
            FROM DayMuscleGroup dmg
                 JOIN Day day ON day.id = dmg.day.id
            WHERE day.mesocycle.id = dmg.day.mesocycle.id
              AND day.week = :week
              AND day.position = :position
              AND dmg.muscleGroup.id = :mgId
            """)
    Optional<DayMuscleGroup> findDayMuscleGroupAt(@Param("week") int week,
            @Param("position") int position,
            @Param("mgId") Long mgId);

    List<DayMuscleGroup> findByDay_Id(Long dayId);


    default Optional<DayMuscleGroup> findMostRecentWithSameMuscleGroup(Long currentDmgId) {
        List<DayMuscleGroup> page = findPreviousWithSameMuscleGroup(currentDmgId, PageRequest.of(0, 1));
        return page.stream().findFirst();
    }

    @Query("""
            SELECT dmgNext
            FROM DayMuscleGroup dmgCurrent
                 JOIN dmgCurrent.day currentDay
                 JOIN DayMuscleGroup dmgNext ON dmgNext.muscleGroup.id = dmgCurrent.muscleGroup.id
                 JOIN dmgNext.day nextDay
            WHERE dmgCurrent.id = :currentDmgId
              AND nextDay.mesocycle.id = currentDay.mesocycle.id
              AND nextDay.week = currentDay.week + 1
              AND nextDay.position = currentDay.position
            """)
    Optional<DayMuscleGroup> findNextDayMuscleGroupForNextWeek(@Param("currentDmgId") Long currentDmgId);

}
