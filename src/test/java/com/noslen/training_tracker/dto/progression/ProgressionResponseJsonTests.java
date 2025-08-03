package com.noslen.training_tracker.dto.progression;

import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.noslen.training_tracker.enums.MgProgressionType;

@JsonTest
public class ProgressionResponseJsonTests {

    @Autowired
    JacksonTester<ProgressionResponse> json;

    @Test
    void testSerialize() throws Exception {
        ProgressionResponse progressionResponse = new ProgressionResponse(6159088L, 1L, MgProgressionType.REGULAR);
        ClassPathResource resource = new ClassPathResource("example/progression.json");
        assertThat(json.write(progressionResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/progression.json");
        ProgressionResponse progressionResponse = json.readObject(resource.getFile());

        assertThat(progressionResponse.id()).isEqualTo(6159088L);
        assertThat(progressionResponse.muscleGroupId()).isEqualTo(1L);
        assertThat(progressionResponse.mgProgressionType()).isEqualTo(MgProgressionType.REGULAR);
    }

}
