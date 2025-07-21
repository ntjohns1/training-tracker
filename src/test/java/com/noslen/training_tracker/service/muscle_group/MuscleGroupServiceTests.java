package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.MuscleGroupPayload;
import com.noslen.training_tracker.mapper.muscle_group.MuscleGroupMapper;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MuscleGroupServiceTests {

    @Mock
    private MuscleGroupRepo repo;

    @Mock
    private MuscleGroupMapper mapper;

    @InjectMocks
    private MuscleGroupServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMuscleGroups() {
        MuscleGroup mg1 = new MuscleGroup();
        MuscleGroup mg2 = new MuscleGroup();
        List<MuscleGroup> entities = Arrays.asList(mg1, mg2);
        
        MuscleGroupPayload payload1 = new MuscleGroupPayload(1L, "Chest", null, null);
        MuscleGroupPayload payload2 = new MuscleGroupPayload(2L, "Back", null, null);
        List<MuscleGroupPayload> expected = Arrays.asList(payload1, payload2);
        
        when(repo.findAll()).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expected);

        List<MuscleGroupPayload> result = service.getAllMuscleGroups();
        assertEquals(expected, result);
        verify(repo, times(1)).findAll();
        verify(mapper, times(1)).toPayloadList(entities);
    }

    @Test
    void testGetMuscleGroupById_Found() {
        MuscleGroup mg = new MuscleGroup();
        MuscleGroupPayload expected = new MuscleGroupPayload(1L, "Chest", null, null);
        
        when(repo.findById(1L)).thenReturn(Optional.of(mg));
        when(mapper.toPayload(mg)).thenReturn(expected);

        MuscleGroupPayload result = service.getMuscleGroupById(1L);
        assertEquals(expected, result);
        verify(repo, times(1)).findById(1L);
        verify(mapper, times(1)).toPayload(mg);
    }

    @Test
    void testGetMuscleGroupById_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getMuscleGroupById(1L));
        verify(repo, times(1)).findById(1L);
    }
}
