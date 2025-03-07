package irdcat.fitness.service

data class TrainingTemplateDto(
    val id: String,
    val name: String,
    val group: String,
    val description: String,
    val exercises: List<TrainingTemplateExerciseDto>
) {
    companion object {
        fun fromTrainingTemplate(trainingTemplate: TrainingTemplate): TrainingTemplateDto {
            return TrainingTemplateDto(
                trainingTemplate.id!!,
                trainingTemplate.name,
                trainingTemplate.group,
                trainingTemplate.description,
                trainingTemplate.exercises.map(TrainingTemplateExerciseDto::fromTrainingTemplateExercise)
            )
        }
    }

    fun toTrainingTemplate(): TrainingTemplate {
        return TrainingTemplate(
            id,
            name,
            group,
            description,
            exercises.map(TrainingTemplateExerciseDto::toTrainingTemplateExercise)
        )
    }
}
