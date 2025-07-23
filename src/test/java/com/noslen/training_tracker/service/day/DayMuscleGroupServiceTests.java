package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.mapper.day.DayMuscleGroupMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.model.muscle_group.types.MgName;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.day.DayRepo;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class DayMuscleGroupServiceTests {

    @Mock
    private DayMuscleGroupRepo repo;
    
    @Mock
    private DayRepo dayRepo;
    
    @Mock
    private MuscleGroupRepo muscleGroupRepo;
    
    @Mock
    private DayMuscleGroupMapper mapper;

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
        // Arrange
        Long dayId = 1L;
        Long muscleGroupId = 2L;
        Day day = Day.builder().id(dayId).build();
        MuscleGroup muscleGroup = new MuscleGroup(muscleGroupId, MgName.CHEST, null, null);
        DayMuscleGroup savedEntity = DayMuscleGroup.builder().id(1L).day(day).muscleGroup(muscleGroup).build();
        DayMuscleGroupPayload expectedPayload = new DayMuscleGroupPayload(1L, dayId, muscleGroupId, null, null, null, 
                Instant.now(), Instant.now(), null, null);
        
        when(dayRepo.findById(dayId)).thenReturn(Optional.of(day));
        when(muscleGroupRepo.findById(muscleGroupId)).thenReturn(Optional.of(muscleGroup));
        when(repo.save(any(DayMuscleGroup.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayMuscleGroupPayload result = service.createDayMuscleGroup(dayId, muscleGroupId);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(dayRepo, times(1)).findById(dayId);
        verify(muscleGroupRepo, times(1)).findById(muscleGroupId);
        verify(repo, times(1)).save(any(DayMuscleGroup.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void updateDayMuscleGroup() {
        // Arrange
        Long id = 1L;
        DayMuscleGroupPayload payload = new DayMuscleGroupPayload(id, 1L, 2L, 8, 3, 7, 
                Instant.now(), Instant.now(), 12, "completed");
        DayMuscleGroup existingEntity = DayMuscleGroup.builder().id(id).pump(6).soreness(2).workload(5).build();
        DayMuscleGroup savedEntity = DayMuscleGroup.builder().id(id).pump(8).soreness(3).workload(7).build();
        DayMuscleGroupPayload expectedPayload = new DayMuscleGroupPayload(id, 1L, 2L, 8, 3, 7, 
                Instant.now(), Instant.now(), 12, "completed");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repo.save(any(DayMuscleGroup.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayMuscleGroupPayload result = service.updateDayMuscleGroup(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(any(DayMuscleGroup.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void deleteDayMuscleGroup() {
        // Arrange
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);
        
        // Act
        service.deleteDayMuscleGroup(id);
        
        // Assert
        verify(repo, times(1)).existsById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void getDayMuscleGroup() {
        // Arrange
        Long id = 1L;
        DayMuscleGroup entity = DayMuscleGroup.builder().id(id).pump(7).soreness(4).workload(6).build();
        DayMuscleGroupPayload expectedPayload = new DayMuscleGroupPayload(id, 1L, 2L, 7, 4, 6, 
                Instant.now(), Instant.now(), 10, "in_progress");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayMuscleGroupPayload result = service.getDayMuscleGroup(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).toPayload(entity);
    }

    @Test
    void getDayMuscleGroupsByDayId() {
        // Arrange
        Long dayId = 1L;
        List<DayMuscleGroup> entities = new ArrayList<>();
        entities.add(DayMuscleGroup.builder().id(1L).pump(7).soreness(4).workload(6).build());
        entities.add(DayMuscleGroup.builder().id(2L).pump(8).soreness(3).workload(7).build());
        
        List<DayMuscleGroupPayload> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayMuscleGroupPayload(1L, dayId, 2L, 7, 4, 6, 
                Instant.now(), Instant.now(), 10, "in_progress"));
        expectedPayloads.add(new DayMuscleGroupPayload(2L, dayId, 3L, 8, 3, 7, 
                Instant.now(), Instant.now(), 12, "completed"));
        
        when(repo.findByDay_Id(dayId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayMuscleGroupPayload> result = service.getDayMuscleGroupsByDayId(dayId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDay_Id(dayId);
        verify(mapper, times(1)).toPayloadList(entities);
    }
    
    @Test
    void testCreateDayMuscleGroupDayNotFound() {
        // Arrange
        Long dayId = 1L;
        Long muscleGroupId = 2L;
        when(dayRepo.findById(dayId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.createDayMuscleGroup(dayId, muscleGroupId));
        verify(dayRepo, times(1)).findById(dayId);
    }
    
    @Test
    void testCreateDayMuscleGroupMuscleGroupNotFound() {
        // Arrange
        Long dayId = 1L;
        Long muscleGroupId = 2L;
        Day day = Day.builder().id(dayId).build();
        when(dayRepo.findById(dayId)).thenReturn(Optional.of(day));
        when(muscleGroupRepo.findById(muscleGroupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.createDayMuscleGroup(dayId, muscleGroupId));
        verify(dayRepo, times(1)).findById(dayId);
        verify(muscleGroupRepo, times(1)).findById(muscleGroupId);
    }
    
    @Test
    void testGetDayMuscleGroupNotFound() {
        // Arrange
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.getDayMuscleGroup(id));
        verify(repo, times(1)).findById(id);
    }
    
    @Test
    void testUpdateDayMuscleGroupNotFound() {
        // Arrange
        Long id = 1L;
        DayMuscleGroupPayload payload = new DayMuscleGroupPayload(id, 1L, 2L, 8, 3, 7, 
                Instant.now(), Instant.now(), 12, "completed");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDayMuscleGroup(id, payload));
        verify(repo, times(1)).findById(id);
    }
}
