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

import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.factory.DayFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.mapper.day.DayMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.day.DayRepo;

public class DayServiceTests {

    @Mock
    private DayRepo dayRepo;
    
    @Mock
    private DayMapper dayMapper;

    @Mock
    private DayFactory dayFactory;

    @InjectMocks
    private DayServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDay() {
        // Arrange
        DayResponse payload = new DayResponse(null, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                              75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        Mesocycle mesocycle = Mesocycle.builder().id(1L).build();
        Day createdEntity = Day.builder().week(1).position(1).label("Day 1").mesocycle(mesocycle).build();
        Day savedEntity = Day.builder().id(1L).week(1).position(1).label("Day 1").mesocycle(mesocycle).build();
        DayResponse expectedPayload = new DayResponse(1L, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                                      75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        
        when(dayFactory.createFromResponse(payload)).thenReturn(createdEntity);
        when(dayRepo.save(any(Day.class))).thenReturn(savedEntity);
        when(dayMapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayResponse result = service.createDay(payload);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(dayFactory, times(1)).createFromResponse(payload);
        verify(dayRepo, times(1)).save(any(Day.class));
        verify(dayMapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void updateDay() {
        // Arrange
        Long id = 1L;
        DayResponse payload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                              77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");
        Day existingEntity = Day.builder().id(id).week(1).position(1).label("Day 1").build();
        Day mergedEntity = Day.builder().id(id).week(1).position(1).label("Updated Day 1").bodyweight(77.0).build();
        Day savedEntity = Day.builder().id(id).week(1).position(1).label("Updated Day 1").bodyweight(77.0).build();
        DayResponse expectedPayload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                                      77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");

        when(dayRepo.findById(id)).thenReturn(Optional.of(existingEntity));
        when(dayMapper.mergeEntity(existingEntity, payload)).thenReturn(mergedEntity);
        when(dayRepo.save(mergedEntity)).thenReturn(savedEntity);
        when(dayMapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayResponse result = service.updateDay(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(dayRepo, times(1)).findById(id);
        verify(dayMapper, times(1)).mergeEntity(existingEntity, payload);
        verify(dayRepo, times(1)).save(mergedEntity);
        verify(dayMapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void getDay() {
        // Arrange
        Long id = 1L;
        Day entity = Day.builder().id(id).week(1).position(1).label("Day 1").build();
        DayResponse expectedPayload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                                      75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        
        when(dayRepo.findById(id)).thenReturn(Optional.of(entity));
        when(dayMapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayResponse result = service.getDay(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(dayRepo, times(1)).findById(id);
        verify(dayMapper, times(1)).toPayload(entity);
    }

    @Test
    void deleteDay() {
        // Arrange
        Long id = 1L;
        when(dayRepo.existsById(id)).thenReturn(true);
        
        // Act
        service.deleteDay(id);
        
        // Assert
        verify(dayRepo, times(1)).existsById(id);
        verify(dayRepo, times(1)).deleteById(id);
    }

    @Test
    void getDaysByMesocycleId() {
        // Arrange
        Long mesocycleId = 1L;
        List<Day> entities = new ArrayList<>();
        entities.add(Day.builder().id(1L).week(1).position(1).label("Day 1").build());
        entities.add(Day.builder().id(2L).week(1).position(2).label("Day 2").build());
        
        List<DayResponse> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayResponse(1L, mesocycleId, 1L, 1L, Instant.now(), Instant.now(),
                                             75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned"));
        expectedPayloads.add(new DayResponse(2L, mesocycleId, 1L, 2L, Instant.now(), Instant.now(),
                                             75, Instant.now(), "kg", null, "Day 2", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned"));
        
        when(dayRepo.findByMesocycleId(mesocycleId)).thenReturn(entities);
        when(dayMapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayResponse> result = service.getDaysByMesocycleId(mesocycleId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(dayRepo, times(1)).findByMesocycleId(mesocycleId);
        verify(dayMapper, times(1)).toPayloadList(entities);
    }
    
    @Test
    void testGetDayNotFound() {
        // Arrange
        Long id = 1L;
        when(dayRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.getDay(id));
        verify(dayRepo, times(1)).findById(id);
    }
    
    @Test
    void testUpdateDayNotFound() {
        // Arrange
        Long id = 1L;
        DayResponse payload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                              77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");
        when(dayRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDay(id, payload));
        verify(dayRepo, times(1)).findById(id);
    }
    
    @Test
    void testCreateDayMesocycleNotFound() {
        // Arrange
        DayResponse payload = new DayResponse(null, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                              75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        
        when(dayFactory.createFromResponse(payload)).thenThrow(new RuntimeException("Mesocycle not found with id: 1"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.createDay(payload));
        verify(dayFactory, times(1)).createFromResponse(payload);
    }
}
