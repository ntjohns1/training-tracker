package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgressionCalculationServiceImpl implements ProgressionCalculationService {
    
    private final DayRepo dayRepository;
    private final ExerciseSetRepo exerciseSetRepository;


    /**
     * @param completedDay
     * @return
     */
    @Override
    public boolean calculateProgression(Day completedDay) {
        Mesocycle meso = completedDay.getMesocycle();
        int mesoWeeksCount = meso.getWeeks().size() / meso.getDays();
        int pos = completedDay.getPosition();
        int week = completedDay.getWeek();
        return false;
    }
}
