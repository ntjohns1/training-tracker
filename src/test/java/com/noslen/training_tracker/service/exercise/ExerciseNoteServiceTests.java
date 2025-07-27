package com.noslen.training_tracker.service.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import com.noslen.training_tracker.dto.exercise.ExerciseNoteResponse;
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
    private ExerciseNoteRepo repo;

    @Mock
    private ExerciseNoteMapper mapper;

    @InjectMocks
    private ExerciseNoteServiceImpl service;

    private ExerciseNote testEntity;
    private ExerciseNoteResponse testPayload;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTime = Instant.now();
        
        testEntity = new ExerciseNote();
        testEntity.setId(1L);
        testEntity.setUserId(2L);
        testEntity.setNoteId(3L);
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
        when(mapper.toEntity(testPayload)).thenReturn(testEntity);
        when(repo.save(any(ExerciseNote.class))).thenReturn(testEntity);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNoteResponse result = service.createExerciseNote(testPayload);
        
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
        ExerciseNoteResponse updatePayload = new ExerciseNoteResponse(
                0L, 0L, 0L, 0L, 0L, null, testTime.plusSeconds(60), "Updated Exercise Note"
        );
        
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        when(repo.save(any(ExerciseNote.class))).thenReturn(testEntity);
        when(mapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseNoteResponse result = service.updateExerciseNote(id, updatePayload);

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
        ExerciseNoteResponse result = service.getExerciseNote(id);
        
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
