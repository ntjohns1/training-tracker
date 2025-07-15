package com.noslen.training_tracker.dto.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesoTemplatePayloadJsonTests {
    @Autowired
    private JacksonTester<MesoTemplatePayload> json;

    @Test
    void testSerialize() throws IOException {
        MesoTemplatePayload mesoTemplatePayload = new MesoTemplatePayload(
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
        assertThat(json.write(mesoTemplatePayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/meso_template.json");
        MesoTemplatePayload mesoTemplatePayload = json.readObject(resource.getFile());

        assertThat(mesoTemplatePayload.id()).isEqualTo(51L);
        assertThat(mesoTemplatePayload.key()).isEqualTo("whole-body-split-male-5x");
        assertThat(mesoTemplatePayload.name()).isEqualTo("Whole Body Split");
        assertThat(mesoTemplatePayload.emphasis()).isEqualTo("Whole Body");
        assertThat(mesoTemplatePayload.sex()).isEqualTo("male");
        assertThat(mesoTemplatePayload.userId()).isNull();
        assertThat(mesoTemplatePayload.sourceTemplateId()).isNull();
        assertThat(mesoTemplatePayload.sourceMesoId()).isNull();
        assertThat(mesoTemplatePayload.prevTemplateId()).isNull();
        assertThat(mesoTemplatePayload.createdAt()).isEqualTo(Instant.parse("2023-10-03T23:30:11.734Z"));
        assertThat(mesoTemplatePayload.updatedAt()).isEqualTo(Instant.parse("2023-10-03T23:30:11.734Z"));
        assertThat(mesoTemplatePayload.deletedAt()).isNull();
        assertThat(mesoTemplatePayload.frequency()).isEqualTo(5);
    }
}
