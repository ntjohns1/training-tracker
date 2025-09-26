package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.day.response.DayMgFeedbackResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgressionCalculationServiceImpl implements ProgressionCalculationService {

    private final DayRepo dayRepository;
    private final DayMuscleGroupRepo dayMuscleGroupRepository;
    private final ExerciseSetRepo exerciseSetRepository;



}

