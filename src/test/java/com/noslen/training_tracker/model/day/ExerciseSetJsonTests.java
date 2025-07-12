package com.noslen.training_tracker.model.day;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ExerciseSetJsonTests {

    @Autowired
    private JacksonTester<ExerciseSet> json;
    
    @Test
    public void exerciseSetSerializationTest() throws IOException {
        DayExercise dayExercise = DayExercise.builder()
                .id(121219664L)
                .build();
                
        ExerciseSet exerciseSet = ExerciseSet.builder()
                .id(156620837L)
                .dayExercise(dayExercise)
                .position(0)
                .setType("regular")
                .weight(35.0f)
                .reps(8)
                .unit("lb")
                .createdAt(Instant.parse("2025-06-12T00:44:33.064Z"))
                .finishedAt(Instant.parse("2025-06-21T16:51:05.289Z"))
                .status("complete")
                .build();

        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.id").isEqualTo(156620837);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.dayExerciseId");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.dayExerciseId").isEqualTo(121219664);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.position");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.position").isEqualTo(0);
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.setType");
        assertThat(json.write(exerciseSet)).extractingJsonPathStringValue("@.setType").isEqualTo("regular");
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.weight");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.weight").isEqualTo(35.0);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.reps");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.reps").isEqualTo(8);
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(exerciseSet)).extractingJsonPathStringValue("@.createdAt").isEqualTo("2025-06-12T00:44:33.064Z");
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.finishedAt");
        assertThat(json.write(exerciseSet)).extractingJsonPathStringValue("@.finishedAt").isEqualTo("2025-06-21T16:51:05.289Z");
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.status"); 
        assertThat(json.write(exerciseSet)).extractingJsonPathStringValue("@.status").isEqualTo("complete");
    }

    //    @TODO: Refactor using DTOs
//    @Test
//    public void exerciseSetDeserializationTest() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("example/exercise_set.json");
//        ExerciseSet exerciseSet = json.readObject(resource.getFile());
//
//        assertThat(exerciseSet.getId()).isEqualTo(156620837L);
//        assertThat(exerciseSet.getDayExerciseId()).isEqualTo(121219664L);
//        assertThat(exerciseSet.getPosition()).isEqualTo(0);
//        assertThat(exerciseSet.getSetType()).isEqualTo("regular");
//        assertThat(exerciseSet.getWeight()).isEqualTo(35.0f);
//        assertThat(exerciseSet.getWeightTarget()).isNull();
//        assertThat(exerciseSet.getWeightTargetMin()).isNull();
//        assertThat(exerciseSet.getWeightTargetMax()).isNull();
//        assertThat(exerciseSet.getReps()).isEqualTo(8);
//        assertThat(exerciseSet.getRepsTarget()).isNull();
//        assertThat(exerciseSet.getBodyweight()).isNull();
//        assertThat(exerciseSet.getUnit()).isEqualTo("lb");
//        assertThat(exerciseSet.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
//        assertThat(exerciseSet.getFinishedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.499Z"));
//        assertThat(exerciseSet.getStatus()).isEqualTo("complete");
//
//        // Check JPA relationship
//        assertThat(exerciseSet.getDayExercise()).isNotNull();
//        assertThat(exerciseSet.getDayExercise().getId()).isEqualTo(121219664L);
//    }
}
