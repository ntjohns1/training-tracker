package com.noslen.training_tracker.service.muscle_group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.ProgressionRepo;
import com.noslen.training_tracker.util.MgProgressionType;

public class ProgressionServiceTests {

    @Mock
    private ProgressionRepo repo;

    @InjectMocks
    private ProgressionServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProgression() {
        Progression progression = new Progression();
        when(repo.save(progression)).thenReturn(progression);

        Progression result = service.createProgression(progression);
        assertEquals(progression, result);
        verify(repo, times(1)).save(progression);
    }

    @Test
    void testUpdateProgression() {
        // Arrange
        Long id = 1L;
        Progression existingProgression = new Progression();
        Progression newProgression = new Progression();
        newProgression.setMgProgressionType(MgProgressionType.regular);

        // Mock the findById to return the existing progression
        when(repo.findById(id)).thenReturn(Optional.of(existingProgression));
        when(repo.save(existingProgression)).thenReturn(existingProgression);

        // Act
        Progression result = service.updateProgression(id, newProgression);

        // Assert
        assertEquals(existingProgression, result);
        assertEquals(MgProgressionType.regular, existingProgression.getMgProgressionType());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(existingProgression);
    }

    @Test
    void testDeleteProgression() {
        Long id = 1L;
        Progression progression = new Progression();
        when(repo.findById(id)).thenReturn(Optional.of(progression));

        service.deleteProgression(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void testGetProgression() {
        Long id = 1L;
        Progression progression = new Progression();
        when(repo.findById(id)).thenReturn(Optional.of(progression));

        Progression result = service.getProgression(id);
        assertEquals(progression, result);
        verify(repo, times(1)).findById(id);
    }
}