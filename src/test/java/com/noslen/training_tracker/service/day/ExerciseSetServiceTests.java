package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;

public class ExerciseSetServiceTests {

    @Mock
    private ExerciseSetRepo repo;

    @InjectMocks
    private ExerciseSetServiceImpl service;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    void createExerciseSet() {
        ExerciseSet exerciseSet = ExerciseSet.builder().build();
        when(repo.save(exerciseSet)).thenReturn(exerciseSet);
        assertEquals(exerciseSet, service.createExerciseSet(exerciseSet));
    }

    @Test
    void updateExerciseSet() {
        Long id = 1L;
        ExerciseSet exerciseSet = ExerciseSet.builder().build();
        when(repo.findById(id)).thenReturn(Optional.of(exerciseSet));
        when(repo.save(exerciseSet)).thenReturn(exerciseSet);
        assertEquals(exerciseSet, service.updateExerciseSet(id, exerciseSet));
    }

    @Test
    void getExerciseSet() {
        Long id = 1L;
        ExerciseSet exerciseSet = ExerciseSet.builder().build();
        when(repo.findById(id)).thenReturn(Optional.of(exerciseSet));
        assertEquals(exerciseSet, service.getExerciseSet(id));
    }

    @Test
    void deleteExerciseSet() {
        Long id = 1L;
        service.deleteExerciseSet(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getExerciseSetsByDayExerciseId() {
        Long dayExerciseId = 1L;
        List<ExerciseSet> exerciseSets = List.of(ExerciseSet.builder().build());
        when(repo.findByDayExerciseId(dayExerciseId)).thenReturn(exerciseSets);
        assertEquals(exerciseSets, service.getExerciseSetsByDayExerciseId(dayExerciseId));
    }

}
