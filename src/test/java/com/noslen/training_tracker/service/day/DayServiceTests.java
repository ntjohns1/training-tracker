package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.dto.day.DayPayload;
import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;

public class DayServiceTests {

    @Mock
    private DayRepo repo;
    
    @Mock
    private MesocycleRepo mesocycleRepo;
    
    @Mock
    private DayMapper mapper;

    @InjectMocks
    private DayServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDay() {
        // Arrange
        DayPayload payload = new DayPayload(null, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        Day entity = Day.builder().week(1).position(1).label("Day 1").build();
        Mesocycle mesocycle = Mesocycle.builder().id(1L).build();
        Day savedEntity = Day.builder().id(1L).week(1).position(1).label("Day 1").mesocycle(mesocycle).build();
        DayPayload expectedPayload = new DayPayload(1L, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        
        when(mapper.toEntity(payload)).thenReturn(entity);
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(mesocycle));
        when(repo.save(any(Day.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayPayload result = service.createDay(payload);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(mapper, times(1)).toEntity(payload);
        verify(mesocycleRepo, times(1)).findById(1L);
        verify(repo, times(1)).save(any(Day.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void updateDay() {
        // Arrange
        Long id = 1L;
        DayPayload payload = new DayPayload(id, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");
        Day existingEntity = Day.builder().id(id).week(1).position(1).label("Day 1").build();
        Day mergedEntity = Day.builder().id(id).week(1).position(1).label("Updated Day 1").bodyweight(77.0).build();
        Day savedEntity = Day.builder().id(id).week(1).position(1).label("Updated Day 1").bodyweight(77.0).build();
        DayPayload expectedPayload = new DayPayload(id, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        when(mapper.mergeEntity(existingEntity, payload)).thenReturn(mergedEntity);
        when(repo.save(mergedEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayPayload result = service.updateDay(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).mergeEntity(existingEntity, payload);
        verify(repo, times(1)).save(mergedEntity);
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void getDay() {
        // Arrange
        Long id = 1L;
        Day entity = Day.builder().id(id).week(1).position(1).label("Day 1").build();
        DayPayload expectedPayload = new DayPayload(id, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayPayload result = service.getDay(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).toPayload(entity);
    }

    @Test
    void deleteDay() {
        // Arrange
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);
        
        // Act
        service.deleteDay(id);
        
        // Assert
        verify(repo, times(1)).existsById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getDaysByMesocycleId() {
        // Arrange
        Long mesocycleId = 1L;
        List<Day> entities = new ArrayList<>();
        entities.add(Day.builder().id(1L).week(1).position(1).label("Day 1").build());
        entities.add(Day.builder().id(2L).week(1).position(2).label("Day 2").build());
        
        List<DayPayload> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayPayload(1L, mesocycleId, 1L, 1L, Instant.now(), Instant.now(), 
                75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned"));
        expectedPayloads.add(new DayPayload(2L, mesocycleId, 1L, 2L, Instant.now(), Instant.now(), 
                75, Instant.now(), "kg", null, "Day 2", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned"));
        
        when(repo.findByMesocycleId(mesocycleId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayPayload> result = service.getDaysByMesocycleId(mesocycleId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByMesocycleId(mesocycleId);
        verify(mapper, times(1)).toPayloadList(entities);
    }
    
    @Test
    void testGetDayNotFound() {
        // Arrange
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.getDay(id));
        verify(repo, times(1)).findById(id);
    }
    
    @Test
    void testUpdateDayNotFound() {
        // Arrange
        Long id = 1L;
        DayPayload payload = new DayPayload(id, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDay(id, payload));
        verify(repo, times(1)).findById(id);
    }
    
    @Test
    void testCreateDayMesocycleNotFound() {
        // Arrange
        DayPayload payload = new DayPayload(null, 1L, 1L, 1L, Instant.now(), Instant.now(), 
                75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        Day entity = Day.builder().week(1).position(1).label("Day 1").build();
        
        when(mapper.toEntity(payload)).thenReturn(entity);
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.createDay(payload));
        verify(mapper, times(1)).toEntity(payload);
        verify(mesocycleRepo, times(1)).findById(1L);
    }
}
