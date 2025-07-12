package com.noslen.training_tracker.model.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CurrentMesoJsonTests {
    @Autowired
    private JacksonTester<CurrentMeso> json;

    @Test
    void testSerialize() throws IOException {
        MesoTemplate template = MesoTemplate.builder().id(16909L).build();
        CurrentMeso currentMeso = CurrentMeso.builder()
                .id(790173L)
                .key("wzzidovd6137")
                .userId(1518614L)
                .name("2025 P6")
                .days(5)
                .unit("lb")
                .sourceTemplate(template)
                .microRirs(32108L)
                .createdAt(Instant.parse("2025-06-12T00:44:33.064Z"))
                .updatedAt(Instant.parse("2025-07-05T16:02:18.167Z"))
                .firstMicroCompletedAt(Instant.parse("2025-06-19T20:16:44.012Z"))
                .firstWorkoutCompletedAt(Instant.parse("2025-06-13T20:19:45.802Z"))
                .firstExerciseCompletedAt(Instant.parse("2025-06-13T19:16:49.001Z"))
                .firstSetCompletedAt(Instant.parse("2025-06-13T19:16:44.810Z"))
                .lastMicroFinishedAt(Instant.parse("2025-07-02T13:49:49.037Z"))
                .lastSetCompletedAt(Instant.parse("2025-07-05T16:00:02.611Z"))
                .lastWorkoutCompletedAt(Instant.parse("2025-07-05T16:02:18.077Z"))
                .lastWorkoutFinishedAt(Instant.parse("2025-07-05T16:02:18.077Z"))
                .weeks(new ArrayList<>())
                .build();

        assertThat(json.write(currentMeso)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(currentMeso)).extractingJsonPathNumberValue("@.id").isEqualTo(790173);
        assertThat(json.write(currentMeso)).hasJsonPathStringValue("@.key");
        assertThat(json.write(currentMeso)).extractingJsonPathStringValue("@.key").isEqualTo("wzzidovd6137");
        assertThat(json.write(currentMeso)).hasJsonPathNumberValue("@.userId");
        assertThat(json.write(currentMeso)).extractingJsonPathNumberValue("@.userId").isEqualTo(1518614);
        assertThat(json.write(currentMeso)).hasJsonPathStringValue("@.name");
        assertThat(json.write(currentMeso)).extractingJsonPathStringValue("@.name").isEqualTo("2025 P6");
        assertThat(json.write(currentMeso)).hasJsonPathNumberValue("@.days");
        assertThat(json.write(currentMeso)).extractingJsonPathNumberValue("@.days").isEqualTo(5);
        assertThat(json.write(currentMeso)).hasJsonPathStringValue("@.unit");
        assertThat(json.write(currentMeso)).extractingJsonPathStringValue("@.unit").isEqualTo("lb");
        assertThat(json.write(currentMeso)).hasJsonPathNumberValue("@.sourceTemplateId");
        assertThat(json.write(currentMeso)).extractingJsonPathNumberValue("@.sourceTemplateId").isEqualTo(16909);
        assertThat(json.write(currentMeso)).hasJsonPathNumberValue("@.microRirs");
        assertThat(json.write(currentMeso)).extractingJsonPathNumberValue("@.microRirs").isEqualTo(32108);
        assertThat(json.write(currentMeso)).hasJsonPathValue("@.createdAt");
        assertThat(json.write(currentMeso)).hasJsonPathValue("@.updatedAt");
        assertThat(json.write(currentMeso)).hasJsonPathArrayValue("weeks");
    }
    //    @TODO: Refactor using DTOs
//    @Test
//    void testDeserialize() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("example/current_meso.json");
//        CurrentMeso currentMeso = json.readObject(resource.getFile());
//
//        assertThat(currentMeso.getId()).isEqualTo(790173);
//        assertThat(currentMeso.getKey()).isEqualTo("wzzidovd6137");
//        assertThat(currentMeso.getUserId()).isEqualTo(1518614);
//        assertThat(currentMeso.getName()).isEqualTo("2025 P6");
//        assertThat(currentMeso.getDays()).isEqualTo(5);
//        assertThat(currentMeso.getUnit()).isEqualTo("lb");
//        assertThat(currentMeso.getSourceTemplateId()).isEqualTo(16909L);
//        assertThat(currentMeso.getSourceMesoId()).isNull();
//        assertThat(currentMeso.getMicroRirs()).isEqualTo(32108L);
//        assertThat(currentMeso.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
//        assertThat(currentMeso.getUpdatedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.167Z"));
//        assertThat(currentMeso.getFinishedAt()).isNull();
//        assertThat(currentMeso.getDeletedAt()).isNull();
//        assertThat(currentMeso.getFirstMicroCompletedAt()).isEqualTo(Instant.parse("2025-06-19T20:16:44.012Z"));
//        assertThat(currentMeso.getFirstWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-06-13T20:19:45.802Z"));
//        assertThat(currentMeso.getFirstExerciseCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:49.001Z"));
//        assertThat(currentMeso.getFirstSetCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.810Z"));
//        assertThat(currentMeso.getLastMicroFinishedAt()).isEqualTo(Instant.parse("2025-07-02T13:49:49.037Z"));
//        assertThat(currentMeso.getLastSetCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:00:02.611Z"));
//        assertThat(currentMeso.getLastSetSkippedAt()).isNull();
//        assertThat(currentMeso.getLastWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
//        assertThat(currentMeso.getLastWorkoutFinishedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
//        assertThat(currentMeso.getLastWorkoutSkippedAt()).isNull();
//        assertThat(currentMeso.getLastWorkoutPartialedAt()).isNull();
//        assertThat(currentMeso.getWeeks()).isNotEmpty();
//    }
}
