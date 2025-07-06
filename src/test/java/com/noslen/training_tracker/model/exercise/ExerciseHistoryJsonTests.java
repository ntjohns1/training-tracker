package com.noslen.training_tracker.model.exercise;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@JsonTest
public class ExerciseHistoryJsonTests {

    @Autowired
    private JacksonTester<ExerciseHistory> json;

    @Test
    public void testSerialize() throws IOException {
        assertThat(json.write(exerciseHistory)).isEqualToJson("exercise_history.json");
    }
}
