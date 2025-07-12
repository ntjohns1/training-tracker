package com.noslen.training_tracker.model.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;

@JsonTest
public class MesoNoteJsonTests {

    @Autowired
    private JacksonTester<MesoNote> json;

    @Test
    void testSerialize() throws IOException {

        MesoNote mesoNote = MesoNote.builder()
                .id(13571L)
                .mesoId(790173L)
                .noteId(1634147L)
                .createdAt(Instant.parse("2025-07-05T19:06:19.128Z"))
                .updatedAt(Instant.parse("2025-07-05T19:06:19.128Z"))
                .text("For P7: \nBack to cable Lateral Raises")
                .build();

        assertThat(json.write(mesoNote)).hasJsonPathNumberValue("$.id");
        assertThat(json.write(mesoNote)).extractingJsonPathNumberValue("$.id").isEqualTo(13571);
        assertThat(json.write(mesoNote)).hasJsonPathNumberValue("$.mesoId");
        assertThat(json.write(mesoNote)).extractingJsonPathNumberValue("$.mesoId").isEqualTo(790173);
        assertThat(json.write(mesoNote)).hasJsonPathNumberValue("$.noteId");
        assertThat(json.write(mesoNote)).extractingJsonPathNumberValue("$.noteId").isEqualTo(1634147);
        assertThat(json.write(mesoNote)).hasJsonPathValue("$.createdAt");
        assertThat(json.write(mesoNote)).extractingJsonPathValue("$.createdAt").isEqualTo("2025-07-05T19:06:19.128Z");
        assertThat(json.write(mesoNote)).hasJsonPathValue("$.updatedAt");
        assertThat(json.write(mesoNote)).extractingJsonPathValue("$.updatedAt").isEqualTo("2025-07-05T19:06:19.128Z");
        assertThat(json.write(mesoNote)).hasJsonPathStringValue("$.text");
        assertThat(json.write(mesoNote)).extractingJsonPathStringValue("$.text").isEqualTo("For P7: \nBack to cable Lateral Raises");
    }

    //    @TODO: Refactor using DTOs
//    @Test
//    void testDeserialize() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("example/meso_note.json");
//        MesoNote mesoNote = json.readObject(resource.getFile());
//
//        assertThat(mesoNote.getId()).isEqualTo(13571L);
//        assertThat(mesoNote.getMesoId()).isEqualTo(790173L);
//        assertThat(mesoNote.getNoteId()).isEqualTo(1634147L);
//        assertThat(mesoNote.getCreatedAt()).isEqualTo(Instant.parse("2025-07-05T19:06:19.128Z"));
//        assertThat(mesoNote.getUpdatedAt()).isEqualTo(Instant.parse("2025-07-05T19:06:19.128Z"));
//        assertThat(mesoNote.getText()).isEqualTo("For P7: \nBack to cable Lateral Raises");
//    }
}
