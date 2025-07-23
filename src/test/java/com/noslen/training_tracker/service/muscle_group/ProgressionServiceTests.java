package com.noslen.training_tracker.service.muscle_group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;
import com.noslen.training_tracker.mapper.muscle_group.ProgressionMapper;
import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.ProgressionRepo;
import com.noslen.training_tracker.model.muscle_group.types.MgProgressionType;

public class ProgressionServiceTests {

    @Mock
    private ProgressionRepo repo;

    @Mock
    private ProgressionMapper mapper;

    @InjectMocks
    private ProgressionServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProgression() {
        ProgressionPayload inputPayload = new ProgressionPayload(0L, 1L, MgProgressionType.REGULAR);
        Progression entity = new Progression(null, 1L, MgProgressionType.REGULAR, null);
        Progression savedEntity = new Progression(1L, 1L, MgProgressionType.REGULAR, null);
        ProgressionPayload expectedPayload = new ProgressionPayload(1L, 1L, MgProgressionType.REGULAR);
        
        when(mapper.toEntity(inputPayload)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        ProgressionPayload result = service.createProgression(inputPayload);
        assertEquals(expectedPayload, result);
        verify(mapper, times(1)).toEntity(inputPayload);
        verify(repo, times(1)).save(entity);
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void testUpdateProgression() {
        // Arrange
        Long id = 1L;
        Progression existingProgression = new Progression(1L, 1L, MgProgressionType.REGULAR, null);
        ProgressionPayload updatePayload = new ProgressionPayload(0L, 0L, MgProgressionType.SECONDARY);
        Progression savedProgression = new Progression(1L, 1L, MgProgressionType.SECONDARY, null);
        ProgressionPayload expectedPayload = new ProgressionPayload(1L, 1L, MgProgressionType.SECONDARY);

        // Mock the findById to return the existing progression
        when(repo.findById(id)).thenReturn(Optional.of(existingProgression));
        when(repo.save(existingProgression)).thenReturn(savedProgression);
        when(mapper.toPayload(savedProgression)).thenReturn(expectedPayload);

        // Act
        ProgressionPayload result = service.updateProgression(id, updatePayload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).updateEntity(existingProgression, updatePayload);
        verify(repo, times(1)).save(existingProgression);
        verify(mapper, times(1)).toPayload(savedProgression);
    }

    @Test
    void testDeleteProgression() {
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);

        service.deleteProgression(id);
        verify(repo, times(1)).existsById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testGetProgression() {
        Long id = 1L;
        Progression progression = new Progression(1L, 1L, MgProgressionType.REGULAR, null);
        ProgressionPayload expectedPayload = new ProgressionPayload(1L, 1L, MgProgressionType.REGULAR);
        
        when(repo.findById(id)).thenReturn(Optional.of(progression));
        when(mapper.toPayload(progression)).thenReturn(expectedPayload);

        ProgressionPayload result = service.getProgression(id);
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).toPayload(progression);
    }
    
    @Test
    void testGetProgression_NotFound() {
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getProgression(id));
        verify(repo, times(1)).findById(id);
    }
}