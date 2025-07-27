package com.noslen.training_tracker.service.muscle_group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.noslen.training_tracker.dto.muscle_group.response.ProgressionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.noslen.training_tracker.enums.MgProgressionType;
import com.noslen.training_tracker.mapper.muscle_group.ProgressionMapper;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;
import com.noslen.training_tracker.repository.muscle_group.ProgressionRepo;

@ExtendWith(MockitoExtension.class)
public class ProgressionServiceTests {

    @Mock
    private ProgressionRepo repo;

    @Mock
    private ProgressionMapper mapper;

    @Mock
    private MuscleGroupRepo muscleGroupRepo;

    @InjectMocks
    private ProgressionServiceImpl service;

    private MuscleGroup testMuscleGroup;

    @BeforeEach
    void setUp() {
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
        Progression existingEntity = new Progression(1L, testMuscleGroup, MgProgressionType.REGULAR, null);
        Progression updatedEntity = new Progression(1L, testMuscleGroup, MgProgressionType.SECONDARY, null);
        ProgressionResponse expectedPayload = new ProgressionResponse(1L, 1L, MgProgressionType.SECONDARY);

        when(repo.findById(progressionId)).thenReturn(Optional.of(existingEntity));
        when(mapper.updateEntity(existingEntity, updatePayload)).thenReturn(updatedEntity);
        when(repo.save(updatedEntity)).thenReturn(updatedEntity);
        when(mapper.toPayload(updatedEntity)).thenReturn(expectedPayload);

        ProgressionResponse result = service.updateProgression(progressionId, updatePayload);

        assertEquals(expectedPayload, result);
        verify(repo).findById(progressionId);
        verify(mapper).updateEntity(existingEntity, updatePayload);
        verify(repo).save(updatedEntity);
        verify(mapper).toPayload(updatedEntity);
    }

    @Test
    void testDeleteProgression() {
        Long progressionId = 1L;
        when(repo.existsById(progressionId)).thenReturn(true);

        service.deleteProgression(progressionId);
        verify(repo).existsById(progressionId);
        verify(repo).deleteById(progressionId);
    }

    @Test
    void testGetProgression() {
        Long progressionId = 1L;
        Progression entity = new Progression(1L, testMuscleGroup, MgProgressionType.REGULAR, null);
        ProgressionResponse expectedPayload = new ProgressionResponse(1L, 1L, MgProgressionType.REGULAR);

        when(repo.findById(progressionId)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        ProgressionResponse result = service.getProgression(progressionId);

        assertEquals(expectedPayload, result);
        verify(repo).findById(progressionId);
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