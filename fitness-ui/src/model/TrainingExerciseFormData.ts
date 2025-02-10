import TrainingExerciseSetFormData from "./TrainingExerciseSetFormData";

type TrainingExerciseFormData = {
    exerciseId: string,
    sets: Array<TrainingExerciseSetFormData>
};

export default TrainingExerciseFormData;