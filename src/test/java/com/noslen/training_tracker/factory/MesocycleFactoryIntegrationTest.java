package com.noslen.training_tracker.factory;

import com.noslen.training_tracker.dto.mesocycle.request.CreateMesocycleRequest;
import com.noslen.training_tracker.enums.MgName;
import com.noslen.training_tracker.enums.MgProgressionType;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.enums.Unit;
import com.noslen.training_tracker.model.day.Day;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.model.progression.Progression;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MesocycleFactory Integration Tests")
class MesocycleFactoryIntegrationTest {

    @Mock
    private ExerciseRepo exerciseRepo;

    @Mock
    private MuscleGroupRepo muscleGroupRepo;

    @Mock
    private DayMuscleGroupRepo dayMuscleGroupRepo;

    @Mock
    private DayFactory dayFactory;

    private MesocycleFactory mesocycleFactory;

    private List<Exercise> mockExercises;
    private List<MuscleGroup> mockMuscleGroups;
    private static final Long TEST_USER_ID = 123L;

    @BeforeEach
    void setUp() {
        mesocycleFactory = new MesocycleFactory(exerciseRepo, dayMuscleGroupRepo, muscleGroupRepo, dayFactory);

        // Setup mock exercises with lenient stubbing. Each exercise carries a muscleGroupId
        // (cycling 1..3) since day muscle groups are derived from the exercise's muscle group.
        mockExercises = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Exercise exercise = new Exercise();
            exercise.setId((long) i);
            exercise.setName("Exercise " + i);
            exercise.setMuscleGroupId((long) ((i - 1) % 3 + 1)); // ex1->1, ex2->2, ex3->3, ex4->1, ex5->2
            mockExercises.add(exercise);
            lenient().when(exerciseRepo.findById((long) i)).thenReturn(Optional.of(exercise));
        }

        // Setup mock muscle groups with lenient stubbing
        mockMuscleGroups = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            MuscleGroup muscleGroup = new MuscleGroup();
            muscleGroup.setId((long) i);
            muscleGroup.setName(MgName.values()[i - 1]);
            mockMuscleGroups.add(muscleGroup);
            lenient().when(muscleGroupRepo.findById((long) i)).thenReturn(Optional.of(muscleGroup));
        }
    }

    @Test
    @DisplayName("Should create complete mesocycle entity hierarchy from CreateMesocycleRequest")
    void shouldCreateCompleteMesocycleEntityHierarchy() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Test Mesocycle",
                4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L),
                                                                      new CreateMesocycleRequest.DayExerciseRequest(2L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Pull Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(3L)
                                                              ))
                ),
                "lb",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 2L)
                ),
                null,
                null
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Mesocycle");
        assertThat(result.getWeeks()).hasSize(8); // 2 weeks × 4 day patterns = 8 total days
        assertThat(result.getUnit()).isEqualTo(Unit.LB);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        // Verify timestamp consistency
        long timeDifference = ChronoUnit.SECONDS.between(result.getCreatedAt(),
                                                         result.getUpdatedAt());
        assertThat(timeDifference).isLessThan(5);
    }

    @Test
    @DisplayName("Should create correct number of Day entities (weeks × days per week)")
    void shouldCreateCorrectNumberOfDayEntities() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Test Mesocycle",
                4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L),
                                                                      new CreateMesocycleRequest.DayExerciseRequest(2L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Pull Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(3L)
                                                              ))
                ),
                "lb",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 2L)
                ),
                null,
                null
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result.getWeeks()).hasSize(8); // 2 weeks × 4 days per week = 8 total days

        // Verify Day properties and relationships
        for (Day day : result.getWeeks()) {
            assertThat(day.getMesocycle().getId()).isEqualTo(result.getId());
            assertThat(day.getCreatedAt()).isNotNull();
            assertThat(day.getUpdatedAt()).isNotNull();

            // Verify week and position values are valid
            assertThat(day.getWeek()).isBetween(1, 4); // 4 weeks
            assertThat(day.getPosition()).isBetween(1, 2); // 2 day patterns
        }
    }

    @Test
    @DisplayName("Should create correct number of DayExercise entities (days × exercises per day)")
    void shouldCreateCorrectNumberOfDayExerciseEntities() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Test Mesocycle",
                4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L),
                                                                      new CreateMesocycleRequest.DayExerciseRequest(2L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Pull Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(3L)
                                                              ))
                ),
                "lb",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 2L)
                ),
                null,
                null
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWeeks()).hasSize(8); // 4 weeks × 2 day patterns = 8 total days

        // Count total DayExercise entities across all days
        int totalDayExercises = 0;
        for (Day day : result.getWeeks()) {
            if (day.getExercises() != null) {
                totalDayExercises += day.getExercises().size();
                for (DayExercise dayExercise : day.getExercises()) {
                    assertThat(dayExercise.getDay().getId()).isEqualTo(day.getId());
                    assertThat(dayExercise.getExercise()).isNotNull();
                    assertThat(dayExercise.getPosition()).isGreaterThan(0);
                    assertThat(dayExercise.getCreatedAt()).isNotNull();
                    assertThat(dayExercise.getUpdatedAt()).isNotNull();

                    // Verify exercise was properly fetched from repository
                    assertThat(mockExercises).anyMatch(exercise -> exercise.getId().equals(dayExercise.getExercise().getId()));
                }
            }
        }
        
        // We expect DayExercise entities to be created for each day pattern across all weeks
        // Pattern 1: 2 exercises, Pattern 2: 1 exercise = 3 exercises per week
        // 4 weeks × 3 exercises per week = 12 total DayExercise entities
        assertThat(totalDayExercises).isEqualTo(12);
    }

    @Test
    @DisplayName("Should create correct number of DayMuscleGroup entities (days × muscle groups)")
    void shouldCreateCorrectNumberOfDayMuscleGroupEntities() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Test Mesocycle",
                4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L),
                                                                      new CreateMesocycleRequest.DayExerciseRequest(2L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Pull Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(3L)
                                                              ))
                ),
                "lb",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 2L)
                ),
                null,
                null
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWeeks()).hasSize(8); // 4 weeks × 2 day patterns = 8 total days

        // Count total DayMuscleGroup entities across all days
        int totalDayMuscleGroups = 0;
        for (Day day : result.getWeeks()) {
            if (day.getMuscleGroups() != null) {
                totalDayMuscleGroups += day.getMuscleGroups().size();
                for (DayMuscleGroup dayMuscleGroup : day.getMuscleGroups()) {
                    assertThat(dayMuscleGroup.getDay().getId()).isEqualTo(day.getId());
                    assertThat(dayMuscleGroup.getMuscleGroup()).isNotNull();
                    // Week 1 ships programmed (it has starting sets); later weeks wait for the
                    // progression engine to fill them in.
                    assertThat(dayMuscleGroup.getStatus()).isEqualTo(
                            day.getWeek() != null && day.getWeek() == 1
                                    ? Status.PROGRAMMED
                                    : Status.UNPROGRAMMED);
                    assertThat(dayMuscleGroup.getCreatedAt()).isNotNull();
                    assertThat(dayMuscleGroup.getUpdatedAt()).isNotNull();

                    // Verify muscle group was properly fetched from repository
                    assertThat(mockMuscleGroups).anyMatch(mg -> mg.getId().equals(dayMuscleGroup.getMuscleGroup().getId()));
                }
            }
        }
        
        // DayMuscleGroups are derived from the distinct muscle groups trained each day:
        // Push Day = ex1(mg1) + ex2(mg2) => 2; Pull Day = ex3(mg3) => 1.
        // 4 weeks × (2 + 1) = 12 total DayMuscleGroup entities.
        assertThat(totalDayMuscleGroups).isEqualTo(12);
    }

    @Test
    @DisplayName("Should create Progression entities with correct types")
    void shouldCreateProgressionEntitiesWithCorrectTypes() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Progression Test Mesocycle",
                4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Day 1",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L)
                                                              ))
                ),
                "lb",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("secondary", 2L)
                ),
                null,
                null
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProgressions()).hasSize(2);

        // Verify progression for muscle group 1
        Progression progression1 = result.getProgressions().get(1L);
        assertThat(progression1).isNotNull();
        assertThat(progression1.getMesocycle().getId()).isEqualTo(result.getId());
        assertThat(progression1.getMuscleGroup().getId()).isEqualTo(1L);
        assertThat(progression1.getMgProgressionType()).isEqualTo(MgProgressionType.REGULAR);

        // Verify progression for muscle group 2
        Progression progression2 = result.getProgressions().get(2L);
        assertThat(progression2).isNotNull();
        assertThat(progression2.getMesocycle().getId()).isEqualTo(result.getId());
        assertThat(progression2.getMuscleGroup().getId()).isEqualTo(2L);
        assertThat(progression2.getMgProgressionType()).isEqualTo(MgProgressionType.SECONDARY);
    }

    @Test
    @DisplayName("Should maintain referential integrity across all entities")
    void shouldMaintainReferentialIntegrity() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Test Mesocycle",
                4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L),
                                                                      new CreateMesocycleRequest.DayExerciseRequest(2L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Pull Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(3L)
                                                              ))
                ),
                "lb",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 2L)
                ),
                null,
                null
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWeeks()).hasSize(8); // 4 weeks × 2 day patterns = 8 total days
        assertThat(result.getProgressions()).hasSize(2); // 2 progressions

        // Verify referential integrity between Mesocycle and Days
        for (Day day : result.getWeeks()) {
            assertThat(day.getMesocycle().getId()).isEqualTo(result.getId());
            assertThat(day.getMesocycle().getName()).isEqualTo(result.getName());
            
            // Verify referential integrity between Day and DayExercises
            if (day.getExercises() != null) {
                for (DayExercise dayExercise : day.getExercises()) {
                    assertThat(dayExercise.getDay().getId()).isEqualTo(day.getId());
                    assertThat(dayExercise.getExercise()).isNotNull();
                }
            }
            
            // Verify referential integrity between Day and DayMuscleGroups
            if (day.getMuscleGroups() != null) {
                for (DayMuscleGroup dayMuscleGroup : day.getMuscleGroups()) {
                    assertThat(dayMuscleGroup.getDay().getId()).isEqualTo(day.getId());
                    assertThat(dayMuscleGroup.getMuscleGroup()).isNotNull();
                }
            }
        }

        // Verify referential integrity between Mesocycle and Progressions
        for (Progression progression : result.getProgressions().values()) {
            assertThat(progression.getMesocycle().getId()).isEqualTo(result.getId());
            assertThat(progression.getMuscleGroup()).isNotNull();
        }
    }

    @Test
    @DisplayName("Should handle validation errors for invalid exercise references")
    void shouldHandleValidationErrorsForInvalidExerciseReferences() {
        // Given - Add mock for the invalid exercise ID that will return empty
        when(exerciseRepo.findById(999L)).thenReturn(Optional.empty());
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            4,
            List.of(new CreateMesocycleRequest.DayRequest(null, List.of(
                new CreateMesocycleRequest.DayExerciseRequest(999L) // Invalid exercise ID
            ))),
            "lb",
            Map.of(),
            null,
            null
        );
        
        // When & Then
        assertThatThrownBy(() -> mesocycleFactory.createFromRequest(request, TEST_USER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Exercise not found with ID: 999");
    }

    @Test
    @DisplayName("Should handle validation errors for invalid muscle group references")
    void shouldHandleValidationErrorsForInvalidMuscleGroupReferences() {
        // Given - Add mock for the invalid muscle group ID that will return empty
        when(muscleGroupRepo.findById(999L)).thenReturn(Optional.empty());
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            4,
            List.of(),
            "lb",
            Map.of("999", new CreateMesocycleRequest.ProgressionRequest("regular", 999L)), // Invalid muscle group ID
            null,
            null
        );
        
        // When & Then
        assertThatThrownBy(() -> mesocycleFactory.createFromRequest(request, TEST_USER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("MuscleGroup not found with ID: 999");
    }

    @Test
    @DisplayName("Should handle complex mesocycle creation")
    void shouldHandleComplexMesocycleCreation() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Complex Mesocycle",
                6,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(1L),
                                                                      new CreateMesocycleRequest.DayExerciseRequest(2L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Pull Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(3L)
                                                              )),
                        new CreateMesocycleRequest.DayRequest("Legs Day",
                                                              List.of(
                                                                      new CreateMesocycleRequest.DayExerciseRequest(4L)
                                                              ))
                ),
                "kg",
                Map.of(
                        "1",
                        new CreateMesocycleRequest.ProgressionRequest("regular", 1L),
                        "2",
                        new CreateMesocycleRequest.ProgressionRequest("secondary", 2L)
                ),
                123L,
                456L
        );

        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request,
                                                              TEST_USER_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Complex Mesocycle");
        assertThat(result.getWeeks()).hasSize(18); // 6 weeks × 3 day patterns = 18 total days
        assertThat(result.getUnit()).isEqualTo(Unit.KG);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
