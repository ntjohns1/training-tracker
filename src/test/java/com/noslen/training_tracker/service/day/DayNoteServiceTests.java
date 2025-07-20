package com.noslen.training_tracker.service.day;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.noslen.training_tracker.model.day.DayNote;
import com.noslen.training_tracker.repository.day.DayNoteRepo;

public class DayNoteServiceTests {

    @Mock
    private DayNoteRepo repo;

    @InjectMocks
    private DayNoteServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDayNote() {
        DayNote dayNote = DayNote.builder().text("Day Note").build();
        when(repo.save(dayNote)).thenReturn(dayNote);

        DayNote result = service.createDayNote(dayNote);
        assertEquals(dayNote, result);
        verify(repo, times(1)).save(dayNote);
    }

    @Test
    void testUpdateDayNote() {
        // Arrange
        Long id = 1L;
        DayNote existingDayNote = DayNote.builder().text("Existing Day Note").build();
        DayNote newDayNote = DayNote.builder().text("Updated Day Note").build();

        // Mock the findById to return the existing day note
        when(repo.findById(id)).thenReturn(Optional.of(existingDayNote));
        when(repo.save(existingDayNote)).thenReturn(existingDayNote);

        // Act
        DayNote result = service.updateDayNote(id, newDayNote);

        // Assert
        assertEquals(existingDayNote, result);
        assertEquals("Updated Day Note", existingDayNote.getText());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(existingDayNote);
    }

    @Test
    void testGetDayNote() {
        Long id = 1L;
        DayNote dayNote = DayNote.builder().text("Day Note").build();
        when(repo.findById(id)).thenReturn(Optional.of(dayNote));

        DayNote result = service.getDayNote(id);
        assertEquals(dayNote, result);
        verify(repo, times(1)).findById(id);
    }

    @Test
    void testGetNotesByDayId() {
        Long dayId = 1L;
        List<DayNote> dayNotes = List.of(DayNote.builder().text("Day Note").build());
        when(repo.findByDayId(dayId)).thenReturn(dayNotes);

        List<DayNote> result = service.getNotesByDayId(dayId);
        assertEquals(dayNotes, result);
        verify(repo, times(1)).findByDayId(dayId);
    }   


}
