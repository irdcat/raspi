import TrainingExerciseFormData from "./TrainingExerciseFormData";

type TrainingFormData = {
    date: Date,
    bodyWeight: number
    exercises: Array<TrainingExerciseFormData>
}

export default TrainingFormData;