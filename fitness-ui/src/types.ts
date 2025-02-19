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
    volume: number,
    averageVolume: number,
    minIntensity: number,
    maxIntensity: number,
    averageIntensity: number,
    bodyweight: number,
    bodyweightVolume: number | null,
    averageBodyweightVolume: number | null,
}

export type Summary = {
    exercise: Exercise,
    parameters: Map<Date, SummaryParameters>
}

export type BodyweightSummary = {
    parameters: Map<Date, number>
}

export type Page<T> = {
    content: Array<T>,
    currentPage: number,
    pageSize: number,
    totalResults: number
}

export const METRICS = [
    "volume", 
    "averageVolume", 
    "minIntensity", 
    "maxIntensity", 
    "averageIntensity"
] as const;

export type Metric = (typeof METRICS)[number];