package com.noslen.training_tracker.service.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;
import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;

public class ExerciseNoteServiceTests {

    @Mock
    private ExerciseNoteRepo repo;

    @Mock
    private ExerciseNoteMapper mapper;

    @InjectMocks
    private ExerciseNoteServiceImpl service;

    private ExerciseNote testEntity;
    private ExerciseNotePayload testPayload;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTime = Instant.now();
        
        testEntity = ExerciseNote.builder()
                .id(1L)
                .userId(2L)
                .noteId(3L)
                .createdAt(testTime)
                .updatedAt(testTime)
                .text("Test note")
                .build();
                
        testPayload = new ExerciseNotePayload(
                1L, 4L, 2L, 3L, 5L, testTime, testTime, "Test note"
        );
    }

    @Test
    void testCreateExerciseNote() {
        // Given
        when(mapper.toEntity(testPayload)).thenReturn(testEntity);
        when(repo.save(any(ExerciseNote.class))).thenReturn(testEntity);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNotePayload result = service.createExerciseNote(testPayload);
        
        // Then
        assertEquals(testPayload, result);
        verify(mapper, times(1)).toEntity(testPayload);
        verify(repo, times(1)).save(any(ExerciseNote.class));
        verify(mapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testUpdateExerciseNote() {
        // Given
        Long id = 1L;
        ExerciseNotePayload updatePayload = new ExerciseNotePayload(
                0L, 0L, 0L, 0L, 0L, null, testTime.plusSeconds(60), "Updated Exercise Note"
        );
        
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        when(repo.save(any(ExerciseNote.class))).thenReturn(testEntity);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNotePayload result = service.updateExerciseNote(id, updatePayload);

        // Then
        assertEquals(testPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).updateEntity(testEntity, updatePayload);
        verify(repo, times(1)).save(testEntity);
        verify(mapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testDeleteExerciseNote() {
        // Given
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);

        // When
        service.deleteExerciseNote(id);
        
        // Then
        verify(repo, times(1)).existsById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testDeleteExerciseNote_NotFound_ShouldThrowException() {
        // Given
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(false);

        // When/Then
        assertThrows(RuntimeException.class, () -> service.deleteExerciseNote(id));
        verify(repo, times(1)).existsById(id);
        verify(repo, times(0)).deleteById(id);
    }

    @Test
    void testGetExerciseNote() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNotePayload result = service.getExerciseNote(id);
        
        // Then
        assertEquals(testPayload, result);
        verify(repo, times(1)).findById(id);
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
