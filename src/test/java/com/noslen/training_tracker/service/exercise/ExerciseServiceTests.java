package com.noslen.training_tracker.service.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.exercise.ExerciseNoteResponse;
import com.noslen.training_tracker.dto.exercise.ExerciseResponse;
import com.noslen.training_tracker.enums.ExerciseType;
import com.noslen.training_tracker.enums.MgSubType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.mapper.exercise.ExerciseMapper;
import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;

public class ExerciseServiceTests {

    @Mock
    private ExerciseRepo repo;

    @Mock
    private ExerciseMapper exerciseMapper;

    @Mock
    private ExerciseNoteMapper exerciseNoteMapper;

    @InjectMocks
    private ExerciseServiceImpl service;

    private Exercise testEntity;
    private ExerciseResponse testPayload;
    private ExerciseNote testNoteEntity;
    private ExerciseNoteResponse testNotePayload;
    private Instant testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTime = Instant.now();
        
        testNoteEntity = new ExerciseNote();
        testNoteEntity.setId(1L);
        testNoteEntity.setUserId(2L);
        testNoteEntity.setNoteId(3L);
        testNoteEntity.setCreatedAt(testTime);
        testNoteEntity.setUpdatedAt(testTime);
        testNoteEntity.setText("Test note");
                
        testNotePayload = new ExerciseNoteResponse(
                1L, 4L, 2L, 3L, 5L, testTime, testTime, "Test note"
        );
        
        testEntity = new Exercise();
        testEntity.setId(1L);
        testEntity.setName("Test Exercise");
        testEntity.setMuscleGroupId(2L);
        testEntity.setYoutubeId("youtube123");
        testEntity.setExerciseType(ExerciseType.BARBELL);
        testEntity.setUserId(3L);
        testEntity.setCreatedAt(testTime);
        testEntity.setUpdatedAt(testTime);
        testEntity.setMgSubType(MgSubType.HORIZONTAL);
        testEntity.setNotes(new ArrayList<>(Arrays.asList(testNoteEntity)));
                
        testPayload = new ExerciseResponse(
                1L, "Test Exercise", 2L, "youtube123", "barbell", 3L,
                testTime, testTime, null, "primary", Arrays.asList(testNotePayload)
        );
    }

    @Test
    void testCreateExercise() {
        // Given
        when(exerciseMapper.toEntity(testPayload)).thenReturn(testEntity);
        when(repo.save(any(Exercise.class))).thenReturn(testEntity);
        when(exerciseMapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseResponse result = service.createExercise(testPayload);
        
        // Then
        assertEquals(testPayload, result);
        verify(exerciseMapper, times(1)).toEntity(testPayload);
        verify(repo, times(1)).save(any(Exercise.class));
        verify(exerciseMapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testUpdateExercise() {
        // Given
        Long id = 1L;
        ExerciseResponse updatePayload = new ExerciseResponse(
                0L, "Updated Exercise", 0L, null, null, 0L,
                null, testTime.plusSeconds(60), null, null, null
        );
        
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        when(repo.save(any(Exercise.class))).thenReturn(testEntity);
        when(exerciseMapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseResponse result = service.updateExercise(id, updatePayload);

        // Then
        assertEquals(testPayload, result);
        verify(repo, times(1)).findById(id);
        verify(exerciseMapper, times(1)).updateEntity(testEntity, updatePayload);
        verify(repo, times(1)).save(testEntity);
        verify(exerciseMapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testDeleteExercise() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));

        // When
        service.deleteExercise(id);
        
        // Then
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testDeleteExercise_NotFound_ShouldThrowException() {
        // Given    
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> service.deleteExercise(id));
        verify(repo, times(1)).findById(id);
        verify(repo, times(0)).deleteById(id);
    }

    @Test
    void testGetExercise() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.of(testEntity));
        when(exerciseMapper.toPayload(testEntity)).thenReturn(testPayload);

        // When
        ExerciseResponse result = service.getExercise(id);
        
        // Then
        assertEquals(testPayload, result);
        verify(repo, times(1)).findById(id);
        verify(exerciseMapper, times(1)).toPayload(testEntity);
    }

    @Test
    void testGetExercise_NotFound_ShouldThrowException() {
        // Given
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> service.getExercise(id));
        verify(repo, times(1)).findById(id);
        verify(exerciseMapper, times(0)).toPayload(any());
    }

    @Test
    void testGetAllExercises() {
        // Given
        List<Exercise> entities = Arrays.asList(testEntity, testEntity);
        List<ExerciseResponse> expectedPayloads = Arrays.asList(testPayload, testPayload);
        when(repo.findAll()).thenReturn(entities);
        when(exerciseMapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // When
        List<ExerciseResponse> result = service.getAllExercises();
        
        // Then
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findAll();
        verify(exerciseMapper, times(1)).toPayloadList(entities);
    }

    @Test
    void testAddExerciseNote() {
        // Given
        Long exerciseId = 1L;
        when(repo.findById(exerciseId)).thenReturn(Optional.of(testEntity));
        when(exerciseNoteMapper.toEntity(testNotePayload)).thenReturn(testNoteEntity);
        when(repo.save(any(Exercise.class))).thenReturn(testEntity);

        // When
        service.addExerciseNote(exerciseId, testNotePayload);
        
        // Then
        verify(repo, times(1)).findById(exerciseId);
        verify(exerciseNoteMapper, times(1)).toEntity(testNotePayload);
        verify(repo, times(1)).save(testEntity);
    }

    @Test
    void testAddExerciseNote_ExerciseNotFound_ShouldThrowException() {
        // Given
        Long exerciseId = 1L;
        when(repo.findById(exerciseId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> service.addExerciseNote(exerciseId, testNotePayload));
        verify(repo, times(1)).findById(exerciseId);
        verify(exerciseNoteMapper, times(0)).toEntity(any());
        verify(repo, times(0)).save(any());
    }
}
