import Exercise from "./Exercise";
import TrainingExerciseSetFormData from "./TrainingExerciseSetFormData";

type TrainingExerciseFormData = {
    exercise: Exercise,
    sets: Array<TrainingExerciseSetFormData>
};

export default TrainingExerciseFormData;