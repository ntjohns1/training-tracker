package com.noslen.training_tracker.service.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.time.Instant;
import java.util.Optional;

import com.noslen.training_tracker.dto.exercise.request.CreateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.request.UpdateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;

public class ExerciseNoteServiceTests {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExerciseNoteRepo repo;

    @Mock
    private ExerciseNoteMapper mapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ExerciseNoteServiceImpl service;

    private ExerciseNote testEntity;
    private ExerciseNoteResponse testPayload;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTime = Instant.now();
        
        // Create proper entity relationships for security validation
        com.noslen.training_tracker.model.mesocycle.Mesocycle mesocycle = 
            com.noslen.training_tracker.model.mesocycle.Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        com.noslen.training_tracker.model.day.Day day = 
            com.noslen.training_tracker.model.day.Day.builder()
                .id(5L)
                .mesocycle(mesocycle)
                .build();
                
        com.noslen.training_tracker.model.day.DayExercise dayExercise = 
            com.noslen.training_tracker.model.day.DayExercise.builder()
                .id(3L)
                .day(day)
                .build();
        
        testEntity = new ExerciseNote();
        testEntity.setId(1L);
        testEntity.setUserId(2L);
        testEntity.setNoteId(3L);
        testEntity.setDayExercise(dayExercise);
        testEntity.setCreatedAt(testTime);
        testEntity.setUpdatedAt(testTime);
        testEntity.setText("Test note");
                
        testPayload = new ExerciseNoteResponse(
                1L, 4L, 2L, 3L, 5L, testTime, testTime, "Test note"
        );
    }

    @Test
    void testCreateExerciseNote() {
        // Given
        CreateExerciseNoteRequest request = new CreateExerciseNoteRequest(4L, 5L, 3L, "Test note");
        Exercise exercise = new Exercise();
        exercise.setId(4L);
        DayExercise dayExercise = DayExercise.builder().id(5L).build();

        when(entityManager.getReference(Exercise.class, 4L)).thenReturn(exercise);
        when(entityManager.getReference(DayExercise.class, 5L)).thenReturn(dayExercise);
        when(repo.save(any(ExerciseNote.class))).thenReturn(testEntity);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNoteResponse result = service.createExerciseNote(request);

        // Then
        assertEquals(testPayload, result);
        verify(entityManager, times(1)).getReference(Exercise.class, 4L);
        verify(entityManager, times(1)).getReference(DayExercise.class, 5L);
        verify(repo, times(1)).save(any(ExerciseNote.class));
        verify(mapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testUpdateExerciseNote() {
        // Given
        Long id = 1L;
        UpdateExerciseNoteRequest request = new UpdateExerciseNoteRequest(id, "Updated Exercise Note");

        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(repo.save(testEntity)).thenReturn(testEntity);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNoteResponse result = service.updateExerciseNote(id, request);

        // Then
        assertEquals(testPayload, result);
        assertEquals("Updated Exercise Note", testEntity.getText());
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).save(testEntity);
        verify(mapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testDeleteExerciseNote() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        doNothing().when(userContext).validateUserAccess(100L);

        // When
        service.deleteExerciseNote(id);
        
        // Then
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testDeleteExerciseNote_NotFound_ShouldThrowException() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> service.deleteExerciseNote(id));
        verify(repo, times(1)).findById(id);
        verify(repo, times(0)).deleteById(id);
    }

    @Test
    void testGetExerciseNote() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNoteResponse result = service.getExerciseNote(id);
        
        // Then
        assertEquals(testPayload, result);
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(mapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testGetExerciseNote_NotFound_ShouldThrowException() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> service.getExerciseNote(id));
        verify(repo, times(1)).findById(id);
        verify(mapper, times(0)).toPayload(any());
    }

}
