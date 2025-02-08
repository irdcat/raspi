import TrainingExerciseFormData from "./TrainingExerciseFormData";

type TrainingFormData = {
    templateId: string,
    date: Date,
    bodyWeight: number
    exercises: Array<TrainingExerciseFormData>
}

export default TrainingFormData;