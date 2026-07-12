package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.day.request.CreateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.mapper.day.DayMuscleGroupMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.enums.MgName;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import jakarta.persistence.EntityManager;
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
    private EntityManager entityManager;

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
        CreateDayMuscleGroupRequest request = new CreateDayMuscleGroupRequest(dayId, muscleGroupId, null, null, 10);
        DayMuscleGroup savedEntity = DayMuscleGroup.builder().id(1L).day(day).muscleGroup(muscleGroup).build();
        DayMuscleGroupResponse expectedPayload = new DayMuscleGroupResponse(1L, dayId, muscleGroupId, null, null, null,
                                                                            Instant.now(), Instant.now(), 10, null);

        when(entityManager.getReference(Day.class, dayId)).thenReturn(day);
        when(entityManager.getReference(MuscleGroup.class, muscleGroupId)).thenReturn(muscleGroup);
        when(repo.save(any(DayMuscleGroup.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayMuscleGroupResponse result = service.createDayMuscleGroup(request);

        // Assert
        assertEquals(expectedPayload, result);
        verify(entityManager, times(1)).getReference(Day.class, dayId);
        verify(entityManager, times(1)).getReference(MuscleGroup.class, muscleGroupId);
        verify(repo, times(1)).save(any(DayMuscleGroup.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void updateDayMuscleGroup() {
        // Arrange
        Long id = 1L;
        UpdateDayMuscleGroupRequest request = new UpdateDayMuscleGroupRequest(id, 1L, 2L, 8, 3, 7,
                                                                              Instant.now(), 12, "complete");
        DayMuscleGroup existingEntity = DayMuscleGroup.builder().id(id).pump(6).soreness(2).workload(5).build();
        DayMuscleGroupResponse expectedPayload = new DayMuscleGroupResponse(id, 1L, 2L, 8, 3, 7,
                                                                            Instant.now(), Instant.now(), 12, "complete");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        Day day = Day.builder().id(1L).build();
        MuscleGroup muscleGroup = new MuscleGroup(2L, MgName.CHEST, null, null);
        when(entityManager.getReference(Day.class, 1L)).thenReturn(day);
        when(entityManager.getReference(MuscleGroup.class, 2L)).thenReturn(muscleGroup);
        when(repo.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toPayload(existingEntity)).thenReturn(expectedPayload);

        // Act
        DayMuscleGroupResponse result = service.updateDayMuscleGroup(id, request);

        // Assert
        assertEquals(expectedPayload, result);
        // Mutable scalar fields applied directly to the managed entity
        assertEquals(8, existingEntity.getPump());
        assertEquals(3, existingEntity.getSoreness());
        assertEquals(7, existingEntity.getWorkload());
        assertEquals(12, existingEntity.getRecommendedSets());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(existingEntity);
        verify(mapper, times(1)).toPayload(existingEntity);
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
        DayMuscleGroupResponse expectedPayload = new DayMuscleGroupResponse(id, 1L, 2L, 7, 4, 6,
                                                                            Instant.now(), Instant.now(), 10, "in_progress");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayMuscleGroupResponse result = service.getDayMuscleGroup(id);
        
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
        
        List<DayMuscleGroupResponse> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayMuscleGroupResponse(1L, dayId, 2L, 7, 4, 6,
                                                        Instant.now(), Instant.now(), 10, "in_progress"));
        expectedPayloads.add(new DayMuscleGroupResponse(2L, dayId, 3L, 8, 3, 7,
                                                        Instant.now(), Instant.now(), 12, "complete"));
        
        when(repo.findByDay_Id(dayId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayMuscleGroupResponse> result = service.getDayMuscleGroupsByDayId(dayId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDay_Id(dayId);
        verify(mapper, times(1)).toPayloadList(entities);
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
        UpdateDayMuscleGroupRequest request = new UpdateDayMuscleGroupRequest(id, 1L, 2L, 8, 3, 7,
                                                                              Instant.now(), 12, "complete");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDayMuscleGroup(id, request));
        verify(repo, times(1)).findById(id);
    }
}
