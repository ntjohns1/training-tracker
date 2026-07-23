package com.noslen.training_tracker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noslen.training_tracker.dto.mesocycle.request.CreateMesocycleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end controller tests over the real service/persistence stack on the dev profile
 * (permissive security, stub user, seeded reference data) against in-memory H2. Kafka consumer
 * startup is disabled since no broker runs during tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:controllertest;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.kafka.consumer.auto-startup=false"
})
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bootstrapReturnsSeededReferenceData() throws Exception {
        mockMvc.perform(get("/api/bootstrap"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises.length()").value(301))
                .andExpect(jsonPath("$.muscleGroups.length()").value(12));
    }

    @Test
    void getExercisesFiltersByMuscleGroup() throws Exception {
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(301));

        MvcResult result = mockMvc.perform(get("/api/exercises").param("muscleGroupId", "1"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode filtered = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(filtered.size() > 0 && filtered.size() < 301, "filtered subset of the catalog");
        filtered.forEach(ex -> assertEquals(1, ex.get("muscleGroupId").asInt()));
    }

    @Test
    void createMesocycleThenFetchItBack() throws Exception {
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Test Meso", 4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(1L))),
                        new CreateMesocycleRequest.DayRequest("Pull",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(2L)))
                ),
                "lb", null, null, null);

        MvcResult created = mockMvc.perform(post("/api/mesocycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Meso"))
                .andExpect(jsonPath("$.unit").value("lb"))
                .andExpect(jsonPath("$.microRirs").value(2108)) // 4-week RIR scheme
                .andReturn();

        Long mesoId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        // Full nested fetch: 4 weeks, each with 2 day patterns
        mockMvc.perform(get("/api/mesocycles/{id}", mesoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mesoId))
                .andExpect(jsonPath("$.weeks.length()").value(4))
                .andExpect(jsonPath("$.weeks[0].days.length()").value(2))
                .andExpect(jsonPath("$.weeks[0].days[0].id").isNumber());

        // The new meso shows up in the user's list
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void missingMesocycleReturns404() throws Exception {
        mockMvc.perform(get("/api/mesocycles/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void fiveWeekMesoDerivesMicroRirs() throws Exception {
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Five Week", 5,
                List.of(
                        new CreateMesocycleRequest.DayRequest("A",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(1L))),
                        new CreateMesocycleRequest.DayRequest("B",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(2L)))
                ),
                "lb", null, null, null);

        mockMvc.perform(post("/api/mesocycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.microRirs").value(32108)); // 5-week RIR scheme
    }

    @Test
    void patchRenamesMesocycle() throws Exception {
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Original", 4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("A",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(1L))),
                        new CreateMesocycleRequest.DayRequest("B",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(2L)))
                ),
                "lb", null, null, null);
        MvcResult created = mockMvc.perform(post("/api/mesocycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        Long mesoId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/mesocycles/{id}", mesoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Renamed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed"));
    }

    @Test
    void createAndFetchMesoNote() throws Exception {
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Noted", 4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("A",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(1L))),
                        new CreateMesocycleRequest.DayRequest("B",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(2L)))
                ),
                "lb", null, null, null);
        MvcResult created = mockMvc.perform(post("/api/mesocycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        Long mesoId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/meso-notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mesoId\":" + mesoId + ",\"text\":\"first note\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("first note"));

        mockMvc.perform(get("/api/mesocycles/{mesoId}/notes", mesoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("first note"));
    }
}
