package com.noslen.training_tracker.dto.day;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ExerciseSetPayloadJsonTests {
    @Autowired
    private JacksonTester<ExerciseSetPayload> json;

    @Test
    void testSerialize() throws IOException {
        ExerciseSetPayload exerciseSetPayload = new ExerciseSetPayload(156620837L, 121219664L, 0, "regular", 35f, null,
                null, null, 8, null, null, "lb", Instant.parse("2025-06-12T00:44:33.064Z"),
                Instant.parse("2025-06-13T19:16:44.499Z"), "complete");
        ClassPathResource resource = new ClassPathResource("example/exercise_set.json");
        assertThat(json.write(exerciseSetPayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/exercise_set.json");
        ExerciseSetPayload exerciseSetPayload = json.readObject(resource.getFile());

        assertThat(exerciseSetPayload.id()).isEqualTo(156620837L);
        assertThat(exerciseSetPayload.dayExerciseId()).isEqualTo(121219664L);
        assertThat(exerciseSetPayload.position()).isEqualTo(0);
        assertThat(exerciseSetPayload.setType()).isEqualTo("regular");
        assertThat(exerciseSetPayload.weight()).isEqualTo(35);
        assertThat(exerciseSetPayload.weightTarget()).isNull();
        assertThat(exerciseSetPayload.weightTargetMin()).isNull();
        assertThat(exerciseSetPayload.weightTargetMax()).isNull();
        assertThat(exerciseSetPayload.reps()).isEqualTo(8);
        assertThat(exerciseSetPayload.repsTarget()).isNull();
        assertThat(exerciseSetPayload.bodyweight()).isNull();
        assertThat(exerciseSetPayload.unit()).isEqualTo("lb");
        assertThat(exerciseSetPayload.createdAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(exerciseSetPayload.finishedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.499Z"));
        assertThat(exerciseSetPayload.status()).isEqualTo("complete");
    }
}
