package com.noslen.training_tracker.factory;

import com.noslen.training_tracker.dto.mesocycle.request.CreateMesocycleRequest;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.mesocycle.Mesocycle;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.day.DayMuscleGroupRepo;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;
import com.noslen.training_tracker.enums.MgName;
import com.noslen.training_tracker.enums.MgProgressionType;
import com.noslen.training_tracker.enums.Unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MesocycleFactory Unit Tests")
class MesocycleFactoryUnitTest {

    @Mock
    private ExerciseRepo exerciseRepo;
    
    @Mock
    private MuscleGroupRepo muscleGroupRepo;
    
    @Mock
    private DayMuscleGroupRepo dayMuscleGroupRepo;
    
    @Mock
    private DayFactory dayFactory;

    private MesocycleFactory mesocycleFactory;
    private static final Long TEST_USER_ID = 123L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mesocycleFactory = new MesocycleFactory(exerciseRepo, dayMuscleGroupRepo, muscleGroupRepo, dayFactory);
        
        // Setup default MuscleGroup mock since factory hardcodes muscleGroupId = 1L
        MuscleGroup defaultMuscleGroup = new MuscleGroup();
        defaultMuscleGroup.setId(1L);
        defaultMuscleGroup.setName(MgName.CHEST);
        when(muscleGroupRepo.findById(1L)).thenReturn(Optional.of(defaultMuscleGroup));
    }

    @Test
    @DisplayName("Should create mesocycle with basic properties from request")
    void shouldCreateMesocycleWithBasicProperties() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            4,
            List.of(), // Empty days list means no days per week
            "kgs",
            Map.of(),
            null,
            null
        );
        
        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request, TEST_USER_ID);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Mesocycle");
        assertThat(result.getWeeks()).isEmpty(); // Empty days list results in no weeks created
        assertThat(result.getDays()).isEqualTo(0); // 0 days total (empty days list * 4 weeks = 0)
        assertThat(result.getUnit()).isEqualTo(Unit.KGS);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getSourceTemplateId()).isNull();
        assertThat(result.getSourceMesoId()).isNull();
    }

    @Test
    @DisplayName("Should handle null optional fields gracefully")
    void shouldHandleNullOptionalFields() {
        // Given
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Minimal Mesocycle",
            1,
            null, // null days
            "lbs",
            null, // null progressions
            null,
            null
        );
        
        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request, TEST_USER_ID);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Minimal Mesocycle");
        assertThat(result.getWeeks()).isEmpty(); // Should handle null days gracefully
        assertThat(result.getUnit()).isEqualTo(Unit.LBS);
        assertThat(result.getProgressions()).isEmpty(); // Should handle null progressions gracefully
    }

    @Test
    @DisplayName("Should throw exception when exercise not found")
    void shouldThrowExceptionWhenExerciseNotFound() {
        // Given
        when(exerciseRepo.findById(999L)).thenReturn(Optional.empty());
        
        // Add default MuscleGroup mock for hardcoded muscleGroupId = 1L
        MuscleGroup defaultMuscleGroup = new MuscleGroup();
        defaultMuscleGroup.setId(1L);
        defaultMuscleGroup.setName(MgName.CHEST);
        when(muscleGroupRepo.findById(1L)).thenReturn(Optional.of(defaultMuscleGroup));
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            1,
            List.of(new CreateMesocycleRequest.DayRequest(null, List.of(
                new CreateMesocycleRequest.DayExerciseRequest(999L)
            ))),
            "lbs",
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
    @DisplayName("Should throw exception when muscle group not found")
    void shouldThrowExceptionWhenMuscleGroupNotFound() {
        // Given
        when(muscleGroupRepo.findById(999L)).thenReturn(Optional.empty());
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            1,
            List.of(),
            "lbs",
            Map.of("999", new CreateMesocycleRequest.ProgressionRequest("regular", 999L)),
            null,
            null
        );
        
        // When & Then
        assertThatThrownBy(() -> mesocycleFactory.createFromRequest(request, TEST_USER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("MuscleGroup not found with ID: 999");
    }

    @Test
    @DisplayName("Should verify repository interactions for exercise lookup")
    void shouldVerifyRepositoryInteractionsForExerciseLookup() {
        // Given
        Exercise mockExercise = new Exercise();
        mockExercise.setId(1L);
        mockExercise.setName("Test Exercise");
        when(exerciseRepo.findById(1L)).thenReturn(Optional.of(mockExercise));
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            1,
            List.of(new CreateMesocycleRequest.DayRequest(null, List.of(
                new CreateMesocycleRequest.DayExerciseRequest(1L)
            ))),
            "lbs",
            Map.of(),
            null,
            null
        );
        
        // When
        mesocycleFactory.createFromRequest(request, TEST_USER_ID);
        
        // Then
        verify(exerciseRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should verify repository interactions for muscle group lookup")
    void shouldVerifyRepositoryInteractionsForMuscleGroupLookup() {
        // Given
        MuscleGroup mockMuscleGroup = new MuscleGroup();
        mockMuscleGroup.setId(1L);
        mockMuscleGroup.setName(MgName.CHEST);
        when(muscleGroupRepo.findById(1L)).thenReturn(Optional.of(mockMuscleGroup));
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Test Mesocycle",
            1,
            List.of(),
            "lbs",
            Map.of("1", new CreateMesocycleRequest.ProgressionRequest("regular", 1L)),
            null,
            null
        );
        
        // When
        mesocycleFactory.createFromRequest(request, TEST_USER_ID);
        
        // Then
        verify(muscleGroupRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should handle complex nested structure correctly")
    void shouldHandleComplexNestedStructureCorrectly() {
        // Given
        Exercise exercise1 = new Exercise();
        exercise1.setId(1L);
        exercise1.setName("Exercise 1");
        exercise1.setMuscleGroupId(1L); // day muscle group is derived from the exercise's muscle group

        Exercise exercise2 = new Exercise();
        exercise2.setId(2L);
        exercise2.setName("Exercise 2");
        exercise2.setMuscleGroupId(1L);

        MuscleGroup muscleGroup1 = new MuscleGroup();
        muscleGroup1.setId(1L);
        muscleGroup1.setName(MgName.CHEST);
        
        when(exerciseRepo.findById(1L)).thenReturn(Optional.of(exercise1));
        when(exerciseRepo.findById(2L)).thenReturn(Optional.of(exercise2));
        when(muscleGroupRepo.findById(1L)).thenReturn(Optional.of(muscleGroup1));
        
        CreateMesocycleRequest request = new CreateMesocycleRequest(
            "Complex Mesocycle",
            2,
            List.of(
                new CreateMesocycleRequest.DayRequest("Day 1", List.of(
                    new CreateMesocycleRequest.DayExerciseRequest(1L),
                    new CreateMesocycleRequest.DayExerciseRequest(2L)
                )),
                new CreateMesocycleRequest.DayRequest("Day 2", List.of(
                    new CreateMesocycleRequest.DayExerciseRequest(1L)
                ))
            ),
            "kgs",
            Map.of("1", new CreateMesocycleRequest.ProgressionRequest("regular", 1L)),
            123L,
            456L
        );
        
        // When
        Mesocycle result = mesocycleFactory.createFromRequest(request, TEST_USER_ID);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Complex Mesocycle");
        assertThat(result.getWeeks()).hasSize(4); // 2 weeks * 2 day patterns = 4 total days
        assertThat(result.getDays()).isEqualTo(4); // Total days calculation: 2 day patterns * 2 weeks = 4
        assertThat(result.getUnit()).isEqualTo(Unit.KGS);
        assertThat(result.getSourceTemplateId()).isEqualTo(123L);
        assertThat(result.getSourceMesoId()).isEqualTo(456L);
        
        // Verify repository interactions
        verify(exerciseRepo, times(6)).findById(anyLong()); // 2 weeks × (2 + 1) exercises = 6 total lookups
        verify(muscleGroupRepo, times(5)).findById(1L); // 4 days × 1 muscle group + 1 progression = 5 total lookups
    }

    @Test
    @DisplayName("Should create mesocycle for soft delete")
    void shouldCreateMesocycleForSoftDelete() {
        // Given
        Mesocycle existingMesocycle = Mesocycle.builder()
            .id(1L)
            .name("Existing Mesocycle")
            .unit(Unit.LBS)
            .weeks(List.of()) // Initialize with empty list instead of null
            .progressions(Map.of()) // Initialize with empty map
            .build();
        
        // When
        Mesocycle result = mesocycleFactory.createForSoftDelete(existingMesocycle);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Existing Mesocycle");
        assertThat(result.getWeeks()).isEmpty();
        assertThat(result.getUnit()).isEqualTo(Unit.LBS);
        assertThat(result.getDeletedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should create mesocycle for finish")
    void shouldCreateMesocycleForFinish() {
        // Given
        Mesocycle existingMesocycle = Mesocycle.builder()
            .id(1L)
            .name("Existing Mesocycle")
            .unit(Unit.LBS)
            .weeks(List.of()) // Initialize with empty list instead of null
            .progressions(Map.of()) // Initialize with empty map
            .build();
        
        // When
        Mesocycle result = mesocycleFactory.createForFinish(existingMesocycle);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Existing Mesocycle");
        assertThat(result.getWeeks()).isEmpty();
        assertThat(result.getUnit()).isEqualTo(Unit.LBS);
        assertThat(result.getFinishedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
