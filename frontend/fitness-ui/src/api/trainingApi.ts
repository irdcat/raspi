import { addHours, format } from "date-fns";
import { ApiError, FileType, Page, Training } from "../types"

const BASE_URL = "/v2/fitness/api/trainings";
type TrainingPage = Page<Training>;

export const fetchTrainings = async (
    from: Date, to: Date, page: number, pageSize: number
): Promise<TrainingPage | ApiError> => {

    const formattedFrom = format(from, "yyyy-MM-dd");
    const formattedTo = format(to, "yyyy-MM-dd");
    const apiPage = page - 1;
    const urlWithParams = `${BASE_URL}?from=${formattedFrom}&to=${formattedTo}&page=${apiPage}&size=${pageSize}`;

    const response = await fetch(urlWithParams)
        .then(r => r.json());

    return response as TrainingPage | ApiError
};

export const fetchTraining = async(
    date: Date
): Promise<Training | ApiError> => {

    const formattedDate = format(date, "yyyy-MM-dd");
    const url = `${BASE_URL}/${formattedDate}`;

    const response = await fetch(url)
        .then(r => r.json());

    return response as Training | ApiError;
};

export const trainingExists = async(
    date: Date
): Promise<boolean> => {

    const formattedDate = format(date, "yyyy-MM-dd");
    const url = `${BASE_URL}/${formattedDate}`;
    const response = await fetch(url, { method: 'head' });
    return response.status === 200;
}

export const createOrUpdateTraining = async(
    training: Training
): Promise<Training | ApiError> => {

    const adjustedTraining = {
        ...training,
        date: addHours(training.date, 1)
    };
    const response = await fetch(BASE_URL, {
        method: 'post',
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify(adjustedTraining, (key, value) => {
            if (key === "date") {
                return format(value, "yyyy-MM-dd");
            } else {
                return value;
            }
        })
    }).then(r => r.json())

    return response as Training | ApiError
}

export const deleteTraining = async (
    date: Date
): Promise<void | ApiError> => {

    const formattedDate = format(date, "yyyy-MM-dd");
    const url = `${BASE_URL}/${formattedDate}`
    const response = await fetch(url, {
        method: 'delete'
    });

    if (response.status !== 204) {
        return await response.json() as ApiError;
    }
}

export const exportTrainings = async (
    format: FileType
): Promise<Blob | ApiError> => {
    
    const url = `${BASE_URL}/export/${format.toString()}`
    const response = await fetch(url);

    if (response.status === 200) {
        return await response.blob();
    } else {
        return await response.json() as ApiError;
    }
}

export const importTrainings = async (
    file: File
): Promise<void | ApiError> => {

    const url = `${BASE_URL}/import/${file.name.split('.').pop()}`
    const formData = new FormData();
    formData.append("file", file);
    const response = await fetch(url, {
        method: "post",
        body: formData
    });

    if (response.status !== 204) {
        return await response.json() as ApiError;
    }
}

export const isTraining = (object: any): object is Training => {
    return ("date" in object && "bodyweight" in object && "exercises" in object);
}

export const isTrainingPage = (object: any): object is Page<Training> => {
    return !("requestId" in object)
}