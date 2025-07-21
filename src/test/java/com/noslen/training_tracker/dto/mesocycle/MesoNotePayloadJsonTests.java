package com.noslen.training_tracker.dto.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesoNotePayloadJsonTests {
    @Autowired
    private JacksonTester<MesoNotePayload> json;

    @Test
    void testSerialize() throws IOException {
        MesoNotePayload mesoNotePayload = new MesoNotePayload(
                13571L,
                790173L,
                1634147L,
                Instant.parse("2025-07-05T19:06:19.128Z"),
                Instant.parse("2025-07-05T19:06:19.128Z"),
                "For P7: \nBack to cable Lateral Raises");

        ClassPathResource resource = new ClassPathResource("example/meso_note.json");
        assertThat(json.write(mesoNotePayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/meso_note.json");
        MesoNotePayload mesoNotePayload = json.readObject(resource.getFile());
        assertThat(mesoNotePayload.id()).isEqualTo(13571L);
        assertThat(mesoNotePayload.mesoId()).isEqualTo(790173L);
        assertThat(mesoNotePayload.noteId()).isEqualTo(1634147L);
        assertThat(mesoNotePayload.createdAt()).isEqualTo(Instant.parse("2025-07-05T19:06:19.128Z"));
        assertThat(mesoNotePayload.updatedAt()).isEqualTo(Instant.parse("2025-07-05T19:06:19.128Z"));
        assertThat(mesoNotePayload.text()).isEqualTo("For P7: \nBack to cable Lateral Raises");
    }
}