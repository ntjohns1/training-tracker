package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.mapper.day.DayNoteMapper;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;

public class DayNoteServiceTests {

    @Mock
    private DayNoteRepo repo;

    @Mock
    private DayNoteMapper mapper;

    @Mock
    private UserContext userContext;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DayNoteServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDayNote() {
        // Arrange
        DayNoteResponse payload = new DayNoteResponse(null, 1L, null, false, Instant.now(), Instant.now(), "Day Note");
        Day day = Day.builder().id(1L).build();
        DayNote savedEntity = new DayNote();
        savedEntity.setId(1L);
        savedEntity.setText("Day Note");
        DayNoteResponse expectedPayload = new DayNoteResponse(1L, 1L, null, false, Instant.now(), Instant.now(), "Day Note");

        when(entityManager.getReference(Day.class, 1L)).thenReturn(day);
        when(repo.save(any(DayNote.class))).thenReturn(savedEntity);
        when(mapper.toPayload(savedEntity)).thenReturn(expectedPayload);

        // Act
        DayNoteResponse result = service.createDayNote(payload);

        // Assert
        assertEquals(expectedPayload, result);
        verify(entityManager, times(1)).getReference(Day.class, 1L);
        verify(repo, times(1)).save(any(DayNote.class));
        verify(mapper, times(1)).toPayload(savedEntity);
    }

    @Test
    void testUpdateDayNote() {
        // Arrange
        Long id = 1L;
        DayNoteResponse payload = new DayNoteResponse(id, 1L, null, false, Instant.now(), Instant.now(), "Updated Day Note");
        
        // Create proper entity relationships for security validation
        com.noslen.training_tracker.model.mesocycle.Mesocycle mesocycle = 
            com.noslen.training_tracker.model.mesocycle.Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        com.noslen.training_tracker.model.day.Day day = 
            com.noslen.training_tracker.model.day.Day.builder()
                .id(5L)
                .mesocycle(mesocycle)
                .build();
        
        DayNote existingEntity = new DayNote();
        existingEntity.setId(id);
        existingEntity.setText("Day Note");
        existingEntity.setDay(day);

        DayNoteResponse expectedPayload = new DayNoteResponse(id, 1L, null, false, Instant.now(), Instant.now(), "Updated Day Note");

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(entityManager.getReference(com.noslen.training_tracker.model.day.Day.class, 1L)).thenReturn(day);
        when(repo.save(existingEntity)).thenReturn(existingEntity);
        when(mapper.toPayload(existingEntity)).thenReturn(expectedPayload);

        // Act
        DayNoteResponse result = service.updateDayNote(id, payload);

        // Assert
        assertEquals(expectedPayload, result);
        assertEquals("Updated Day Note", existingEntity.getText());
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(repo, times(1)).save(existingEntity);
        verify(mapper, times(1)).toPayload(existingEntity);
    }

    @Test
    void testGetDayNote() {
        // Arrange
        Long id = 1L;
        
        // Create proper entity relationships for security validation
        com.noslen.training_tracker.model.mesocycle.Mesocycle mesocycle = 
            com.noslen.training_tracker.model.mesocycle.Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        com.noslen.training_tracker.model.day.Day day = 
            com.noslen.training_tracker.model.day.Day.builder()
                .id(5L)
                .mesocycle(mesocycle)
                .build();
        
        DayNote entity = new DayNote();
        entity.setId(id);
        entity.setText("Day Note");
        entity.setDay(day);
        
        DayNoteResponse expectedPayload = new DayNoteResponse(id, 1L, null, false, Instant.now(), Instant.now(), "Day Note");
        
        when(repo.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayload(entity)).thenReturn(expectedPayload);

        // Act
        DayNoteResponse result = service.getDayNote(id);

        // Assert
        assertEquals(expectedPayload, result);
        verify(repo, times(1)).findById(id);
        verify(userContext, times(1)).validateUserAccess(100L);
        verify(mapper, times(1)).toPayload(entity);
    }

    @Test
    void testGetNotesByDayId() {
        // Arrange
        Long dayId = 1L;
        
        // Create proper entity relationships for security validation
        com.noslen.training_tracker.model.mesocycle.Mesocycle mesocycle = 
            com.noslen.training_tracker.model.mesocycle.Mesocycle.builder()
                .id(10L)
                .userId(100L)
                .build();
                
        com.noslen.training_tracker.model.day.Day day = 
            com.noslen.training_tracker.model.day.Day.builder()
                .id(dayId)
                .mesocycle(mesocycle)
                .build();
        
        List<DayNote> entities = new ArrayList<>();
        DayNote entity1 = new DayNote();
        entity1.setId(1L);
        entity1.setText("Day Note 1");
        entity1.setDay(day);
        entities.add(entity1);
        
        DayNote entity2 = new DayNote();
        entity2.setId(2L);
        entity2.setText("Day Note 2");
        entity2.setDay(day);
        entities.add(entity2);
        
        List<DayNoteResponse> expectedPayloads = new ArrayList<>();
        expectedPayloads.add(new DayNoteResponse(1L, dayId, null, false, Instant.now(), Instant.now(), "Day Note 1"));
        expectedPayloads.add(new DayNoteResponse(2L, dayId, null, false, Instant.now(), Instant.now(), "Day Note 2"));
        
        when(repo.findByDay_Id(dayId)).thenReturn(entities);
        doNothing().when(userContext).validateUserAccess(100L);
        when(mapper.toPayloadList(entities)).thenReturn(expectedPayloads);

        // Act
        List<DayNoteResponse> result = service.getNotesByDayId(dayId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedPayloads, result);
        verify(repo, times(1)).findByDay_Id(dayId);
        verify(userContext, times(1)).validateUserAccess(100L);
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
        DayNoteResponse payload = new DayNoteResponse(id, 1L, null, false, Instant.now(), Instant.now(), "Updated Day Note");
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.updateDayNote(id, payload));
        verify(repo, times(1)).findById(id);
    }
}
