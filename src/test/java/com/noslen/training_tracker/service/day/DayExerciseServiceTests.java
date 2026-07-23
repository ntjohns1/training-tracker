package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.mapper.day.DayExerciseMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayExerciseRepo;
import com.noslen.training_tracker.security.UserContext;
import com.noslen.training_tracker.util.EnumConverter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.Mockito.doNothing;

public class DayExerciseServiceTests {

    @Mock
    private EntityManager entityManager;

    @Mock
    private DayExerciseRepo repo;

    @Mock
    private DayExerciseMapper mapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private DayExerciseServiceImpl service;

    private AutoCloseable closeable;
    private Instant now = Instant.now();

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createDayExercise_Success() {
        Instant now = Instant.now();
        CreateDayExerciseRequest createRequest = new CreateDayExerciseRequest(
                1L, 2L, 1, 0, now, now, null, 3L
        );

        Day day = Day.builder().id(1L).build();
        Exercise exercise = new Exercise();
        exercise.setId(2L);
        MuscleGroup muscleGroup = new MuscleGroup();
        muscleGroup.setId(3L);

        DayExercise savedEntity = DayExercise.builder()
                .id(1L)
                .day(day)
                .exercise(exercise)
                .position(1)
                .jointPain(0)
                .createdAt(now)
                .updatedAt(now)
                .muscleGroup(muscleGroup)
                .build();

        DayExerciseResponse expectedPayload = new DayExerciseResponse(
                1L, 1L, 2L, 1, 0, now, now, null, 3L, null, "active"
        );

        // Mock EntityManager references
        when(entityManager.getReference(Day.class, 1L)).thenReturn(day);
        when(entityManager.getReference(Exercise.class, 2L)).thenReturn(exercise);
        when(entityManager.getReference(MuscleGroup.class, 3L)).thenReturn(muscleGroup);

        when(repo.save(any(DayExercise.class))).thenReturn(savedEntity);
        when(mapper.toPayload(any(DayExercise.class))).thenReturn(expectedPayload);

        // Act
        DayExerciseResponse result = service.createDayExercise(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(entityManager).getReference(Day.class, 1L);
        verify(entityManager).getReference(Exercise.class, 2L);
        verify(entityManager).getReference(MuscleGroup.class, 3L);
        verify(repo).save(any(DayExercise.class));
        verify(mapper).toPayload(any(DayExercise.class));
    }

    @Test
    void createDayExercise_NullPayload_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createDayExercise(null)
        );
        assertEquals("CreateDayExerciseRequest cannot be null", exception.getMessage());
    }

    @Test
    void updateDayExercise_Success() {
        // Arrange
        Long id = 1L;
        
        // Create proper entity relationships for security validation
        Mesocycle mesocycle = 
            Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        Day day = 
            Day.builder()
                .id(5L)
                .mesocycle(mesocycle)
                .build();
        
        UpdateDayExerciseRequest updateRequest = new UpdateDayExerciseRequest(
                1L, 1L, 2L, 2, 1, now, null, 3L, "complete"
        );

        DayExercise existingEntity = DayExercise.builder()
                .id(1L)
                .day(day)
                .position(1)
                .jointPain(0)
                .build();

        Day dayRef = Day.builder().id(1L).build();
        Exercise exerciseRef = new Exercise();
        exerciseRef.setId(2L);
        MuscleGroup muscleGroupRef = new MuscleGroup();
        muscleGroupRef.setId(3L);

        DayExerciseResponse expectedPayload = new DayExerciseResponse(
                1L, 1L, 2L, 2, 1, now, now, null, 3L, null, "complete"
        );

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(entityManager.getReference(Day.class, 1L)).thenReturn(dayRef);
        when(entityManager.getReference(Exercise.class, 2L)).thenReturn(exerciseRef);
        when(entityManager.getReference(MuscleGroup.class, 3L)).thenReturn(muscleGroupRef);
        when(repo.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toPayload(existingEntity)).thenReturn(expectedPayload);

        // Act
        DayExerciseResponse result = service.updateDayExercise(id, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("complete", result.status());
        // Mutable scalar fields are applied directly to the managed entity
        assertEquals(2, existingEntity.getPosition());
        assertEquals(1, existingEntity.getJointPain());
        assertEquals("complete", EnumConverter.enumToString(existingEntity.getStatus()));
        verify(repo).findById(id);
        verify(userContext).validateUserAccess(100L);
        verify(entityManager).getReference(MuscleGroup.class, 3L);
        verify(repo).save(existingEntity);
        verify(mapper).toPayload(existingEntity);
    }

    @Test
    void updateDayExercise_NullId_ThrowsException() {
        // Arrange
        UpdateDayExerciseRequest updateRequest = new UpdateDayExerciseRequest(
                1L, 1L, 2L, 2, 1, now, null, 3L, "complete"
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateDayExercise(null, updateRequest)
        );
        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    void updateDayExercise_NullPayload_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateDayExercise(1L, null)
        );
        assertEquals("UpdateDayExerciseRequest cannot be null", exception.getMessage());
    }

    @Test
    void updateDayExercise_EntityNotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        UpdateDayExerciseRequest updateRequest = new UpdateDayExerciseRequest(
                1L, 1L, 2L, 2, 1, now, null, 3L, "complete"
        );
        
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.updateDayExercise(id, updateRequest)
        );
        assertEquals("DayExercise not found with id: " + id, exception.getMessage());
    }

    @Test
    void deleteDayExercise_Success() {
        // Arrange
        Long id = 1L;
        
        // Create proper entity relationships for security validation
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        Day day = Day.builder()
                .id(5L)
                .mesocycle(mesocycle)
                .build();
        
        DayExercise entity = DayExercise.builder()
                .id(1L)
                .day(day)
                .build();
                
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);

        // Act
        service.deleteDayExercise(id);

        // Assert
        verify(repo).findById(id);
        verify(userContext).validateUserAccess(100L);
        verify(repo).deleteById(id);
    }

    @Test
    void deleteDayExercise_NotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.deleteDayExercise(id)
        );
        assertEquals("DayExercise not found with id: 1", exception.getMessage());
    }

    @Test
    void getDayExercise_Success() {
        // Arrange
        Long id = 1L;
        
        // Create proper entity relationships for security validation
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        Day day = Day.builder()
                .id(5L)
                .mesocycle(mesocycle)
                .build();
        
        DayExercise entity = DayExercise.builder()
                .id(1L)
                .day(day)
                .build();
                
        DayExerciseResponse expectedPayload = new DayExerciseResponse(
                1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"
        );

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayExerciseResponse result = service.getDayExercise(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repo).findById(id);
        verify(userContext).validateUserAccess(100L);
        verify(mapper).toPayload(entity);
    }

    @Test
    void getDayExercise_NotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.getDayExercise(id));
        verify(repo).findById(id);
        verify(mapper, never()).toPayload(any());
    }

    @Test
    void getDayExerciseByDayIdAndExerciseId_Success() {
        // Arrange
        Long dayId = 1L;
        Long exerciseId = 2L;
        
        // Create proper entity relationships for security validation
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        Day day = Day.builder()
                .id(dayId)
                .mesocycle(mesocycle)
                .build();
        
        DayExercise entity = DayExercise.builder()
                .id(1L)
                .day(day)
                .build();
                
        DayExerciseResponse expectedPayload = new DayExerciseResponse(
                1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"
        );

        when(repo.findByDay_IdAndExercise_Id(dayId, exerciseId)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayExerciseResponse result = service.getDayExercise(dayId, exerciseId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repo, times(1)).findByDay_IdAndExercise_Id(dayId, exerciseId);
        verify(userContext).validateUserAccess(100L);
        verify(mapper).toPayload(entity);
    }

    @Test
    void getDayExercisesByDayId_Success() {
        // Arrange
        Long dayId = 1L;
        
        // Create proper entity relationships for security validation
        Mesocycle mesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        Day day = Day.builder()
                .id(dayId)
                .mesocycle(mesocycle)
                .build();
        
        List<DayExercise> entities = List.of(
                DayExercise.builder().id(1L).day(day).build(),
                DayExercise.builder().id(2L).day(day).build()
        );
        
        List<DayExerciseResponse> expectedPayloads = List.of(
                new DayExerciseResponse(1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"),
                new DayExerciseResponse(2L, 1L, 3L, 2, 0, Instant.now(), Instant.now(), null, 3L, null, "active")
        );

        when(repo.findByDay_Id(dayId)).thenReturn(entities);
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayExerciseResponse> result = service.getDayExercisesByDayId(dayId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).findByDay_Id(dayId);
        verify(userContext).validateUserAccess(100L);
        verify(mapper).toPayloadList(entities);
    }

    @Test
    void getDayExercisesByDayId_NullDayId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDayExercisesByDayId(null)
        );
        assertEquals("Day ID cannot be null", exception.getMessage());
    }
}
