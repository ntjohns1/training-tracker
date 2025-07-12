package com.noslen.training_tracker.model.muscle_group;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MuscleGroupJsonTests {
    @Autowired
    private JacksonTester<MuscleGroup> json;

    @Test
    void testSerialize() throws IOException {
        MuscleGroup muscleGroup =  new MuscleGroup(12L, "Quads", Instant.parse("2022-11-21T23:28:14.769Z"), Instant.parse("2022-11-21T23:28:15.342Z"));
        

        ClassPathResource resource = new ClassPathResource("example/muscle_group.json");
        assertThat(json.write(muscleGroup)).isEqualToJson(resource);
    }

    //    @TODO: Refactor using DTOs
//    @Test
//    void testDeserialize() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("example/muscle_group.json");
//        MuscleGroup muscleGroup = json.readObject(resource.getFile());
//
//        assertThat(muscleGroup.getId()).isEqualTo(12L);
//        assertThat(muscleGroup.getName()).isEqualTo("Quads");
//        assertThat(muscleGroup.getCreatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:14.769Z"));
//        assertThat(muscleGroup.getUpdatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:15.342Z"));
//    }
}
