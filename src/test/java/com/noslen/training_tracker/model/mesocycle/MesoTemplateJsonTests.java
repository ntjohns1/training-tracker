package com.noslen.training_tracker.model.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesoTemplateJsonTests {
    @Autowired
    private JacksonTester<MesoTemplate> json;

    @Test
    void testSerialize() throws IOException {
        // Create a MesoTemplateData instance with minimal fields for serialization test
        MesoTemplate mesoTemplate = MesoTemplate.builder()
                .id(51L)
                .key("whole-body-split-male-5x")
                .name("Whole Body Split")
                .emphasis("Whole Body")
                .sex("male")
                .createdAt(Instant.parse("2023-10-03T23:30:11.734Z"))
                .updatedAt(Instant.parse("2023-10-03T23:30:11.734Z"))
                .frequency(5)
                .build();

        assertThat(json.write(mesoTemplate)).extractingJsonPathStringValue("$.key")
                .isEqualTo("whole-body-split-male-5x");
        assertThat(json.write(mesoTemplate)).extractingJsonPathStringValue("$.name")
                .isEqualTo("Whole Body Split");
        assertThat(json.write(mesoTemplate)).extractingJsonPathStringValue("$.emphasis")
                .isEqualTo("Whole Body");
    }

    //    @TODO: Refactor using DTOs
//    @Test
//    void testDeserialize() throws IOException {
//
//        ClassPathResource resource = new ClassPathResource("example/meso_template.json");
//        MesoTemplateData mesoTemplate = json.readObject(resource.getFile());
//
//        assertThat(mesoTemplate.getId()).isEqualTo(51L);
//        assertThat(mesoTemplate.getKey()).isEqualTo("whole-body-split-male-5x");
//        assertThat(mesoTemplate.getName()).isEqualTo("Whole Body Split");
//        assertThat(mesoTemplate.getEmphasis()).isEqualTo("Whole Body");
//        assertThat(mesoTemplate.getSex()).isEqualTo("male");
//        assertThat(mesoTemplate.getUserId()).isNull();
//        assertThat(mesoTemplate.getSourceTemplateId()).isNull();
//        assertThat(mesoTemplate.getSourceMesoId()).isNull();
//        assertThat(mesoTemplate.getPrevTemplateId()).isNull();
//        assertThat(mesoTemplate.getCreatedAt()).isEqualTo(Instant.parse("2023-10-03T23:30:11.734Z"));
//        assertThat(mesoTemplate.getUpdatedAt()).isEqualTo(Instant.parse("2023-10-03T23:30:11.734Z"));
//        assertThat(mesoTemplate.getDeletedAt()).isNull();
//        assertThat(mesoTemplate.getFrequency()).isEqualTo(5);
//    }
}
