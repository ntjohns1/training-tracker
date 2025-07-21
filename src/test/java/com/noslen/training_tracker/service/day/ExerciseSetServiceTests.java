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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
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
        ExerciseSetPayload payload = new ExerciseSetPayload(null, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f, 
                10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed");
        ExerciseSet entity = ExerciseSet.builder().position(1).build();
        ExerciseSet savedEntity = ExerciseSet.builder().id(1L).position(1).build();
        ExerciseSetPayload expectedPayload = new ExerciseSetPayload(1L, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f, 
                10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed");
        
        when(mapper.toEntity(payload)).thenReturn(entity);
        when(repo.save(any(ExerciseSet.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetPayload result = service.createExerciseSet(payload);
        
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
        ExerciseSetPayload payload = new ExerciseSetPayload(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f, 
                12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed");
        ExerciseSet existingEntity = ExerciseSet.builder().id(id).position(1).build();
        ExerciseSet savedEntity = ExerciseSet.builder().id(id).position(1).weight(105.0f).reps(12).build();
        ExerciseSetPayload expectedPayload = new ExerciseSetPayload(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f, 
                12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        // updateEntity is void, so we don't mock its return value
        when(repo.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetPayload result = service.updateExerciseSet(id, payload);

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
        ExerciseSet entity = ExerciseSet.builder().id(id).position(1).build();
        ExerciseSetPayload expectedPayload = new ExerciseSetPayload(id, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f, 
                10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetPayload result = service.getExerciseSet(id);
        
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
        entities.add(ExerciseSet.builder().id(1L).position(1).build());
        entities.add(ExerciseSet.builder().id(2L).position(2).build());
        
        List<ExerciseSetPayload> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new ExerciseSetPayload(1L, dayExerciseId, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f, 
                10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed"));
        expectedPayloads.add(new ExerciseSetPayload(2L, dayExerciseId, 2, "working", 102.5f, 95.0f, 90.0f, 105.0f, 
                9, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed"));
        
        when(repo.findByDayExerciseId(dayExerciseId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<ExerciseSetPayload> result = service.getExerciseSetsByDayExerciseId(dayExerciseId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDayExerciseId(dayExerciseId);
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
        ExerciseSetPayload payload = new ExerciseSetPayload(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f, 
                12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "completed");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateExerciseSet(id, payload));
        verify(repo, times(1)).findById(id);
    }
}
