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

import com.noslen.training_tracker.dto.day.request.CreateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.request.UpdateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.noslen.training_tracker.mapper.day.ExerciseSetMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;

public class ExerciseSetServiceTests {

    @Mock
    private ExerciseSetRepo repo;

    @Mock
    private ExerciseSetMapper mapper;

    @Mock
    private UserContext userContext;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ExerciseSetServiceImpl service;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    void createExerciseSet() {
        // Arrange
        CreateExerciseSetRequest request = new CreateExerciseSetRequest(1L, 1, "regular", 95.0f, 90.0f, 105.0f, 8, Instant.now());
        DayExercise dayExercise = DayExercise.builder().id(1L).build();
        ExerciseSet savedEntity = new ExerciseSet();
        savedEntity.setId(1L);
        savedEntity.setPosition(1);
        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(1L, 1L, 1, "working", 100.0f, 95.0f, 90.0f, 105.0f,
                                                                      10, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");

        when(entityManager.getReference(DayExercise.class, 1L)).thenReturn(dayExercise);
        when(repo.save(any(ExerciseSet.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.createExerciseSet(request);

        // Assert
        assertEquals(expectedPayload, result);
        verify(entityManager, times(1)).getReference(DayExercise.class, 1L);
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
        
        UpdateExerciseSetRequest request = new UpdateExerciseSetRequest(105.0f, 12, null, null);

        ExerciseSet existingEntity = new ExerciseSet();
        existingEntity.setId(id);
        existingEntity.setPosition(1);
        existingEntity.setDayExercise(dayExercise);

        ExerciseSetResponse expectedPayload = new ExerciseSetResponse(id, 1L, 1, "working", 105.0f, 95.0f, 90.0f, 105.0f,
                                                                      12, 8, 70.0f, "kg", Instant.now(), Instant.now(), "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(repo.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toPayload(existingEntity)).thenReturn(expectedPayload);

        // Act
        ExerciseSetResponse result = service.updateExerciseSet(id, request);

        // Assert
        assertEquals(expectedPayload, result);
        assertEquals(105.0f, existingEntity.getWeight());
        assertEquals(12, existingEntity.getReps());
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).save(existingEntity);
        verify(mapper, times(1)).toPayload(existingEntity);
    }

    @Test
    void updateExerciseSet_LogStampsFinishedAt() {
        // Arrange — logging a set: status "complete" should stamp finishedAt + set status
        Long id = 1L;
        com.noslen.training_tracker.model.mesocycle.Mesocycle mesocycle =
            com.noslen.training_tracker.model.mesocycle.Mesocycle.builder().id(10L).userId(100L).build();
        com.noslen.training_tracker.model.day.Day day =
            com.noslen.training_tracker.model.day.Day.builder().id(5L).mesocycle(mesocycle).build();
        com.noslen.training_tracker.model.day.DayExercise dayExercise =
            com.noslen.training_tracker.model.day.DayExercise.builder().id(3L).day(day).build();

        ExerciseSet existingEntity = new ExerciseSet();
        existingEntity.setId(id);
        existingEntity.setDayExercise(dayExercise);

        UpdateExerciseSetRequest request = new UpdateExerciseSetRequest(100.0f, 8, null, "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(repo.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toPayload(existingEntity)).thenReturn(
            new ExerciseSetResponse(id, 3L, 1, "regular", 100.0f, null, null, null, 8, null, null,
                                    "kg", Instant.now(), Instant.now(), "complete"));

        // Act
        service.updateExerciseSet(id, request);

        // Assert — finishedAt stamped, status COMPLETE
        assertEquals(com.noslen.training_tracker.enums.Status.COMPLETE, existingEntity.getStatus());
        org.junit.jupiter.api.Assertions.assertNotNull(existingEntity.getFinishedAt());
    }

    @Test
    void updateExerciseSet_UnlogClearsFinishedAt() {
        // Arrange — unlogging: any non-complete status clears finishedAt
        Long id = 1L;
        com.noslen.training_tracker.model.mesocycle.Mesocycle mesocycle =
            com.noslen.training_tracker.model.mesocycle.Mesocycle.builder().id(10L).userId(100L).build();
        com.noslen.training_tracker.model.day.Day day =
            com.noslen.training_tracker.model.day.Day.builder().id(5L).mesocycle(mesocycle).build();
        com.noslen.training_tracker.model.day.DayExercise dayExercise =
            com.noslen.training_tracker.model.day.DayExercise.builder().id(3L).day(day).build();

        ExerciseSet existingEntity = new ExerciseSet();
        existingEntity.setId(id);
        existingEntity.setDayExercise(dayExercise);
        existingEntity.setFinishedAt(Instant.now());
        existingEntity.setStatus(com.noslen.training_tracker.enums.Status.COMPLETE);

        UpdateExerciseSetRequest request = new UpdateExerciseSetRequest(null, null, null, "pending");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(repo.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toPayload(existingEntity)).thenReturn(
            new ExerciseSetResponse(id, 3L, 1, "regular", null, null, null, null, null, null, null,
                                    "kg", Instant.now(), null, "pending"));

        // Act
        service.updateExerciseSet(id, request);

        // Assert
        assertEquals(com.noslen.training_tracker.enums.Status.PENDING, existingEntity.getStatus());
        org.junit.jupiter.api.Assertions.assertNull(existingEntity.getFinishedAt());
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
        UpdateExerciseSetRequest request = new UpdateExerciseSetRequest(105.0f, 12, null, null);
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateExerciseSet(id, request));
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
