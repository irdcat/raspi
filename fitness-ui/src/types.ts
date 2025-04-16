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
    order: number,
    exercise: Exercise,
    sets: Array<TrainingExerciseSet>
}

export interface Training {
    date: Date,
    bodyweight: number,
    exercises: Array<TrainingExercise>
}

export type TrainingExerciseSetFormData = {
    repetitions: number,
    weight: number
}

export type TrainingExerciseFormData = {
    id: string,
    exercise: Exercise,
    sets: Array<TrainingExerciseSetFormData>
}

export type TrainingFormData = {
    date: Date,
    bodyweight: number,
    exercises: Array<TrainingExerciseFormData>
}

export type TrainingTemplateExercise = {
    exercise: Exercise,
    setCount: number,
    minReps: number | null,
    maxReps: number | null
}

export type TrainingTemplate = {
    id: string,
    name: string,
    group: string,
    description: string,
    exercises: Array<TrainingTemplateExercise>
}

export type TrainingTemplateExerciseFormData = {
    exercise: Exercise,
    setCount: number,
    minReps: number | null,
    maxReps: number | null
}

export type TrainingTemplateFormData = {
    id: string,
    name: string,
    group: string,
    description: string,
    exercises: Array<TrainingTemplateExerciseFormData>
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
    parameters: { [key: string]: number }
}

export type Page<T> = {
    content: Array<T>,
    currentPage: number,
    pageSize: number,
    totalResults: number
}

export type ApiError = {
    timestamp: Date,
    path: string,
    status: number,
    error: string | null,
    requestId: string
}

export const METRICS = [
    "volume", 
    "averageVolume", 
    "minIntensity", 
    "maxIntensity", 
    "averageIntensity"
] as const;

export type Metric = (typeof METRICS)[number];

export const FILE_TYPES = [
    "json",
    "yaml"
] as const;

export type FileType = (typeof FILE_TYPES)[number];