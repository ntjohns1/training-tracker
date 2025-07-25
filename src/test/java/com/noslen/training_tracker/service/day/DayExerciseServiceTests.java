package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.mapper.day.DayExerciseMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.repository.day.DayExerciseRepo;
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
        DayExercisePayload inputPayload = new DayExercisePayload(
                null, 1L, 2L, 1, 0, null, null, null, 3L, null, "active"
        );
        DayExercise entity = DayExercise.builder().id(1L).build();
        DayExercise savedEntity = DayExercise.builder().id(1L).build();
        DayExercisePayload expectedPayload = new DayExercisePayload(
                1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"
        );

        when(mapper.toEntity(inputPayload)).thenReturn(entity);
        when(repo.save(any(DayExercise.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayExercisePayload result = service.createDayExercise(inputPayload);

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
        assertEquals("DayExercisePayload cannot be null", exception.getMessage());
    }

    @Test
    void updateDayExercise_Success() {
        // Arrange
        Long id = 1L;
        DayExercisePayload updatePayload = new DayExercisePayload(
                1L, 1L, 2L, 2, 1, null, null, null, 3L, null, "complete"
        );
        DayExercise existingEntity = DayExercise.builder().id(1L).build();
        DayExercise savedEntity = DayExercise.builder().id(1L).build();
        DayExercisePayload expectedPayload = new DayExercisePayload(
                1L, 1L, 2L, 2, 1, Instant.now(), Instant.now(), null, 3L, null, "complete"
        );

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(mapper).updateEntity(existingEntity, updatePayload);
        when(repo.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayExercisePayload result = service.updateDayExercise(id, updatePayload);

        // Assert
        assertNotNull(result);
        assertEquals("complete", result.status());
        verify(repo).findById(id);
        verify(mapper).updateEntity(existingEntity, updatePayload);
        verify(repo).save(existingEntity);
        verify(mapper).toPayload(savedEntity);
    }

    @Test
    void updateDayExercise_NotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        DayExercisePayload updatePayload = new DayExercisePayload(
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
        when(repo.existsById(id)).thenReturn(true);

        // Act
        service.deleteDayExercise(id);

        // Assert
        verify(repo).existsById(id);
        verify(repo).deleteById(id);
    }

    @Test
    void deleteDayExercise_NotFound_ThrowsException() {
        // Arrange
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(false);

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
        DayExercise entity = DayExercise.builder().id(1L).build();
        DayExercisePayload expectedPayload = new DayExercisePayload(
                1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"
        );

        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayExercisePayload result = service.getDayExercise(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repo).findById(id);
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
        DayExercise entity = DayExercise.builder().id(1L).build();
        DayExercisePayload expectedPayload = new DayExercisePayload(
                1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"
        );

        when(repo.findByDay_IdAndExercise_Id(dayId, exerciseId)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayExercisePayload result = service.getDayExercise(dayId, exerciseId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repo, times(1)).findByDay_IdAndExercise_Id(dayId, exerciseId);
        verify(mapper).toPayload(entity);
    }

    @Test
    void getDayExercisesByDayId_Success() {
        // Arrange
        Long dayId = 1L;
        List<DayExercise> entities = List.of(
                DayExercise.builder().id(1L).build(),
                DayExercise.builder().id(2L).build()
        );
        List<DayExercisePayload> expectedPayloads = List.of(
                new DayExercisePayload(1L, 1L, 2L, 1, 0, Instant.now(), Instant.now(), null, 3L, null, "active"),
                new DayExercisePayload(2L, 1L, 3L, 2, 0, Instant.now(), Instant.now(), null, 3L, null, "active")
        );

        when(repo.findByDay_Id(dayId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayExercisePayload> result = service.getDayExercisesByDayId(dayId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).findByDay_Id(dayId);
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
