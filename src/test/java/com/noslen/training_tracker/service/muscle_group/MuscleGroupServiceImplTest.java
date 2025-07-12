package com.noslen.training_tracker.service.muscle_group;

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

class MuscleGroupServiceImplTest {

    @Mock
    private MuscleGroupRepo repo;

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
        List<MuscleGroup> expected = Arrays.asList(mg1, mg2);
        when(repo.findAll()).thenReturn(expected);

        List<MuscleGroup> result = service.getAllMuscleGroups();
        assertEquals(expected, result);
        verify(repo, times(1)).findAll();
    }

    @Test
    void testGetMuscleGroupById_Found() {
        MuscleGroup mg = new MuscleGroup();
        when(repo.findById(1L)).thenReturn(Optional.of(mg));

        MuscleGroup result = service.getMuscleGroupById(1L);
        assertEquals(mg, result);
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void testGetMuscleGroupById_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        MuscleGroup result = service.getMuscleGroupById(1L);
        assertNull(result);
        verify(repo, times(1)).findById(1L);
    }
}
