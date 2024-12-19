package irdcat.fitness.model

data class TrainingTemplateDto(
    val id: String? = null,
    val name: String? = null,
    val groupName: String? = null,
    val description: String? = null,
    val exercises: List<ExerciseDto>? = null
) {
    companion object {
        fun fromTrainingTemplate(trainingTemplate: TrainingTemplate): TrainingTemplateDto {
            return TrainingTemplateDto(
                trainingTemplate.id,
                trainingTemplate.name,
                trainingTemplate.groupName,
                trainingTemplate.description,
                trainingTemplate.exercises.map(ExerciseDto::fromExercise)
            )
        }
    }

    fun toTrainingTemplate(): TrainingTemplate {
        return toTrainingTemplate(id!!)
    }

    fun toTrainingTemplate(newId: String): TrainingTemplate {
        return TrainingTemplate(
            newId,
            name!!,
            groupName!!,
            description!!,
            exercises!!.map(ExerciseDto::toExercise)
        )
    }
}
