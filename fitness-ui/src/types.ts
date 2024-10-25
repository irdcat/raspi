export type Exercise = {
    id: string,
    name: string, 
    isBodyWeight: false
}

export type TrainingExerciseSet = {
    reps: number,
    weight: number
}

export type TrainingExercise = {
    order: number,
    exerciseId: string,
    sets: Array<TrainingExerciseSet>
}

export type Training = {
    id: string,
    date: Date,
    bodyWeight: number,
    exercises: Array<TrainingExercise>
}