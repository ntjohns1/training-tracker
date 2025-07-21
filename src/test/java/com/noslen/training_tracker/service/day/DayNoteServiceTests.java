package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.mapper.day.DayNoteMapper;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;

public class DayNoteServiceTests {

    @Mock
    private DayNoteRepo repo;
    
    @Mock
    private DayNoteMapper mapper;

    @InjectMocks
    private DayNoteServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDayNote() {
        // Arrange
        DayNotePayload payload = new DayNotePayload(null, 1L, null, false, Instant.now(), Instant.now(), "Day Note");
        DayNote entity = DayNote.builder().text("Day Note").build();
        DayNote savedEntity = DayNote.builder().id(1L).text("Day Note").build();
        DayNotePayload expectedPayload = new DayNotePayload(1L, 1L, null, false, Instant.now(), Instant.now(), "Day Note");
        
        when(mapper.toEntity(payload)).thenReturn(entity);
        when(repo.save(any(DayNote.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayNotePayload result = service.createDayNote(payload);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(mapper, times(1)).toEntity(payload);
        verify(repo, times(1)).save(any(DayNote.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void testUpdateDayNote() {
        // Arrange
        Long id = 1L;
        DayNotePayload payload = new DayNotePayload(id, 1L, null, false, Instant.now(), Instant.now(), "Updated Day Note");
        DayNote existingEntity = DayNote.builder().id(id).text("Existing Day Note").build();
        DayNote savedEntity = DayNote.builder().id(id).text("Updated Day Note").build();
        DayNotePayload expectedPayload = new DayNotePayload(id, 1L, null, false, Instant.now(), Instant.now(), "Updated Day Note");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repo.save(existingEntity)).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayNotePayload result = service.updateDayNote(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).updateEntity(existingEntity, payload);
        verify(repo, times(1)).save(existingEntity);
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void testGetDayNote() {
        // Arrange
        Long id = 1L;
        DayNote entity = DayNote.builder().id(id).text("Day Note").build();
        DayNotePayload expectedPayload = new DayNotePayload(id, 1L, null, false, Instant.now(), Instant.now(), "Day Note");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayNotePayload result = service.getDayNote(id);
        
        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(mapper, times(1)).toPayload(entity);
    }

    @Test
    void testGetNotesByDayId() {
        // Arrange
        Long dayId = 1L;
        List<DayNote> entities = new ArrayList<>();
        entities.add(DayNote.builder().id(1L).text("Day Note 1").build());
        entities.add(DayNote.builder().id(2L).text("Day Note 2").build());
        
        List<DayNotePayload> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayNotePayload(1L, dayId, null, false, Instant.now(), Instant.now(), "Day Note 1"));
        expectedPayloads.add(new DayNotePayload(2L, dayId, null, false, Instant.now(), Instant.now(), "Day Note 2"));
        
        when(repo.findByDay_Id(dayId)).thenReturn(entities);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayNotePayload> result = service.getNotesByDayId(dayId);
        
        // Assert
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDay_Id(dayId);
        verify(mapper, times(1)).toPayloadList(entities);
    }
    
    @Test
    void testGetDayNoteNotFound() {
        // Arrange
        Long id = 1L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.getDayNote(id));
        verify(repo, times(1)).findById(id);
    }
    
    @Test
    void testUpdateDayNoteNotFound() {
        // Arrange
        Long id = 1L;
        DayNotePayload payload = new DayNotePayload(id, 1L, null, false, Instant.now(), Instant.now(), "Updated Day Note");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDayNote(id, payload));
        verify(repo, times(1)).findById(id);
    }
}
