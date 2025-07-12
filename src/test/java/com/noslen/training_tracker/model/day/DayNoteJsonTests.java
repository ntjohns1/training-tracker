package com.noslen.training_tracker.model.day;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DayNoteJsonTests {

    @Autowired
    private JacksonTester<DayNote> json;

    @Test
    public void dayNoteSerializationTest() throws IOException {
        Day day = Day.builder()
                .id(19749552L)
                .build();
        DayNote dayNote = DayNote.builder()
                .id(57650L)
                .day(day)
                .noteId(1634150L)
                .pinned(false)
                .text("Machine Chest Press broken")
                .createdAt(Instant.parse("2025-07-05T19:07:06.751Z"))
                .updatedAt(Instant.parse("2025-07-05T19:07:06.751Z"))
                .build();

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

    //    @TODO: Refactor using DTOs
//    @Test
//    public void dayNoteDeserializationTest() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("example/day_note.json");
//        DayNote dayNote = json.readObject(resource.getFile());
//
//        assertThat(dayNote.getId()).isEqualTo(57650);
//        assertThat(dayNote.getDay().getId()).isEqualTo(19749552);
//        assertThat(dayNote.getNoteId()).isEqualTo(1634150);
//        assertThat(dayNote.getPinned()).isFalse();
//        assertThat(dayNote.getText()).isEqualTo("Machine Chest Press broken");
//        assertThat(dayNote.getCreatedAt()).isEqualTo(Instant.parse("2025-07-05T19:07:06.751Z"));
//        assertThat(dayNote.getUpdatedAt()).isEqualTo(Instant.parse("2025-07-05T19:07:06.751Z"));
//    }
}
