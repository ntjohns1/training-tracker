package com.noslen.training_tracker.dto.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoNoteResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesocycleResponseJsonTests {
    @Autowired
    private JacksonTester<MesocycleResponse> json;

    @Test
    void testSerialize() throws IOException {
        MesoNoteResponse mesoNoteResponse = new MesoNoteResponse(
                13571L,
                790173L,
                1634147L,
                Instant.parse("2025-07-05T19:06:19.128Z"),
                Instant.parse("2025-07-05T19:06:19.128Z"),
                "For P7: \nBack to cable Lateral Raises");

        ArrayList<MesoNoteResponse> notes = new ArrayList<MesoNoteResponse>();
        notes.add(mesoNoteResponse);

        MesocycleResponse mesocycleResponse = new MesocycleResponse(
                790173L,
                "wzzidovd6137",
                1518614L,
                "2025 P6",
                5,
                "lb",
                16909L,
                null,
                32108L,
                Instant.parse("2025-06-12T00:44:33.064Z"),
                Instant.parse("2025-07-05T16:02:18.167Z"),
                null,
                null,
                Instant.parse("2025-06-19T20:16:44.012Z"),
                Instant.parse("2025-06-13T20:19:45.802Z"),
                Instant.parse("2025-06-13T19:16:49.001Z"),
                Instant.parse("2025-06-13T19:16:44.810Z"),
                Instant.parse("2025-07-02T13:49:49.037Z"),
                Instant.parse("2025-07-05T16:00:02.611Z"),
                null,
                Instant.parse("2025-07-05T16:02:18.077Z"),
                Instant.parse("2025-07-05T16:02:18.077Z"),
                null,
                null,
                5,
                notes);

        ClassPathResource resource = new ClassPathResource("example/mesocycle.json");
        assertThat(json.write(mesocycleResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/mesocycle.json");
        MesocycleResponse mesocycleResponse = json.readObject(resource.getFile());

        assertThat(mesocycleResponse.id()).isEqualTo(790173L);
        assertThat(mesocycleResponse.key()).isEqualTo("wzzidovd6137");
        assertThat(mesocycleResponse.userId()).isEqualTo(1518614L);
        assertThat(mesocycleResponse.name()).isEqualTo("2025 P6");
        assertThat(mesocycleResponse.days()).isEqualTo(5);
        assertThat(mesocycleResponse.unit()).isEqualTo("lb");
        assertThat(mesocycleResponse.sourceTemplateId()).isEqualTo(16909L);
        assertThat(mesocycleResponse.sourceMesoId()).isNull();
        assertThat(mesocycleResponse.microRirs()).isEqualTo(32108L);
        assertThat(mesocycleResponse.createdAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(mesocycleResponse.updatedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.167Z"));
        assertThat(mesocycleResponse.finishedAt()).isNull();
        assertThat(mesocycleResponse.deletedAt()).isNull();
        assertThat(mesocycleResponse.firstMicroCompletedAt()).isEqualTo(Instant.parse("2025-06-19T20:16:44.012Z"));
        assertThat(mesocycleResponse.firstWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-06-13T20:19:45.802Z"));
        assertThat(mesocycleResponse.firstExerciseCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:49.001Z"));
        assertThat(mesocycleResponse.firstSetCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.810Z"));
        assertThat(mesocycleResponse.lastMicroFinishedAt()).isEqualTo(Instant.parse("2025-07-02T13:49:49.037Z"));
        assertThat(mesocycleResponse.lastSetCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:00:02.611Z"));
        assertThat(mesocycleResponse.lastSetSkippedAt()).isNull();
        assertThat(mesocycleResponse.lastWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
        assertThat(mesocycleResponse.lastWorkoutFinishedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
        assertThat(mesocycleResponse.lastWorkoutSkippedAt()).isNull();
        assertThat(mesocycleResponse.lastWorkoutPartialedAt()).isNull();
        assertThat(mesocycleResponse.weeks()).isEqualTo(5);
        assertThat(mesocycleResponse.notes()).hasSize(1);
    }
}
