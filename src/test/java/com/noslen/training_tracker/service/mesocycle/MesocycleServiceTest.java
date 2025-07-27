package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.factory.MesocycleFactory;
import com.noslen.training_tracker.mapper.mesocycle.MesocycleMapper;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesocycleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MesocycleServiceTest {

    @Mock
    private MesocycleRepo mesocycleRepo;

    @Mock
    private MesocycleMapper mesocycleMapper;

    @Mock
    private MesocycleFactory mesocycleFactory;

    @InjectMocks
    private MesocycleServiceImpl mesocycleService;

    private MesocycleResponse samplePayload;
    private Mesocycle sampleEntity;
    private Instant now;
    private MesoNoteResponse notePayload;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        notePayload = new MesoNoteResponse(
                1L, 10L, 20L, now, now, "Test note"
        );

        samplePayload = new MesocycleResponse(
                1L, "test-key", 100L, "Test Mesocycle", 28, "lbs",
                2L, 3L, 5L, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Arrays.asList(notePayload)
        );

        sampleEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("test-key")
                .userId(100L)
                .name("Test Mesocycle")
                .days(28)
                .unit(Unit.LBS)
                .sourceTemplate(MesoTemplate.builder().id(2L).build())
                .sourceMeso(Mesocycle.builder().id(3L).build())
                .microRirs(5L)
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null)
                .deletedAt(null)
                .weeks(Collections.emptyList())
                .notes(Collections.emptyList())
                .status(Status.READY)
                .generatedFrom("template")
                .progressions(Collections.emptyMap())
                .build();
    }

    @Test
    void createMesocycle_Success() {
        // Given
        MesocycleResponse resultPayload = new MesocycleResponse(
                1L, "test-key", 100L, "Test Mesocycle", 28, "lbs",
                2L, 3L, 5L, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Arrays.asList(notePayload)
        );

        when(mesocycleFactory.createFromResponse(any(MesocycleResponse.class))).thenReturn(sampleEntity);
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(resultPayload);

        // When
        MesocycleResponse result = mesocycleService.createMesocycle(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.key(), result.key());
        assertEquals(samplePayload.name(), result.name());

        verify(mesocycleFactory).createFromResponse(any(MesocycleResponse.class));
        verify(mesocycleRepo).save(any(Mesocycle.class));
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void getMesocycle_Success() {
        // Given
        MesocycleResponse resultPayload = new MesocycleResponse(
                1L, "test-key", 100L, "Test Mesocycle", 28, "lbs",
                2L, 3L, 5L, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Arrays.asList(notePayload)
        );

        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.of(sampleEntity));
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(resultPayload);

        // When
        MesocycleResponse result = mesocycleService.getMesocycle(1L);

        // Then
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.name(), result.name());

        verify(mesocycleRepo).findById(any(Long.class));
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void getMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> mesocycleService.getMesocycle(1L));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(any(Long.class));
    }

    @Test
    void getMesocyclesByUserId_Success() {
        // Given
        List<MesocycleResponse> resultPayload = Arrays.asList(samplePayload);

        when(mesocycleRepo.findByUserId(any(Long.class))).thenReturn(Collections.singletonList(sampleEntity));
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(samplePayload);

        // When
        List<MesocycleResponse> result = mesocycleService.getMesocyclesByUserId(100L);

        // Then
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findByUserId(any(Long.class));
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void updateMesocycle_Success() {
        // Given
        MesocycleResponse updatedPayload = new MesocycleResponse(
                1L, "updated-key", 100L, "Updated Mesocycle", 28, "lbs",
                null, null, null, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Collections.emptyList()
        );

        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.of(sampleEntity));
        when(mesocycleMapper.mergeEntity(any(Mesocycle.class), any(MesocycleResponse.class))).thenReturn(sampleEntity);
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(updatedPayload);

        // When
        MesocycleResponse result = mesocycleService.updateMesocycle(1L, samplePayload);

        // Then
        assertEquals(1L, result.id());
        assertEquals("Updated Mesocycle", result.name());

        verify(mesocycleRepo).findById(any(Long.class));
        verify(mesocycleMapper).mergeEntity(any(Mesocycle.class), any(MesocycleResponse.class));
        verify(mesocycleRepo).save(any(Mesocycle.class));
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void updateMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> mesocycleService.updateMesocycle(1L, samplePayload));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(any(Long.class));
    }

    @Test
    void deleteMesocycle_Success() {
        // Given
        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.of(sampleEntity));
        when(mesocycleFactory.createForSoftDelete(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);

        // When
        mesocycleService.deleteMesocycle(1L);

        // Then
        verify(mesocycleRepo).findById(any(Long.class));
        verify(mesocycleFactory).createForSoftDelete(any(Mesocycle.class));
        verify(mesocycleRepo).save(any(Mesocycle.class));
    }

    @Test
    void deleteMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> mesocycleService.deleteMesocycle(1L));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(any(Long.class));
    }

    @Test
    void getAllMesocycles_Success() {
        // Given
        List<MesocycleResponse> resultPayload = Arrays.asList(samplePayload);

        when(mesocycleRepo.findAll()).thenReturn(Collections.singletonList(sampleEntity));
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(samplePayload);

        // When
        List<MesocycleResponse> result = mesocycleService.getAllMesocycles();

        // Then
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findAll();
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void getAllActiveMesocycles_Success() {
        // Given
        List<MesocycleResponse> resultPayload = Arrays.asList(samplePayload);

        when(mesocycleRepo.findByDeletedAtIsNull()).thenReturn(Collections.singletonList(sampleEntity));
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(samplePayload);

        // When
        List<MesocycleResponse> result = mesocycleService.getAllActiveMesocycles();

        // Then
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findByDeletedAtIsNull();
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void getActiveMesocyclesByUserId_Success() {
        // Given
        List<MesocycleResponse> resultPayload = Arrays.asList(samplePayload);

        when(mesocycleRepo.findByUserIdAndDeletedAtIsNull(any(Long.class))).thenReturn(Collections.singletonList(sampleEntity));
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(samplePayload);

        // When
        List<MesocycleResponse> result = mesocycleService.getActiveMesocyclesByUserId(100L);

        // Then
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findByUserIdAndDeletedAtIsNull(any(Long.class));
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void finishMesocycle_Success() {
        // Given
        MesocycleResponse finishedPayload = new MesocycleResponse(
                1L, "test-key", 100L, "Test Mesocycle", 28, "days",
                null, null, null, now, now, now, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Collections.emptyList()
        );

        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.of(sampleEntity));
        when(mesocycleFactory.createForFinish(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(finishedPayload);

        // When
        MesocycleResponse result = mesocycleService.finishMesocycle(1L);

        // Then
        assertEquals(1L, result.id());
        assertNotNull(result.finishedAt());

        verify(mesocycleRepo).findById(any(Long.class));
        verify(mesocycleFactory).createForFinish(any(Mesocycle.class));
        verify(mesocycleRepo).save(any(Mesocycle.class));
        verify(mesocycleMapper).toPayload(any(Mesocycle.class));
    }

    @Test
    void finishMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(any(Long.class))).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> mesocycleService.finishMesocycle(1L));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(any(Long.class));
    }
}
