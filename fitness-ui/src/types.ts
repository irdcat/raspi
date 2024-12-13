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

export type ExerciseSummaryQuery = {
    exerciseIds: Array<string>,
    from: Date,
    to: Date
}

export type ExerciseParameters = {
    volume: number,
    averageVolume: number,
    averageIntensity: number,
    minIntensity: number,
    maxIntensity: number
}

export type ExerciseSummary = {
    id: string,
    parameters: Map<Date, ExerciseParameters>
}