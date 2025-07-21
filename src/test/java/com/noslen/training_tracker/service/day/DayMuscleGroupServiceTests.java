package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class DayMuscleGroupServiceTests {

    @Mock
    private DayMuscleGroupRepo repo;

    @InjectMocks
    private DayMuscleGroupServiceImpl service;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createDayMuscleGroup() {
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder().build();
        when(repo.save(dayMuscleGroup)).thenReturn(dayMuscleGroup);
        assertEquals(dayMuscleGroup, service.createDayMuscleGroup(1L, 1L));
    }

    @Test
    void updateDayMuscleGroup() {
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder().build();
        when(repo.findById(1L)).thenReturn(Optional.of(dayMuscleGroup));
        when(repo.save(dayMuscleGroup)).thenReturn(dayMuscleGroup);
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void deleteDayMuscleGroup() {
        service.deleteDayMuscleGroup(1L);
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void getDayMuscleGroup() {
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder().build();
        when(repo.findById(1L)).thenReturn(Optional.of(dayMuscleGroup));
        assertEquals(dayMuscleGroup, service.getDayMuscleGroup(1L));
    }

    @Test
    void getDayMuscleGroupsByDayId() {
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder()
                .build();
        when(repo.findByDayId(1L)).thenReturn(List.of(dayMuscleGroup));
        assertEquals(List.of(dayMuscleGroup),
                     service.getDayMuscleGroupsByDayId(1L));

    }
}
