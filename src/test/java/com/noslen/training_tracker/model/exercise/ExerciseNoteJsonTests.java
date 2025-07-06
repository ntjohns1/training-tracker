package com.noslen.training_tracker.model.exercise;

    import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ExerciseNoteJsonTests {
    @Autowired
    private JacksonTester<ExerciseNote> json;

    @Test
    void exerciseNoteSerializationTest() throws IOException {
        ExerciseNote note = ExerciseNote.builder()
                .id(1248357L)
                .exerciseId(191L)
                .userId(1518614L)
                .noteId(1304638L)
                .dayExerciseId(121219737L)
                .createdAt(Instant.parse("2025-02-09T18:07:46.567Z"))
                .updatedAt(Instant.parse("2025-06-29T18:39:37.789Z"))
                .text("Big chest all the way through")
                .build();

        // Assert that the serialized ExerciseNote matches expected JSON structure
        assertThat(json.write(note)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(note)).extractingJsonPathNumberValue("@.id").isEqualTo(1248357);
        assertThat(json.write(note)).hasJsonPathNumberValue("@.exerciseId");
        assertThat(json.write(note)).extractingJsonPathNumberValue("@.exerciseId").isEqualTo(191);
        assertThat(json.write(note)).hasJsonPathNumberValue("@.userId");
        assertThat(json.write(note)).extractingJsonPathNumberValue("@.userId").isEqualTo(1518614);
        assertThat(json.write(note)).hasJsonPathNumberValue("@.noteId");
        assertThat(json.write(note)).extractingJsonPathNumberValue("@.noteId").isEqualTo(1304638);
        assertThat(json.write(note)).hasJsonPathNumberValue("@.dayExerciseId");
        assertThat(json.write(note)).extractingJsonPathNumberValue("@.dayExerciseId").isEqualTo(121219737);
        assertThat(json.write(note)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(note)).hasJsonPathStringValue("@.updatedAt");
        assertThat(json.write(note)).hasJsonPathStringValue("@.text");
    }

    @Test
    void exerciseNoteDeserializationTest() throws IOException {
        String jsonContent = "{"
                + "\"id\": 1248357,"
                + "\"exerciseId\": 191,"
                + "\"userId\": 1518614,"
                + "\"noteId\": 1304638,"
                + "\"dayExerciseId\": 121219737,"
                + "\"createdAt\": \"2025-02-09T18:07:46.567Z\","
                + "\"updatedAt\": \"2025-06-29T18:39:37.789Z\","
                + "\"text\": \"Big chest all the way through\""
                + "}";

        ExerciseNote note = json.parse(jsonContent).getObject();

        assertThat(note.getId()).isEqualTo(1248357L);
        assertThat(note.getExerciseId()).isEqualTo(191L);
        assertThat(note.getUserId()).isEqualTo(1518614L);
        assertThat(note.getNoteId()).isEqualTo(1304638L);
        assertThat(note.getDayExerciseId()).isEqualTo(121219737L);
        assertThat(note.getCreatedAt()).isEqualTo(Instant.parse("2025-02-09T18:07:46.567Z"));
        assertThat(note.getUpdatedAt()).isEqualTo(Instant.parse("2025-06-29T18:39:37.789Z"));
        assertThat(note.getText()).isEqualTo("Big chest all the way through");
    }   

}
