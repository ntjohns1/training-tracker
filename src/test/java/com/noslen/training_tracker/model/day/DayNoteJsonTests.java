package com.noslen.training_tracker.model.day;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
// 4. TODO
@JsonTest
public class DayNoteJsonTests {

    @Autowired
    private JacksonTester<DayNote> json;

    @Test
    public void dayNoteSerializationTest() throws IOException {
        // Create a DayNote object with all necessary properties
        DayNote dayNote = DayNote.builder()
                .id(57650L)
                .dayId(19749552L)
                .noteId(1634150L)
                .pinned(false)
                .text("Machine Chest Press broken")
                .createdAt(Instant.parse("2025-07-05T19:07:06.751Z"))
                .updatedAt(Instant.parse("2025-07-05T19:07:06.751Z"))
                .build();

        // Assert that the serialized DayNote matches expected JSON structure
        assertThat(json.write(dayNote)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(dayNote)).extractingJsonPathNumberValue("@.id").isEqualTo(57650);
        assertThat(json.write(dayNote)).hasJsonPathNumberValue("@.dayId");
        assertThat(json.write(dayNote)).extractingJsonPathNumberValue("@.dayId").isEqualTo(19749552);
        assertThat(json.write(dayNote)).hasJsonPathNumberValue("@.noteId");
        assertThat(json.write(dayNote)).extractingJsonPathNumberValue("@.noteId").isEqualTo(1634150);
        assertThat(json.write(dayNote)).hasJsonPathBooleanValue("@.pinned");
        assertThat(json.write(dayNote)).extractingJsonPathBooleanValue("@.pinned").isEqualTo(false);
        assertThat(json.write(dayNote)).hasJsonPathStringValue("@.text");
        assertThat(json.write(dayNote)).extractingJsonPathStringValue("@.text").isEqualTo("Machine Chest Press broken");
        assertThat(json.write(dayNote)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(dayNote)).hasJsonPathStringValue("@.updatedAt");
    }

}
