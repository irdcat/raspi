import TrainingExerciseSet from "./TrainingExerciseSet";

type TrainingExercise = {
    order: number,
    exerciseId: string,
    sets: Array<TrainingExerciseSet>
}

export default TrainingExercise;