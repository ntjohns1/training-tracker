//package com.noslen.training_tracker.model.mesocycle;
//
//import org.springframework.boot.test.autoconfigure.json.JsonTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.json.JacksonTester;
//import org.junit.jupiter.api.Test;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.util.ArrayList;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@JsonTest
//public class MesocycleJsonTests {
//    @Autowired
//    private JacksonTester<MesocycleData> json;
//
//    @Test
//    void testSerialize() throws IOException {
//
//        MesocycleData mesocycle = new MesocycleData();
//
//            MesoNoteData note = MesoNoteData.builder()
//                    .id(13571L)
//                    .mesocycle(mesocycle)
//                    .noteId(1634147L)
//                    .createdAt(Instant.parse("2025-07-05T19:06:19.128Z"))
//                    .updatedAt(Instant.parse("2025-07-05T19:06:19.128Z"))
//                    .text("For P7: \nBack to cable Lateral Raises")
//                    .build();
//
//            mesocycle.getNotes().add(note);
//
//            // ClassPathResource resource = new ClassPathResource("example/mesocycle.json");
//            // assertThat(json.write(mesocycle)).isEqualToJson(resource);
//
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.id").isEqualTo(790173);
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.key").isEqualTo("wzzidovd6137");
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.userId").isEqualTo(1518614);
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.name").isEqualTo("2025 P6");
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.days").isEqualTo(5);
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.sourceTemplateId").isEqualTo(16909);
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.microRirs").isEqualTo(32108);
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.unit").isEqualTo("lb");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.createdAt").isEqualTo("2025-06-12T00:44:33.064Z");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.updatedAt").isEqualTo("2025-07-05T16:02:18.167Z");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.weeks").isEqualTo(5);
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.days").isEqualTo(5);
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.sourceTemplateId").isEqualTo(16909);
//            assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.microRirs").isEqualTo(32108);
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.unit").isEqualTo("lb");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.createdAt").isEqualTo("2025-06-12T00:44:33.064Z");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.updatedAt").isEqualTo("2025-07-05T16:02:18.167Z");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.weeks").isEqualTo(5);
//            assertThat(json.write(mesocycle)).hasJsonPath("$.finishedAt");
//            assertThat(json.write(mesocycle)).hasJsonPath("$.deletedAt");
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.firstMicroCompletedAt");
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.firstWorkoutCompletedAt");
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.firstExerciseCompletedAt");
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.firstSetCompletedAt");
//            assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.lastMicroFinishedAt");
//            assertThat(json.write(mesocycle)).hasJsonPathStringValue("$.lastSetCompletedAt");
//            assertThat(json.write(mesocycle)).hasJsonPath("$.lastSetSkippedAt");
//            assertThat(json.write(mesocycle)).hasJsonPathStringValue("$.lastWorkoutCompletedAt");
//            assertThat(json.write(mesocycle)).hasJsonPathStringValue("$.lastWorkoutFinishedAt");
//            assertThat(json.write(mesocycle)).hasJsonPath("$.lastWorkoutSkippedAt");
//            assertThat(json.write(mesocycle)).hasJsonPath("$.lastWorkoutPartialedAt");
//            assertThat(json.write(mesocycle)).hasJsonPath("$.weeks");
//            assertThat(json.write(mesocycle)).extractingJsonPathValue("$.weeks").isEqualTo(5);
//            assertThat(json.write(mesocycle)).hasJsonPath("$.notes");
//    }
//
////    @Test
////    void testDeserialize() throws IOException {
////
////        ClassPathResource resource = new ClassPathResource("example/mesocycle.json");
////        MesocycleData mesocycle = json.readObject(resource.getFile());
////
////        assertThat(mesocycle.getId()).isEqualTo(790173L);
////        assertThat(mesocycle.getKey()).isEqualTo("wzzidovd6137");
////        assertThat(mesocycle.getUserId()).isEqualTo(1518614L);
////        assertThat(mesocycle.getName()).isEqualTo("2025 P6");
////        assertThat(mesocycle.getDays()).isEqualTo(5);
////        assertThat(mesocycle.getUnit()).isEqualTo("lb");
////        assertThat(mesocycle.getSourceTemplateId()).isEqualTo(16909L);
////        assertThat(mesocycle.getSourceMesoId()).isNull();
////        assertThat(mesocycle.getMicroRirs()).isEqualTo(32108L);
////        assertThat(mesocycle.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
////        assertThat(mesocycle.getUpdatedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.167Z"));
////        assertThat(mesocycle.getFinishedAt()).isNull();
////        assertThat(mesocycle.getDeletedAt()).isNull();
////        assertThat(mesocycle.getFirstMicroCompletedAt()).isEqualTo(Instant.parse("2025-06-19T20:16:44.012Z"));
////        assertThat(mesocycle.getFirstWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-06-13T20:19:45.802Z"));
////        assertThat(mesocycle.getFirstExerciseCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:49.001Z"));
////        assertThat(mesocycle.getFirstSetCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.810Z"));
////        assertThat(mesocycle.getLastMicroFinishedAt()).isEqualTo(Instant.parse("2025-07-02T13:49:49.037Z"));
////        assertThat(mesocycle.getLastSetCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:00:02.611Z"));
////        assertThat(mesocycle.getLastSetSkippedAt()).isNull();
////        assertThat(mesocycle.getLastWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
////        assertThat(mesocycle.getLastWorkoutFinishedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
////        assertThat(mesocycle.getLastWorkoutSkippedAt()).isNull();
////        assertThat(mesocycle.getLastWorkoutPartialedAt()).isNull();
////        assertThat(mesocycle.getWeeks()).isEqualTo(5);
////        assertThat(mesocycle.getNotes()).isNotEmpty();
////    }
//}
