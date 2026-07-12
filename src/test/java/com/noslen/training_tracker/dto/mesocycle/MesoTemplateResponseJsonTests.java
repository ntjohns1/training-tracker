package com.noslen.training_tracker.dto.mesocycle;

import com.noslen.training_tracker.dto.mesocycle.response.MesoTemplateResponse;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesoTemplateResponseJsonTests {
    @Autowired
    private JacksonTester<MesoTemplateResponse> json;

    @Test
    void testSerialize() throws IOException {
        MesoTemplateResponse mesoTemplateResponse = new MesoTemplateResponse(
                51L,
                "whole-body-split-male-5x",
                "Whole Body Split",
                "Whole Body",
                "male",
                null,
                null,
                null,
                null,
                Instant.parse("2023-10-03T23:30:11.734Z"),
                Instant.parse("2023-10-03T23:30:11.734Z"),
                null,
                5);

        ClassPathResource resource = new ClassPathResource("example/meso_template.json");
        assertThat(json.write(mesoTemplateResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/meso_template.json");
        MesoTemplateResponse mesoTemplateResponse = json.readObject(resource.getFile());

        assertThat(mesoTemplateResponse.id()).isEqualTo(51L);
        assertThat(mesoTemplateResponse.key()).isEqualTo("whole-body-split-male-5x");
        assertThat(mesoTemplateResponse.name()).isEqualTo("Whole Body Split");
        assertThat(mesoTemplateResponse.emphasis()).isEqualTo("Whole Body");
        assertThat(mesoTemplateResponse.sex()).isEqualTo("male");
        assertThat(mesoTemplateResponse.userId()).isNull();
        assertThat(mesoTemplateResponse.sourceTemplateId()).isNull();
        assertThat(mesoTemplateResponse.sourceMesoId()).isNull();
        assertThat(mesoTemplateResponse.prevTemplateId()).isNull();
        assertThat(mesoTemplateResponse.createdAt()).isEqualTo(Instant.parse("2023-10-03T23:30:11.734Z"));
        assertThat(mesoTemplateResponse.updatedAt()).isEqualTo(Instant.parse("2023-10-03T23:30:11.734Z"));
        assertThat(mesoTemplateResponse.deletedAt()).isNull();
        assertThat(mesoTemplateResponse.frequency()).isEqualTo(5);
    }
}
