import TrainingExercise from "./TrainingExercise";

type Training = {
    id: string,
    date: Date,
    bodyWeight: number,
    exercises: Array<TrainingExercise>
}

export default Training;