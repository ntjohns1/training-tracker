package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.ExerciseSetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.noslen.training_tracker.mapper.day.ExerciseSetMapper;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;

public class ExerciseSetServiceTests {

    @Mock
    private ExerciseSetRepo repo;
    
    @Mock
    private ExerciseSetMapper mapper;

    @InjectMocks
    private ExerciseSetServiceImpl service;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    void createExerciseSet() {
        // Arrange
        ExerciseSetResponse payload = new ExerciseSetResponse(null, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                              10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        ExerciseSet entity = new ExerciseSet();
        entity.setPosition(1);
        ExerciseSet savedEntity = new ExerciseSet();
        savedEntity.setId(1L);
        savedEntity.setPosition(1);
        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(1L, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                                      10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        
        when(mapper.toEntity(payload)).thenReturn(entity);
        when(repo.save(any(ExerciseSet.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.createExerciseSet(payload);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(mapper, times(1)).toEntity(payload);
        verify(repo, times(1)).save(any(ExerciseSet.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void updateExerciseSet() {
        // Arrange
        Long id = 1L;
        ExerciseSetResponse payload = new ExerciseSetResponse(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f,
                                                              12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        ExerciseSet existingEntity = new ExerciseSet();
        existingEntity.setId(id);
        existingEntity.setPosition(1);
        ExerciseSet savedEntity = new ExerciseSet();
        savedEntity.setId(id);
        savedEntity.setPosition(1);
        savedEntity.setWeight(105.0f);
        savedEntity.setReps(12);
        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f,
                                                                      12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        // updateEntity is void, so we don't mock its return value
        when(repo.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.updateExerciseSet(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).updateEntity(existingEntity, payload);
        verify(repo, times(1)).save(existingEntity);
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void getExerciseSet() {
        // Arrange
        Long id = 1L;
        ExerciseSet entity = new ExerciseSet();
        entity.setId(id);
        entity.setPosition(1);
        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(id, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                                      10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.getExerciseSet(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).toPayload(entity);
    }

    @Test
    void deleteExerciseSet() {
        // Arrange
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);
        
        // Act
        service.deleteExerciseSet(id);
        
        // Assert
        verify(repo, times(1)).existsById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getExerciseSetsByDayExerciseId() {
        // Arrange
        Long dayExerciseId = 1L;
        List<ExerciseSet> entities = new ArrayList<>();
        entities.add(createExerciseSet(1L, 1));
        entities.add(createExerciseSet(2L, 2));
        
        List<ExerciseSetResponse> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new ExerciseSetResponse(1L, dayExerciseId, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                     10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete"));
        expectedPayloads.add(new ExerciseSetResponse(2L, dayExerciseId, 2, "working", 102.5f, 95.0f, 90.0f, 105.0f,
                                                     9, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete"));
        
        when(repo.findByDayExercise_Id(dayExerciseId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<ExerciseSetResponse> result = service.getExerciseSetsByDayExerciseId(dayExerciseId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDayExercise_Id(dayExerciseId);
        verify(mapper, times(1)).toPayloadList(entities);
    }
    
    @Test
    void testGetExerciseSetNotFound() {
        // Arrange
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.getExerciseSet(id));
        verify(repo, times(1)).findById(id);
    }
    
    @Test
    void testUpdateExerciseSetNotFound() {
        // Arrange
        Long id = 1L;
        ExerciseSetResponse payload = new ExerciseSetResponse(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f,
                                                              12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateExerciseSet(id, payload));
        verify(repo, times(1)).findById(id);
    }

    private ExerciseSet createExerciseSet(Long id, int position) {
        ExerciseSet entity = new ExerciseSet();
        entity.setId(id);
        entity.setPosition(position);
        return entity;
    }
}
