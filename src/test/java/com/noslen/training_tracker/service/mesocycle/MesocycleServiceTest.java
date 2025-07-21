package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;
import com.noslen.training_tracker.dto.mesocycle.MesocyclePayload;
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

    @InjectMocks
    private MesocycleServiceImpl mesocycleService;

    private MesocyclePayload samplePayload;
    private Mesocycle sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        MesoNotePayload notePayload = new MesoNotePayload(
                1L, 10L, 20L, now, now, "Test note"
        );

        samplePayload = new MesocyclePayload(
                1L, "test-key", 100L, "Test Mesocycle", 28, "days",
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
                .unit("days")
                .sourceTemplate(MesoTemplate.builder().id(2L).build())
                .sourceMeso(Mesocycle.builder().id(3L).build())
                .microRirs(5L)
                .createdAt(now)
                .updatedAt(now)
                .finishedAt(null)
                .deletedAt(null)
                .weeks(Collections.emptyList())
                .notes(Collections.emptyList())
                .status("active")
                .generatedFrom("template")
                .progressions(Collections.emptyMap())
                .build();
    }

    @Test
    void createMesocycle_Success() {
        // Given
        Mesocycle entityToSave = Mesocycle.builder()
                .mesocycleKey("test-key")
                .name("Test Mesocycle")
                .userId(100L)
                .days(28)
                .unit("days")
                .build();

        when(mesocycleMapper.toEntity(samplePayload)).thenReturn(entityToSave);
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesocyclePayload result = mesocycleService.createMesocycle(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.key(), result.key());
        assertEquals(samplePayload.name(), result.name());

        verify(mesocycleMapper).toEntity(samplePayload);
        verify(mesocycleRepo).save(any(Mesocycle.class));
        verify(mesocycleMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesocycle_Success() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesocyclePayload result = mesocycleService.getMesocycle(1L);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.name(), result.name());

        verify(mesocycleRepo).findById(1L);
        verify(mesocycleMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesocycleService.getMesocycle(1L));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(1L);
        verifyNoInteractions(mesocycleMapper);
    }

    @Test
    void getMesocyclesByUserId_Success() {
        // Given
        List<Mesocycle> entities = Arrays.asList(sampleEntity);

        when(mesocycleRepo.findByUserId(100L)).thenReturn(entities);
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesocyclePayload> result = mesocycleService.getMesocyclesByUserId(100L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findByUserId(100L);
        verify(mesocycleMapper).toPayload(sampleEntity);
    }

    @Test
    void updateMesocycle_Success() {
        // Given
        Mesocycle updatedEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("updated-key")
                .name("Updated Mesocycle")
                .userId(100L)
                .days(28)
                .unit("days")
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

        MesocyclePayload updatedPayload = new MesocyclePayload(
                1L, "updated-key", 100L, "Updated Mesocycle", 28, "days",
                null, null, null, now, now, null, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Collections.emptyList()
        );

        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesocycleMapper.mergeEntity(sampleEntity, samplePayload)).thenReturn(updatedEntity);
        when(mesocycleRepo.save(updatedEntity)).thenReturn(updatedEntity);
        when(mesocycleMapper.toPayload(updatedEntity)).thenReturn(updatedPayload);

        // When
        MesocyclePayload result = mesocycleService.updateMesocycle(1L, samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated Mesocycle", result.name());

        verify(mesocycleRepo).findById(1L);
        verify(mesocycleMapper).mergeEntity(sampleEntity, samplePayload);
        verify(mesocycleRepo).save(updatedEntity);
        verify(mesocycleMapper).toPayload(updatedEntity);
    }

    @Test
    void updateMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesocycleService.updateMesocycle(1L, samplePayload));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(1L);
        verifyNoMoreInteractions(mesocycleMapper, mesocycleRepo);
    }

    @Test
    void deleteMesocycle_Success() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);

        // When
        mesocycleService.deleteMesocycle(1L);

        // Then
        verify(mesocycleRepo).findById(1L);
        verify(mesocycleRepo).save(any(Mesocycle.class));
        
        // Verify that save was called with a mesocycle that has deletedAt set
        verify(mesocycleRepo).save(argThat(mesocycle -> 
            mesocycle.getDeletedAt() != null && mesocycle.getId().equals(1L)
        ));
    }

    @Test
    void deleteMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesocycleService.deleteMesocycle(1L));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(1L);
        verify(mesocycleRepo, never()).save(any(Mesocycle.class));
    }

    @Test
    void getAllMesocycles_Success() {
        // Given
        List<Mesocycle> entities = Arrays.asList(sampleEntity);

        when(mesocycleRepo.findAll()).thenReturn(entities);
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesocyclePayload> result = mesocycleService.getAllMesocycles();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findAll();
        verify(mesocycleMapper).toPayload(sampleEntity);
    }

    @Test
    void getAllMesocycles_EmptyList() {
        // Given
        when(mesocycleRepo.findAll()).thenReturn(Arrays.asList());

        // When
        List<MesocyclePayload> result = mesocycleService.getAllMesocycles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesocycleRepo).findAll();
        verifyNoInteractions(mesocycleMapper);
    }

    @Test
    void getAllActiveMesocycles_Success() {
        // Given
        List<Mesocycle> activeEntities = Arrays.asList(sampleEntity);

        when(mesocycleRepo.findByDeletedAtIsNull()).thenReturn(activeEntities);
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesocyclePayload> result = mesocycleService.getAllActiveMesocycles();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findByDeletedAtIsNull();
        verify(mesocycleMapper).toPayload(sampleEntity);
    }

    @Test
    void getAllActiveMesocycles_EmptyList() {
        // Given
        when(mesocycleRepo.findByDeletedAtIsNull()).thenReturn(Arrays.asList());

        // When
        List<MesocyclePayload> result = mesocycleService.getAllActiveMesocycles();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesocycleRepo).findByDeletedAtIsNull();
        verifyNoInteractions(mesocycleMapper);
    }

    @Test
    void getActiveMesocyclesByUserId_Success() {
        // Given
        List<Mesocycle> activeEntities = Arrays.asList(sampleEntity);

        when(mesocycleRepo.findByUserIdAndDeletedAtIsNull(100L)).thenReturn(activeEntities);
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesocyclePayload> result = mesocycleService.getActiveMesocyclesByUserId(100L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesocycleRepo).findByUserIdAndDeletedAtIsNull(100L);
        verify(mesocycleMapper).toPayload(sampleEntity);
    }

    @Test
    void getActiveMesocyclesByUserId_EmptyList() {
        // Given
        when(mesocycleRepo.findByUserIdAndDeletedAtIsNull(100L)).thenReturn(Arrays.asList());

        // When
        List<MesocyclePayload> result = mesocycleService.getActiveMesocyclesByUserId(100L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesocycleRepo).findByUserIdAndDeletedAtIsNull(100L);
        verifyNoInteractions(mesocycleMapper);
    }

    @Test
    void finishMesocycle_Success() {
        // Given
        Mesocycle finishedEntity = Mesocycle.builder()
                .id(1L)
                .mesocycleKey("test-key")
                .name("Test Mesocycle")
                .userId(100L)
                .finishedAt(now)
                .build();

        MesocyclePayload finishedPayload = new MesocyclePayload(
                1L, "test-key", 100L, "Test Mesocycle", 28, "days",
                null, null, null, now, now, now, null,
                null, null, null, null, null, null, null, null, null, null, null,
                4, Collections.emptyList()
        );

        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(finishedEntity);
        when(mesocycleMapper.toPayload(finishedEntity)).thenReturn(finishedPayload);

        // When
        MesocyclePayload result = mesocycleService.finishMesocycle(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNotNull(result.finishedAt());

        verify(mesocycleRepo).findById(1L);
        verify(mesocycleRepo).save(argThat(mesocycle -> 
            mesocycle.getFinishedAt() != null && mesocycle.getId().equals(1L)
        ));
        verify(mesocycleMapper).toPayload(finishedEntity);
    }

    @Test
    void finishMesocycle_NotFound_ShouldThrowException() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesocycleService.finishMesocycle(1L));
        
        assertEquals("Mesocycle not found with id: 1", exception.getMessage());
        verify(mesocycleRepo).findById(1L);
        verify(mesocycleRepo, never()).save(any(Mesocycle.class));
        verifyNoInteractions(mesocycleMapper);
    }

    @Test
    void createMesocycle_ShouldSetTimestampsCorrectly() {
        // Given
        Mesocycle entityToSave = Mesocycle.builder()
                .mesocycleKey("test-key")
                .name("Test Mesocycle")
                .build();

        when(mesocycleMapper.toEntity(samplePayload)).thenReturn(entityToSave);
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        mesocycleService.createMesocycle(samplePayload);

        // Then
        verify(mesocycleRepo).save(argThat(mesocycle -> 
            mesocycle.getCreatedAt() != null && 
            mesocycle.getUpdatedAt() != null &&
            mesocycle.getDeletedAt() == null &&
            mesocycle.getFinishedAt() == null
        ));
    }

    @Test
    void deleteMesocycle_ShouldPerformSoftDelete() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));

        // When
        mesocycleService.deleteMesocycle(1L);

        // Then
        verify(mesocycleRepo).save(argThat(mesocycle -> 
            mesocycle.getDeletedAt() != null &&
            mesocycle.getUpdatedAt() != null &&
            mesocycle.getId().equals(1L) &&
            mesocycle.getMesocycleKey().equals("test-key") &&
            mesocycle.getName().equals("Test Mesocycle")
        ));
    }

    @Test
    void finishMesocycle_ShouldSetFinishedAtTimestamp() {
        // Given
        when(mesocycleRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesocycleRepo.save(any(Mesocycle.class))).thenReturn(sampleEntity);
        when(mesocycleMapper.toPayload(any(Mesocycle.class))).thenReturn(samplePayload);

        // When
        mesocycleService.finishMesocycle(1L);

        // Then
        verify(mesocycleRepo).save(argThat(mesocycle -> 
            mesocycle.getFinishedAt() != null &&
            mesocycle.getUpdatedAt() != null &&
            mesocycle.getId().equals(1L) &&
            mesocycle.getMesocycleKey().equals("test-key") &&
            mesocycle.getName().equals("Test Mesocycle")
        ));
    }
}
