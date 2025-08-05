package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.security.UserContext;
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

    @Mock
    private UserContext userContext;

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
        
        ExerciseSetResponse payload = new ExerciseSetResponse(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f,
                                                              12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        
        ExerciseSet existingEntity = new ExerciseSet();
        existingEntity.setId(id);
        existingEntity.setPosition(1);
        existingEntity.setDayExercise(dayExercise);
        
        ExerciseSet savedEntity = new ExerciseSet();
        savedEntity.setId(id);
        savedEntity.setPosition(1);
        savedEntity.setWeight(105.0f);
        savedEntity.setReps(12);
        savedEntity.setDayExercise(dayExercise);
        
        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f,
                                                                      12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        // updateEntity is void, so we don't mock its return value
        when(repo.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.updateExerciseSet(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(mapper, times(1)).updateEntity(existingEntity, payload);
        verify(repo, times(1)).save(existingEntity);
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void getExerciseSet() {
        // Arrange
        Long id = 1L;
        
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
        
        ExerciseSet entity = new ExerciseSet();
        entity.setId(id);
        entity.setPosition(1);
        entity.setDayExercise(dayExercise);
        
        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(id, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                                      10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.getExerciseSet(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(mapper, times(1)).toPayload(entity);
    }

    @Test
    void deleteExerciseSet() {
        // Arrange
        Long id = 1L;
        
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
        
        ExerciseSet entity = new ExerciseSet();
        entity.setId(id);
        entity.setDayExercise(dayExercise);
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        
        // Act
        service.deleteExerciseSet(id);
        
        // Assert
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getExerciseSetsByDayExerciseId() {
        // Arrange
        Long dayExerciseId = 1L;
        
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
                .id(dayExerciseId)
                .day(day)
                .build();
        
        List<ExerciseSet> entities = new ArrayList<>();
        entities.add(createExerciseSetWithRelationship(1L, 1, dayExercise));
        entities.add(createExerciseSetWithRelationship(2L, 2, dayExercise));
        
        List<ExerciseSetResponse> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new ExerciseSetResponse(1L, dayExerciseId, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                     10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete"));
        expectedPayloads.add(new ExerciseSetResponse(2L, dayExerciseId, 2, "working", 102.5f, 95.0f, 90.0f, 105.0f,
                                                     9, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete"));
        
        when(repo.findByDayExercise_Id(dayExerciseId)).thenReturn(entities);
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<ExerciseSetResponse> result = service.getExerciseSetsByDayExerciseId(dayExerciseId);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDayExercise_Id(dayExerciseId);
        verify(userContext, times(1)).validateUserAccess(100L);
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
    
    private ExerciseSet createExerciseSetWithRelationship(Long id, int position, com.noslen.training_tracker.model.day.DayExercise dayExercise) {
        ExerciseSet entity = new ExerciseSet();
        entity.setId(id);
        entity.setPosition(position);
        entity.setDayExercise(dayExercise);
        return entity;
    }
}
