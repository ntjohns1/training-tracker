package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.repository.day.DayRepo;

public class DayServiceTests {

    @Mock
    private DayRepo repo;

    @InjectMocks
    private DayServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDay() {
        Day day = Day.builder().build();
        when(repo.save(day)).thenReturn(day);
        assertEquals(day, service.createDay(day));
    }

    @Test
    void updateDay() {
        Long id = 1L;
        Day day = Day.builder().build();
        when(repo.findById(id)).thenReturn(Optional.of(day));
        when(repo.save(day)).thenReturn(day);
        assertEquals(day, service.updateDay(id, day));
    }

    @Test
    void getDay() {
        Long id = 1L;
        Day day = Day.builder().build();
        when(repo.findById(id)).thenReturn(Optional.of(day));
        assertEquals(day, service.getDay(id));
    }

    @Test
    void deleteDay() {
        Long id = 1L;
        service.deleteDay(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getDaysByMesocycleId() {
        Long mesocycleId = 1L;
        List<Day> days = List.of(Day.builder().build());
        when(repo.findByMesocycleId(mesocycleId)).thenReturn(days);
        assertEquals(days, service.getDaysByMesocycleId(mesocycleId));
    }
}
