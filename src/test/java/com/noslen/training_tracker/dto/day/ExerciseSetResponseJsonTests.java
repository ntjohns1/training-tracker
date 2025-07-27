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
public class ExerciseSetResponseJsonTests {
    @Autowired
    private JacksonTester<ExerciseSetResponse> json;

    @Test
    void testSerialize() throws IOException {
        ExerciseSetResponse exerciseSetResponse = new ExerciseSetResponse(156620837L, 121219664L, 0, "regular", 35f, null,
                                                                          null, null, 8, null, null, "lb", Instant.parse("2025-06-12T00:44:33.064Z"),
                                                                          Instant.parse("2025-06-13T19:16:44.499Z"), "complete");
        ClassPathResource resource = new ClassPathResource("example/exercise_set.json");
        assertThat(json.write(exerciseSetResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/exercise_set.json");
        ExerciseSetResponse exerciseSetResponse = json.readObject(resource.getFile());

        assertThat(exerciseSetResponse.id()).isEqualTo(156620837L);
        assertThat(exerciseSetResponse.dayExerciseId()).isEqualTo(121219664L);
        assertThat(exerciseSetResponse.position()).isEqualTo(0);
        assertThat(exerciseSetResponse.setType()).isEqualTo("regular");
        assertThat(exerciseSetResponse.weight()).isEqualTo(35);
        assertThat(exerciseSetResponse.weightTarget()).isNull();
        assertThat(exerciseSetResponse.weightTargetMin()).isNull();
        assertThat(exerciseSetResponse.weightTargetMax()).isNull();
        assertThat(exerciseSetResponse.reps()).isEqualTo(8);
        assertThat(exerciseSetResponse.repsTarget()).isNull();
        assertThat(exerciseSetResponse.bodyweight()).isNull();
        assertThat(exerciseSetResponse.unit()).isEqualTo("lb");
        assertThat(exerciseSetResponse.createdAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(exerciseSetResponse.finishedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.499Z"));
        assertThat(exerciseSetResponse.status()).isEqualTo("complete");
    }
}
