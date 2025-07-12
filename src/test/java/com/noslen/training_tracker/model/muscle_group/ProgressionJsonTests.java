package com.noslen.training_tracker.model.muscle_group;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ProgressionJsonTests {
    @Autowired
    private JacksonTester<Progression> json;

    @Test
    void testSerialize() throws IOException {
        Progression progression = new Progression(6159088L, 1L, "regular");

        ClassPathResource resource = new ClassPathResource("example/progression.json");
        assertThat(json.write(progression)).isEqualToJson(resource);
    }

//    @Test
//    void testDeserialize() throws IOException {
//        ClassPathResource resource = new ClassPathResource("example/progression.json");
//        Progression progression = json.readObject(resource.getFile());
//
//        assertThat(progression.getId()).isEqualTo(6159088L);
//        assertThat(progression.getMuscleGroupId()).isEqualTo(1L);
//        assertThat(progression.getMgProgressionType()).isEqualTo("regular");
//    }
}
