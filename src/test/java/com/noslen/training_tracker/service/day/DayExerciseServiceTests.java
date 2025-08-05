package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.mapper.day.DayExerciseMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.day.DayExerciseRepo;
import com.noslen.training_tracker.security.UserContext;
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
    private DayExerciseRepo repo;

    @Mock
    private DayExerciseMapper mapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private DayExerciseServiceImpl service;

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
    void createDayExercise_Success() {
        // Arrange
        DayExerciseResponse inputPayload = new DayExerciseResponse(
                null, 1L, 2L, 1, 0, null, null, null, 3L, null, "active"
        );
        DayExercise entity = DayExercise.builder().id(1L).build();
        DayExercise savedEntity = DayExercise.builder().id(1L).build();
        DayExerciseResponse expectedPayload = new DayExerciseResponse(
                1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"
        );

        when(mapper.toEntity(inputPayload)).thenReturn(entity);
        when(repo.save(any(DayExercise.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayExerciseResponse result = service.createDayExercise(inputPayload);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(mapper).toEntity(inputPayload);
        verify(repo).save(any(DayExercise.class));
        verify(mapper).toPayload(savedEntity);
    }

    @Test
    void createDayExercise_NullPayload_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createDayExercise(null)
        );
        assertEquals("DayExerciseResponse cannot be null", exception.getMessage());
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
        
        DayExerciseResponse updatePayload = new DayExerciseResponse(
                1L, 1L, 2L, 2, 1, null, null, null, 3L, null, "complete"
        );
        
        DayExercise existingEntity = DayExercise.builder()
                .id(1L)
                .day(day)
                .build();
                
        DayExercise savedEntity = DayExercise.builder()
                .id(1L)
                .day(day)
                .build();
                
        DayExerciseResponse expectedPayload = new DayExerciseResponse(
                1L, 1L, 2L, 2, 1, Instant.now(), Instant.now(), null, 3L, null, "complete"
        );

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        doNothing().when(mapper).updateEntity(existingEntity, updatePayload);
        when(repo.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayExerciseResponse result = service.updateDayExercise(id, updatePayload);

        // Assert
        assertNotNull(result);
        assertEquals("complete", result.status());
        verify(repo).findById(id);
        verify(userContext).validateUserAccess(100L);
        verify(mapper).updateEntity(existingEntity, updatePayload);
        verify(repo).save(existingEntity);
        verify(mapper).toPayload(savedEntity);
    }

    @Test
    void updateDayExercise_NotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        DayExerciseResponse updatePayload = new DayExerciseResponse(
                1L, 1L, 2L, 2, 1, null, null, null, 3L, null, "complete"
        );

        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.updateDayExercise(id, updatePayload)
        );
        assertEquals("DayExercise not found with id: 1", exception.getMessage());
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
