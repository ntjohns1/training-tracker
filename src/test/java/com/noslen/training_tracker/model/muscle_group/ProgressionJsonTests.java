package com.noslen.training_tracker.model.muscle_group;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ProgressionJsonTests {
    @Autowired
    private JacksonTester<Progression> json;

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{"
                + "\"id\": 6159088,"
                + "\"muscleGroupId\": 1,"
                + "\"mgProgressionType\": \"regular\""
                + "}";

        // Parse the JSON string into a Progression object
        Progression progression = json.parse(jsonContent).getObject();

        // Verify the parsed object has the expected values
        assertThat(progression.getId()).isEqualTo(6159088L);
        assertThat(progression.getMuscleGroupId()).isEqualTo(1L);
        assertThat(progression.getMgProgressionType()).isEqualTo("regular");
    }
}
