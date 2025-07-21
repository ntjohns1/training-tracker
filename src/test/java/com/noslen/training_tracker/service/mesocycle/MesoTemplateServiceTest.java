package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoTemplatePayload;
import com.noslen.training_tracker.mapper.mesocycle.MesoTemplateMapper;
import com.noslen.training_tracker.model.mesocycle.MesoTemplate;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesoTemplateRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MesoTemplateServiceTest {

    @Mock
    private MesoTemplateRepo mesoTemplateRepo;

    @Mock
    private MesoTemplateMapper mesoTemplateMapper;

    @InjectMocks
    private MesoTemplateServiceImpl mesoTemplateService;

    private MesoTemplatePayload samplePayload;
    private MesoTemplate sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        samplePayload = new MesoTemplatePayload(
                1L, "test-key", "Test Template", "strength", "male", 100L,
                2L, 3L, 4L, now, now, null, 4
        );

        sampleEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("test-key")
                .name("Test Template")
                .emphasis("strength")
                .sex("male")
                .userId(100L)
                .sourceTemplate(MesoTemplate.builder().id(2L).build())
                .sourceMeso(Mesocycle.builder().id(3L).build())
                .prevTemplate(MesoTemplate.builder().id(4L).build())
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .frequency(4)
                .build();
    }

    @Test
    void createMesoTemplate_Success() {
        // Given
        MesoTemplate entityToSave = MesoTemplate.builder()
                .templateKey("test-key")
                .name("Test Template")
                .emphasis("strength")
                .sex("male")
                .userId(100L)
                .frequency(4)
                .build();

        when(mesoTemplateMapper.toEntity(samplePayload)).thenReturn(entityToSave);
        when(mesoTemplateRepo.save(any(MesoTemplate.class))).thenReturn(sampleEntity);
        when(mesoTemplateMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesoTemplatePayload result = mesoTemplateService.createMesoTemplate(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.key(), result.key());
        assertEquals(samplePayload.name(), result.name());

        verify(mesoTemplateMapper).toEntity(samplePayload);
        verify(mesoTemplateRepo).save(any(MesoTemplate.class));
        verify(mesoTemplateMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesoTemplate_Success() {
        // Given
        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoTemplateMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesoTemplatePayload result = mesoTemplateService.getMesoTemplate(1L);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.name(), result.name());

        verify(mesoTemplateRepo).findById(1L);
        verify(mesoTemplateMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesoTemplate_NotFound_ShouldThrowException() {
        // Given
        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesoTemplateService.getMesoTemplate(1L));
        
        assertEquals("MesoTemplate not found with id: 1", exception.getMessage());
        verify(mesoTemplateRepo).findById(1L);
        verifyNoInteractions(mesoTemplateMapper);
    }

    @Test
    void getMesoTemplatesByUserId_Success() {
        // Given
        List<MesoTemplate> entities = Arrays.asList(sampleEntity);

        when(mesoTemplateRepo.findByUserId(100L)).thenReturn(entities);
        when(mesoTemplateMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesoTemplatePayload> result = mesoTemplateService.getMesoTemplatesByUserId(100L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoTemplateRepo).findByUserId(100L);
        verify(mesoTemplateMapper).toPayload(sampleEntity);
    }

    @Test
    void updateMesoTemplate_Success() {
        // Given
        MesoTemplate updatedEntity = MesoTemplate.builder()
                .id(1L)
                .templateKey("updated-key")
                .name("Updated Template")
                .emphasis("strength")
                .sex("male")
                .userId(100L)
                .frequency(4)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

        MesoTemplatePayload updatedPayload = new MesoTemplatePayload(
                1L, "updated-key", "Updated Template", "strength", "male", 100L,
                null, null, null, now, now, null, 4
        );

        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoTemplateMapper.mergeEntity(sampleEntity, samplePayload)).thenReturn(updatedEntity);
        when(mesoTemplateRepo.save(updatedEntity)).thenReturn(updatedEntity);
        when(mesoTemplateMapper.toPayload(updatedEntity)).thenReturn(updatedPayload);

        // When
        MesoTemplatePayload result = mesoTemplateService.updateMesoTemplate(1L, samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated Template", result.name());

        verify(mesoTemplateRepo).findById(1L);
        verify(mesoTemplateMapper).mergeEntity(sampleEntity, samplePayload);
        verify(mesoTemplateRepo).save(updatedEntity);
        verify(mesoTemplateMapper).toPayload(updatedEntity);
    }

    @Test
    void updateMesoTemplate_NotFound_ShouldThrowException() {
        // Given
        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesoTemplateService.updateMesoTemplate(1L, samplePayload));
        
        assertEquals("MesoTemplate not found with id: 1", exception.getMessage());
        verify(mesoTemplateRepo).findById(1L);
        verifyNoMoreInteractions(mesoTemplateMapper, mesoTemplateRepo);
    }

    @Test
    void deleteMesoTemplate_Success() {
        // Given
        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoTemplateRepo.save(any(MesoTemplate.class))).thenReturn(sampleEntity);

        // When
        mesoTemplateService.deleteMesoTemplate(1L);

        // Then
        verify(mesoTemplateRepo).findById(1L);
        verify(mesoTemplateRepo).save(any(MesoTemplate.class));
        
        // Verify that save was called with a template that has deletedAt set
        verify(mesoTemplateRepo).save(argThat(template -> 
            template.getDeletedAt() != null && template.getId().equals(1L)
        ));
    }

    @Test
    void deleteMesoTemplate_NotFound_ShouldThrowException() {
        // Given
        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> mesoTemplateService.deleteMesoTemplate(1L));
        
        assertEquals("MesoTemplate not found with id: 1", exception.getMessage());
        verify(mesoTemplateRepo).findById(1L);
        verify(mesoTemplateRepo, never()).save(any(MesoTemplate.class));
    }

    @Test
    void getAllMesoTemplates_Success() {
        // Given
        List<MesoTemplate> entities = Arrays.asList(sampleEntity);

        when(mesoTemplateRepo.findAll()).thenReturn(entities);
        when(mesoTemplateMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesoTemplatePayload> result = mesoTemplateService.getAllMesoTemplates();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoTemplateRepo).findAll();
        verify(mesoTemplateMapper).toPayload(sampleEntity);
    }

    @Test
    void getAllMesoTemplates_EmptyList() {
        // Given
        when(mesoTemplateRepo.findAll()).thenReturn(Arrays.asList());

        // When
        List<MesoTemplatePayload> result = mesoTemplateService.getAllMesoTemplates();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesoTemplateRepo).findAll();
        verifyNoInteractions(mesoTemplateMapper);
    }

    @Test
    void getAllActiveMesoTemplates_Success() {
        // Given
        List<MesoTemplate> activeEntities = Arrays.asList(sampleEntity);

        when(mesoTemplateRepo.findByDeletedAtIsNull()).thenReturn(activeEntities);
        when(mesoTemplateMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesoTemplatePayload> result = mesoTemplateService.getAllActiveMesoTemplates();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoTemplateRepo).findByDeletedAtIsNull();
        verify(mesoTemplateMapper).toPayload(sampleEntity);
    }

    @Test
    void getAllActiveMesoTemplates_EmptyList() {
        // Given
        when(mesoTemplateRepo.findByDeletedAtIsNull()).thenReturn(Arrays.asList());

        // When
        List<MesoTemplatePayload> result = mesoTemplateService.getAllActiveMesoTemplates();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesoTemplateRepo).findByDeletedAtIsNull();
        verifyNoInteractions(mesoTemplateMapper);
    }

    @Test
    void createMesoTemplate_ShouldSetTimestampsCorrectly() {
        // Given
        MesoTemplate entityToSave = MesoTemplate.builder()
                .templateKey("test-key")
                .name("Test Template")
                .build();

        when(mesoTemplateMapper.toEntity(samplePayload)).thenReturn(entityToSave);
        when(mesoTemplateRepo.save(any(MesoTemplate.class))).thenReturn(sampleEntity);
        when(mesoTemplateMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        mesoTemplateService.createMesoTemplate(samplePayload);

        // Then
        verify(mesoTemplateRepo).save(argThat(template -> 
            template.getCreatedAt() != null && 
            template.getUpdatedAt() != null &&
            template.getDeletedAt() == null
        ));
    }

    @Test
    void deleteMesoTemplate_ShouldPerformSoftDelete() {
        // Given
        when(mesoTemplateRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));

        // When
        mesoTemplateService.deleteMesoTemplate(1L);

        // Then
        verify(mesoTemplateRepo).save(argThat(template -> 
            template.getDeletedAt() != null &&
            template.getUpdatedAt() != null &&
            template.getId().equals(1L) &&
            template.getTemplateKey().equals("test-key") &&
            template.getName().equals("Test Template")
        ));
    }
}
