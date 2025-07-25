package com.noslen.training_tracker.service.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.MesoNotePayload;
import com.noslen.training_tracker.mapper.mesocycle.MesoNoteMapper;
import com.noslen.training_tracker.model.mesocycle.MesoNote;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.repository.mesocycle.MesoNoteRepo;
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
    private MesoNoteRepo mesoNoteRepo;

    @Mock
    private MesoNoteMapper mesoNoteMapper;

    @InjectMocks
    private MesoNoteServiceImpl mesoNoteService;

    private MesoNotePayload samplePayload;
    private MesoNote sampleEntity;
    private Instant now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = Instant.now();

        samplePayload = new MesoNotePayload(
                1L, 10L, 20L, now, now, "Test meso note");

        sampleEntity = MesoNote.builder()
                .id(1L)
                .mesocycle(Mesocycle.builder().id(10L).build())
                .noteId(20L)
                .text("Test meso note")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void createMesoNote_Success() {
        // Given
        MesoNote entityToSave = MesoNote.builder()
                .mesocycle(Mesocycle.builder().id(10L).build())
                .noteId(20L)
                .text("Test meso note")
                .build();

        when(mesoNoteMapper.toEntity(samplePayload)).thenReturn(entityToSave);
        when(mesoNoteRepo.save(any(MesoNote.class))).thenReturn(sampleEntity);
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesoNotePayload result = mesoNoteService.createMesoNote(samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.mesoId(), result.mesoId());
        assertEquals(samplePayload.text(), result.text());

        verify(mesoNoteMapper).toEntity(samplePayload);
        verify(mesoNoteRepo).save(any(MesoNote.class));
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void getMesoNote_Success() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        MesoNotePayload result = mesoNoteService.getMesoNote(1L);

        // Then
        assertNotNull(result);
        assertEquals(samplePayload.id(), result.id());
        assertEquals(samplePayload.text(), result.text());

        verify(mesoNoteRepo).findById(1L);
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

        // When
        List<MesoNotePayload> result = mesoNoteService.getMesoNotesByMesoId(10L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoNoteRepo).findByMesocycle_Id(10L);
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void updateMesoNote_Success() {
        // Given
        MesoNote updatedEntity = MesoNote.builder()
                .id(1L)
                .mesocycle(Mesocycle.builder().id(10L).build())
                .noteId(20L)
                .text("Updated text")
                .createdAt(now)
                .updatedAt(now)
                .build();

        MesoNotePayload updatedPayload = new MesoNotePayload(
                1L, 10L, 20L, now, now, "Updated text");

        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(mesoNoteMapper.mergeEntity(sampleEntity, samplePayload)).thenReturn(updatedEntity);
        when(mesoNoteRepo.save(updatedEntity)).thenReturn(updatedEntity);
        when(mesoNoteMapper.toPayload(updatedEntity)).thenReturn(updatedPayload);

        // When
        MesoNotePayload result = mesoNoteService.updateMesoNote(1L, samplePayload);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Updated text", result.text());

        verify(mesoNoteRepo).findById(1L);
        verify(mesoNoteMapper).mergeEntity(sampleEntity, samplePayload);
        verify(mesoNoteRepo).save(updatedEntity);
        verify(mesoNoteMapper).toPayload(updatedEntity);
    }

    @Test
    void updateMesoNote_NotFound_ShouldThrowException() {
        // Given
        when(mesoNoteRepo.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mesoNoteService.updateMesoNote(1L, samplePayload));

        assertEquals("MesoNote not found with id: 1", exception.getMessage());
        verify(mesoNoteRepo).findById(1L);
        verifyNoMoreInteractions(mesoNoteMapper, mesoNoteRepo);
    }

    @Test
    void deleteMesoNote_Success() {
        // Given
        when(mesoNoteRepo.existsById(1L)).thenReturn(true);

        // When
        mesoNoteService.deleteMesoNote(1L);

        // Then
        verify(mesoNoteRepo).existsById(1L);
        verify(mesoNoteRepo).deleteById(1L);
    }

    @Test
    void deleteMesoNote_NotFound_ShouldThrowException() {
        // Given
        when(mesoNoteRepo.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mesoNoteService.deleteMesoNote(1L));

        assertEquals("MesoNote not found with id: 1", exception.getMessage());
        verify(mesoNoteRepo).existsById(1L);
        verify(mesoNoteRepo, never()).deleteById(anyLong());
    }

    @Test
    void getAllMesoNotes_Success() {
        // Given
        List<MesoNote> entities = Arrays.asList(sampleEntity);

        when(mesoNoteRepo.findAll()).thenReturn(entities);
        when(mesoNoteMapper.toPayload(sampleEntity)).thenReturn(samplePayload);

        // When
        List<MesoNotePayload> result = mesoNoteService.getAllMesoNotes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(samplePayload.id(), result.get(0).id());

        verify(mesoNoteRepo).findAll();
        verify(mesoNoteMapper).toPayload(sampleEntity);
    }

    @Test
    void getAllMesoNotes_EmptyList() {
        // Given
        when(mesoNoteRepo.findAll()).thenReturn(Arrays.asList());

        // When
        List<MesoNotePayload> result = mesoNoteService.getAllMesoNotes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mesoNoteRepo).findAll();
        verifyNoInteractions(mesoNoteMapper);
    }
}
