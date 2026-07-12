package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.request.CreateDayRequest;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.factory.DayFactory;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    private DayRepo repo;
    
    @Mock
    private DayMapper dayMapper;

    @Mock
    private DayFactory dayFactory;

    @Mock
    private UserContext userContext;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DayServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDay() {
        // Arrange
        Instant now = Instant.now();
        CreateDayRequest payload = new CreateDayRequest(1L, 1, 1, now, now, "kg", "ready");
        Mesocycle mesocycle = Mesocycle.builder().id(1L).userId(100L).build();
        Day createdEntity = Day.builder().week(1).position(1).label("Day 1").mesocycle(mesocycle).build();
        Day savedEntity = Day.builder().id(1L).week(1).position(1).label("Day 1").mesocycle(mesocycle).build();
        DayResponse expectedPayload = new DayResponse(1L, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                                      75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "ready");
        
        // Mock entity manager reference
        when(dayFactory.createFromRequest(payload)).thenReturn(createdEntity);
        when(repo.save(any(Day.class))).thenReturn(savedEntity);
        when(dayMapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayResponse result = service.createDay(payload);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(dayFactory, times(1)).createFromRequest(payload);
        verify(repo, times(1)).save(any(Day.class));
        verify(dayMapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void updateDay() {
        // Arrange
        Long id = 1L;
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        DayResponse payload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                              77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");
        
        Day existingEntity = Day.builder()
                .id(id)
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .label("Day 1")
                .build();
                
        Day mergedEntity = Day.builder()
                .id(id)
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .label("Updated Day 1")
                .bodyweight(77.0)
                .build();
                
        Day savedEntity = Day.builder()
                .id(id)
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .label("Updated Day 1")
                .bodyweight(77.0)
                .build();
                
        DayResponse expectedPayload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                                      77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(repo.save(any(Day.class))).thenReturn(savedEntity);
        when(dayMapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayResponse result = service.updateDay(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).save(any(Day.class));
        verify(dayMapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void getDay() {
        // Arrange
        Long id = 1L;
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
        
        Day entity = Day.builder()
                .id(id)
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .label("Day 1")
                .build();
                
        DayResponse expectedPayload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                                      75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(dayMapper.toPayload(entity)).thenReturn(expectedPayload);
        doNothing().when(userContext).validateUserAccess(100L);

        // Act
        DayResponse result = service.getDay(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(dayMapper, times(1)).toPayload(entity);
    }

    @Test
    void deleteDay() {
        // Arrange
        Long id = 1L;
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        Day entity = Day.builder()
                .id(id)
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .label("Day 1")
                .build();
                
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        
        // Act
        service.deleteDay(id);
        
        // Assert
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getDaysByMesocycleId() {
        // Arrange
        Long mesocycleId = 1L;
        Mesocycle mesocycle = Mesocycle.builder()
                .id(mesocycleId)
                .userId(100L)
                .build();
                
        List<Day> entities = new ArrayList<>();
        entities.add(Day.builder()
                .id(1L)
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .label("Day 1")
                .build());
        entities.add(Day.builder()
                .id(2L)
                .mesocycle(mesocycle)
                .week(1)
                .position(2)
                .label("Day 2")
                .build());
        
        List<DayResponse> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayResponse(1L, mesocycleId, 1L, 1L, Instant.now(), Instant.now(),
                                             75, Instant.now(), "kg", null, "Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned"));
        expectedPayloads.add(new DayResponse(2L, mesocycleId, 1L, 2L, Instant.now(), Instant.now(),
                                             75, Instant.now(), "kg", null, "Day 2", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "planned"));
        
        when(repo.findByMesocycleId(mesocycleId)).thenReturn(entities);
        when(dayMapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayResponse> result = service.getDaysByMesocycleId(mesocycleId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByMesocycleId(mesocycleId);
        verify(dayMapper, times(1)).toPayloadList(entities);
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
        DayResponse payload = new DayResponse(id, 1L, 1L, 1L, Instant.now(), Instant.now(),
                                              77, Instant.now(), "kg", Instant.now(), "Updated Day 1", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "complete");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDay(id, payload));
        verify(repo, times(1)).findById(id);
    }
    
    @Test
    void testCreateDayWithEntityManagerReference() {
        // Arrange
        Instant now = Instant.now();
        Long mesocycleId = 1L;
        CreateDayRequest payload = new CreateDayRequest(mesocycleId, 1, 1, now, now, "kg", "ready");
        
        // Mock EntityManager throwing exception when reference is accessed
        Mesocycle mesocycle = Mesocycle.builder().id(mesocycleId).build();
        Day day = Day.builder()
                .mesocycle(mesocycle)
                .week(1)
                .position(1)
                .createdAt(now)
                .updatedAt(now)
                .label("ready")
                .build();
                
        // Mock factory to throw exception when creating entity
        when(dayFactory.createFromRequest(payload)).thenThrow(new RuntimeException("Entity reference error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.createDay(payload));
        verify(dayFactory, times(1)).createFromRequest(payload);
        // Repo save should not be called due to exception
        verify(repo, never()).save(any(Day.class));
    }
}
