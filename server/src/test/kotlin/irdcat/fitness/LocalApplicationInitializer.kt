package irdcat.fitness

import irdcat.fitness.model.ExerciseDto
import irdcat.fitness.repository.ExerciseRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class LocalApplicationInitializer(
    private val exerciseRepository: ExerciseRepository
): InitializingBean {

    override fun afterPropertiesSet() {
        exerciseRepository.insert(listOf(
            ExerciseDto(name = "Bench Press", isBodyWeight = false).toExercise(),
            ExerciseDto(name = "Barbell Row", isBodyWeight = false).toExercise(),
            ExerciseDto(name = "Deadlift", isBodyWeight = false).toExercise(),
            ExerciseDto(name = "Squat", isBodyWeight = false).toExercise(),
            ExerciseDto(name = "Over Head Press", isBodyWeight = false).toExercise(),
            ExerciseDto(name = "Dip", isBodyWeight = true).toExercise(),
            ExerciseDto(name = "Pull Up", isBodyWeight = true).toExercise()
        )).blockLast()
    }
}