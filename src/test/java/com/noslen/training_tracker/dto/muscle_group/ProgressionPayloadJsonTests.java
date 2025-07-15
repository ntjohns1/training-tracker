package com.noslen.training_tracker.dto.muscle_group;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.noslen.training_tracker.util.MgProgressionType;

@JsonTest
public class ProgressionPayloadJsonTests {

    @Autowired
    JacksonTester<ProgressionPayload> json;

    @Test
    void testSerialize() throws Exception {
        ProgressionPayload progressionPayload = new ProgressionPayload(6159088L, 1L, MgProgressionType.regular);
        ClassPathResource resource = new ClassPathResource("example/progression.json");
        assertThat(json.write(progressionPayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/progression.json");
        ProgressionPayload progressionPayload = json.readObject(resource.getFile());

        assertThat(progressionPayload.id()).isEqualTo(6159088L);
        assertThat(progressionPayload.muscleGroupId()).isEqualTo(1L);
        assertThat(progressionPayload.mgProgressionType()).isEqualTo(MgProgressionType.regular);
    }

}
