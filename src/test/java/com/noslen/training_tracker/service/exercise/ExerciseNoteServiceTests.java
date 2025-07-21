package com.noslen.training_tracker.service.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;

public class ExerciseNoteServiceTests {

    @Mock
    private ExerciseNoteRepo repo;

    @InjectMocks
    private ExerciseNoteServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateExerciseNote() {
        ExerciseNote exerciseNote = new ExerciseNote();
        when(repo.save(exerciseNote)).thenReturn(exerciseNote);

        ExerciseNote result = service.createExerciseNote(exerciseNote);
        assertEquals(exerciseNote, result);
        verify(repo, times(1)).save(exerciseNote);
    }

    @Test
    void testUpdateExerciseNote() {
        // Arrange
        Long id = 1L;
        ExerciseNote existingExerciseNote = new ExerciseNote();
        ExerciseNote newExerciseNote = new ExerciseNote();
        newExerciseNote.setText("Updated Exercise Note");

        // Mock the findById to return the existing exercise note
        when(repo.findById(id)).thenReturn(Optional.of(existingExerciseNote));
        when(repo.save(existingExerciseNote)).thenReturn(existingExerciseNote);

        // Act
        ExerciseNote result = service.updateExerciseNote(id, newExerciseNote);

        // Assert
        assertEquals(existingExerciseNote, result);
        assertEquals("Updated Exercise Note", existingExerciseNote.getText());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(existingExerciseNote);
    }

    @Test
    void testDeleteExerciseNote() {
        Long id = 1L;
        ExerciseNote exerciseNote = new ExerciseNote();
        when(repo.findById(id)).thenReturn(Optional.of(exerciseNote));

        service.deleteExerciseNote(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testGetExerciseNote() {
        Long id = 1L;
        ExerciseNote exerciseNote = new ExerciseNote();
        when(repo.findById(id)).thenReturn(Optional.of(exerciseNote));

        ExerciseNote result = service.getExerciseNote(id);
        assertEquals(exerciseNote, result);
        verify(repo, times(1)).findById(id);
    }

}
