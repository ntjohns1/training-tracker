package com.noslen.training_tracker.service.progression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;

import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;
import com.noslen.training_tracker.security.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.noslen.training_tracker.enums.MgProgressionType;
import com.noslen.training_tracker.mapper.progression.ProgressionMapper;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.model.progression.Progression;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;
import com.noslen.training_tracker.repository.progression.ProgressionRepo;

@ExtendWith(MockitoExtension.class)
public class ProgressionServiceTests {

    @Mock
    private ProgressionRepo repo;

    @Mock
    private ProgressionMapper mapper;

    @Mock
    private MuscleGroupRepo muscleGroupRepo;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProgressionServiceImpl service;

    private MuscleGroup testMuscleGroup;
    private com.noslen.training_tracker.model.mesocycle.Mesocycle testMesocycle;

    @BeforeEach
    void setUp() {
        // Create proper entity relationships for security validation
        testMesocycle = com.noslen.training_tracker.model.mesocycle.Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        testMuscleGroup = new MuscleGroup();
        testMuscleGroup.setId(1L);
    }

    @Test
    void testCreateProgression() {
        ProgressionResponse inputPayload = new ProgressionResponse(0L, 1L, MgProgressionType.REGULAR);
        Progression entityFromMapper = new Progression(null, null, MgProgressionType.REGULAR, null); // Mapper returns null MuscleGroup
        Progression entityWithMuscleGroup = new Progression(null, testMuscleGroup, MgProgressionType.REGULAR, null);
        Progression savedEntity = new Progression(1L, testMuscleGroup, MgProgressionType.REGULAR, null);
        ProgressionResponse expectedPayload = new ProgressionResponse(1L, 1L, MgProgressionType.REGULAR);
        
        when(mapper.toEntity(inputPayload)).thenReturn(entityFromMapper);
        when(muscleGroupRepo.findById(1L)).thenReturn(Optional.of(testMuscleGroup));
        when(repo.save(entityWithMuscleGroup)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        ProgressionResponse result = service.createProgression(inputPayload);

        assertEquals(expectedPayload, result);
        verify(mapper).toEntity(inputPayload);
        verify(muscleGroupRepo).findById(1L);
        verify(repo).save(entityWithMuscleGroup);
        verify(mapper).toPayload(savedEntity);
    }

    @Test
    void testUpdateProgression() {
        Long progressionId = 1L;
        ProgressionResponse updatePayload = new ProgressionResponse(1L, 1L, MgProgressionType.SECONDARY);
        
        Progression existingEntity = new Progression(1L, testMuscleGroup, MgProgressionType.REGULAR, testMesocycle);
        Progression updatedEntity = new Progression(1L, testMuscleGroup, MgProgressionType.SECONDARY, testMesocycle);
        Progression savedEntity = new Progression(1L, testMuscleGroup, MgProgressionType.SECONDARY, testMesocycle);
        ProgressionResponse expectedPayload = new ProgressionResponse(1L, 1L, MgProgressionType.SECONDARY);

        when(repo.findById(progressionId)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.updateEntity(existingEntity, updatePayload)).thenReturn(updatedEntity);
        when(repo.save(updatedEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        ProgressionResponse result = service.updateProgression(progressionId, updatePayload);

        assertEquals(expectedPayload, result);
        verify(repo).findById(progressionId);
        verify(userContext).validateUserAccess(100L);
        verify(mapper).updateEntity(existingEntity, updatePayload);
        verify(repo).save(updatedEntity);
        verify(mapper).toPayload(savedEntity);
    }

    @Test
    void testDeleteProgression() {
        Long progressionId = 1L;
        Progression entity = new Progression(1L, testMuscleGroup, MgProgressionType.REGULAR, testMesocycle);
        
        when(repo.findById(progressionId)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);

        service.deleteProgression(progressionId);
        
        verify(repo).findById(progressionId);
        verify(userContext).validateUserAccess(100L);
        verify(repo).deleteById(progressionId);
    }

    @Test
    void testGetProgression() {
        Long progressionId = 1L;
        Progression entity = new Progression(1L, testMuscleGroup, MgProgressionType.REGULAR, testMesocycle);
        ProgressionResponse expectedPayload = new ProgressionResponse(1L, 1L, MgProgressionType.REGULAR);

        when(repo.findById(progressionId)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        ProgressionResponse result = service.getProgression(progressionId);

        assertEquals(expectedPayload, result);
        verify(repo).findById(progressionId);
        verify(userContext).validateUserAccess(100L);
        verify(mapper).toPayload(entity);
    }
    
    @Test
    void testGetProgression_NotFound() {
        Long progressionId = 1L;
        when(repo.findById(progressionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getProgression(progressionId));
        verify(repo).findById(progressionId);
    }
}