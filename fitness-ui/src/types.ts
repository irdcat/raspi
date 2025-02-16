export type Exercise = {
    name: string,
    isBodyweight: boolean
};

export type CountedExercise = {
    exercise: Exercise,
    count: number
};

export type TrainingExerciseSet = {
    repetitions: number,
    weight: number
};

export type TrainingExercise = {
    id: string,
    exercise: Exercise,
    sets: Array<TrainingExerciseSet>
}

export type Training = {
    date: Date,
    bodyweight: number,
    exercises: Array<TrainingExercise>
}

export type SummaryParameters = {
    volume: number | null,
    averageVolume: number | null,
    bodyweightVolume: number | null,
    minIntensity: number | null,
    maxIntensity: number | null,
    averageIntensity: number | null,
    bodyweight: number | null
}

export type Summary = {
    exercise: Exercise | null,
    parameters: Map<Date, SummaryParameters>
}

export const METRICS = [
    "volume", 
    "averageVolume", 
    "minIntensity", 
    "maxIntensity", 
    "averageIntensity"
] as const;

export type Metric = (typeof METRICS)[number];