package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.request.CreateMesoNoteRequest;
import com.noslen.training_tracker.dto.mesocycle.request.UpdateMesoNoteRequest;
import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.mapper.mesocycle.MesoNoteMapper;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesoNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class MesoNoteServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private MesoNoteRepo mesoNoteRepo;

    @Mock
    private MesoNoteMapper mesoNoteMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MesoNoteServiceImpl mesoNoteService;

    private MesoNoteResponse samplePayload;
    private MesoNote sampleEntity;
    private Mesocycle sampleMesocycle;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        // Create sample mesocycle with userId for security validation
        sampleMesocycle = Mesocycle.builder()
                .id(10L)
                .userId(100L) // Add userId for security validation
                .build();

        samplePayload = new MesoNoteResponse(
                1L, 10L, 20L, now, now, "Test meso note");

        sampleEntity = new MesoNote();
        sampleEntity.setId(1L);
        sampleEntity.setMesocycle(sampleMesocycle);
        sampleEntity.setNoteId(20L);
        sampleEntity.setText("Test meso note");
        sampleEntity.setCreatedAt(now);
        sampleEntity.setUpdatedAt(now);
    }

    @Test
    void createMesoNote_Success() {
        // Given
        CreateMesoNoteRequest request = new CreateMesoNoteRequest(10L, 20L, "Test meso note");

        when(entityManager.getReference(Mesocycle.class, 10L)).thenReturn(sampleMesocycle);
        when(mesoNoteRepo.save(any(MesoNote.class))).thenReturn(sampleEntity);
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesoNoteResponse result = mesoNoteService.createMesoNote(request);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.mesoId(), result.mesoId());
        assertEquals(samplePayload.text(), result.text());

        verify(entityManager).getReference(Mesocycle.class, 10L);
        verify(mesoNoteRepo).save(any(MesoNote.class));
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesoNote_Success() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);
        doNothing().when(userContext).validateUserAccess(100L);

        // When
        MesoNoteResponse result = mesoNoteService.getMesoNote(1L);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.text(), result.text());

        verify(mesoNoteRepo).findById(1L);
        verify(userContext).validateUserAccess(100L);
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesoNote_NotFound_ShouldThrowException() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mesoNoteService.getMesoNote(1L));

        assertEquals("MesoNote not found with id: 1", exception.getMessage());
        verify(mesoNoteRepo).findById(1L);
        verifyNoInteractions(mesoNoteMapper);
    }

    @Test
    void getMesoNotesByMesoId_Success() {
        // Given
        List<MesoNote> entities = Arrays.asList(sampleEntity);

        when(mesoNoteRepo.findByMesocycle_Id(10L)).thenReturn(entities);
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);
        doNothing().when(userContext).validateUserAccess(100L);

        // When
        List<MesoNoteResponse> result = mesoNoteService.getMesoNotesByMesoId(10L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoNoteRepo).findByMesocycle_Id(10L);
        verify(userContext).validateUserAccess(100L);
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void updateMesoNote_Success() {
        // Given
        MesoNote updatedEntity = new MesoNote();
        updatedEntity.setId(1L);
        updatedEntity.setMesocycle(sampleMesocycle); // Use sampleMesocycle with userId
        updatedEntity.setNoteId(20L);
        updatedEntity.setText("Updated text");
        updatedEntity.setCreatedAt(now);
        updatedEntity.setUpdatedAt(now);

        UpdateMesoNoteRequest request = new UpdateMesoNoteRequest(1L, "Updated text");
        MesoNoteResponse updatedPayload = new MesoNoteResponse(
                1L, 10L, 20L, now, now, "Updated text");

        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoNoteRepo.save(sampleEntity)).thenReturn(sampleEntity);
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(updatedPayload);
        doNothing().when(userContext).validateUserAccess(100L);

        // When
        MesoNoteResponse result = mesoNoteService.updateMesoNote(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated text", result.text());
        // Text applied directly to the managed entity
        assertEquals("Updated text", sampleEntity.getText());

        verify(mesoNoteRepo).findById(1L);
        verify(userContext).validateUserAccess(100L);
        verify(mesoNoteRepo).save(sampleEntity);
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void updateMesoNote_NotFound_ShouldThrowException() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mesoNoteService.updateMesoNote(1L, new UpdateMesoNoteRequest(1L, "x")));

        assertEquals("MesoNote not found with id: 1", exception.getMessage());
        verify(mesoNoteRepo).findById(1L);
        verifyNoMoreInteractions(mesoNoteMapper, mesoNoteRepo);
    }

    @Test
    void deleteMesoNote_Success() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        doNothing().when(userContext).validateUserAccess(100L);

        // When
        mesoNoteService.deleteMesoNote(1L);

        // Then
        verify(mesoNoteRepo).findById(1L);
        verify(userContext).validateUserAccess(100L);
        verify(mesoNoteRepo).deleteById(1L);
    }

    @Test
    void deleteMesoNote_NotFound_ShouldThrowException() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mesoNoteService.deleteMesoNote(1L));

        assertEquals("MesoNote not found with id: 1", exception.getMessage());
        verify(mesoNoteRepo).findById(1L);
        verify(mesoNoteRepo, never()).deleteById(anyLong());
        verifyNoInteractions(userContext);
    }

    @Test
    void getAllMesoNotes_Success() {
        // Given
        List<MesoNote> entities = Arrays.asList(sampleEntity);

        when(mesoNoteRepo.findAll()).thenReturn(entities);
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);
        doNothing().when(userContext).validateUserAccess(100L);

        // When
        List<MesoNoteResponse> result = mesoNoteService.getAllMesoNotes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoNoteRepo).findAll();
        verify(userContext).validateUserAccess(100L);
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void getAllMesoNotes_EmptyList() {
        // Given
        when(mesoNoteRepo.findAll()).thenReturn(Arrays.asList());

        // When
        List<MesoNoteResponse> result = mesoNoteService.getAllMesoNotes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesoNoteRepo).findAll();
        verifyNoInteractions(mesoNoteMapper);
    }
}
