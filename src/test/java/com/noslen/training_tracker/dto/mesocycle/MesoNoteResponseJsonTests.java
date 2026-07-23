package com.noslen.training_tracker.dto.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesoNoteResponseJsonTests {
    @Autowired
    private JacksonTester<MesoNoteResponse> json;

    @Test
    void testSerialize() throws IOException {
        MesoNoteResponse mesoNoteResponse = new MesoNoteResponse(
                13571L,
                790173L,
                1634147L,
                Instant.parse("2025-07-05T19:06:19.128Z"),
                Instant.parse("2025-07-05T19:06:19.128Z"),
                "For P7: \nBack to cable Lateral Raises");

        ClassPathResource resource = new ClassPathResource("example/meso_note.json");
        assertThat(json.write(mesoNoteResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/meso_note.json");
        MesoNoteResponse mesoNoteResponse = json.readObject(resource.getFile());
        assertThat(mesoNoteResponse.id()).isEqualTo(13571L);
        assertThat(mesoNoteResponse.mesoId()).isEqualTo(790173L);
        assertThat(mesoNoteResponse.noteId()).isEqualTo(1634147L);
        assertThat(mesoNoteResponse.createdAt()).isEqualTo(Instant.parse("2025-07-05T19:06:19.128Z"));
        assertThat(mesoNoteResponse.updatedAt()).isEqualTo(Instant.parse("2025-07-05T19:06:19.128Z"));
        assertThat(mesoNoteResponse.text()).isEqualTo("For P7: \nBack to cable Lateral Raises");
    }
}