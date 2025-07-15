package com.noslen.training_tracker.service.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;

public class ExerciseServiceTests {

    @Mock
    private ExerciseRepo repo;

    @InjectMocks
    private ExerciseServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExercise() {
        Exercise exercise = new Exercise();
        when(repo.save(exercise)).thenReturn(exercise);

        Exercise result = service.createExercise(exercise);
        assertEquals(exercise, result);
        verify(repo, times(1)).save(exercise);
    }

    @Test
    void testUpdateExercise() {
        // Arrange
        Long id = 1L;
        Exercise existingExercise = new Exercise();
        Exercise newExercise = new Exercise();
        newExercise.setName("Updated Exercise");

        // Mock the findById to return the existing exercise
        when(repo.findById(id)).thenReturn(Optional.of(existingExercise));
        when(repo.save(existingExercise)).thenReturn(existingExercise);

        // Act
        Exercise result = service.updateExercise(id, newExercise);

        // Assert
        assertEquals(existingExercise, result);
        assertEquals("Updated Exercise", existingExercise.getName());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(existingExercise);
    }

    @Test
    void testDeleteExercise() {
        Long id = 1L;
        Exercise exercise = new Exercise();
        when(repo.findById(id)).thenReturn(Optional.of(exercise));

        service.deleteExercise(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testGetExercise() {
        Long id = 1L;
        Exercise exercise = new Exercise();
        when(repo.findById(id)).thenReturn(Optional.of(exercise));

        Exercise result = service.getExercise(id);
        assertEquals(exercise, result);
        verify(repo, times(1)).findById(id);
    }

    @Test
    void testGetAllExercises() {
        Exercise exercise1 = new Exercise();
        Exercise exercise2 = new Exercise();
        List<Exercise> expected = Arrays.asList(exercise1, exercise2);
        when(repo.findAll()).thenReturn(expected);

        List<Exercise> result = service.getAllExercises();
        assertEquals(expected, result);
        verify(repo, times(1)).findAll();
    }

    @Test
    void testAddExerciseNote() {
        Long exerciseId = 1L;
        ExerciseNote exerciseNote = new ExerciseNote();
        Optional<Exercise> exercise = Optional.of(new Exercise());
        when(repo.findById(exerciseId)).thenReturn(exercise);

        service.addExerciseNote(exerciseId, exerciseNote);
        verify(repo, times(1)).findById(exerciseId);
    }
}
