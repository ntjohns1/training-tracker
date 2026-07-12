package com.noslen.training_tracker.dto.day;

import com.noslen.training_tracker.dto.day.response.DayNoteResponse;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DayNoteResponseJsonTests {
    @Autowired
    private JacksonTester<DayNoteResponse> json;

    @Test
    void testSerialize() throws IOException {
        DayNoteResponse dayNoteResponse = new DayNoteResponse(
            57650L,
            19749552L,
            1634150L,
            false,
            Instant.parse("2025-07-05T19:07:06.751Z"),
            Instant.parse("2025-07-05T19:07:06.751Z"),
            "Machine Chest Press broken");
        ClassPathResource resource = new ClassPathResource("example/day_note.json");
        assertThat(json.write(dayNoteResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/day_note.json");
        DayNoteResponse dayNoteResponse = json.read(resource).getObject();
        assertThat(dayNoteResponse).isNotNull();
    }
}
