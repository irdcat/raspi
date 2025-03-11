import { ApiError, FileType, TrainingTemplate } from "../types";

const BASE_URL = "/api/templates";

export const fetchTemplates = async (): Promise<Array<TrainingTemplate> | ApiError> => {
    const response = await fetch(BASE_URL)
        .then(r => r.json());
    return response as Array<TrainingTemplate> | ApiError;
}

export const fetchTemplateById = async (id: string): Promise<TrainingTemplate | ApiError> => {
    const response = await fetch(`${BASE_URL}/${id}`)
        .then(r => r.json());
    return response as TrainingTemplate | ApiError;
}

export const createTemplate = async (trainingTemplate: TrainingTemplate): Promise<TrainingTemplate | ApiError> => {
    const response = await fetch(BASE_URL, {
        method: 'post',
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify(trainingTemplate)
    }).then(r => r.json());
    return response as TrainingTemplate | ApiError
}

export const updateTemplate = async (id: string, trainingTemplate: TrainingTemplate): Promise<TrainingTemplate | ApiError> => {
    const response = await fetch(`${BASE_URL}/${id}`, {
        method: 'put',
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify(trainingTemplate)
    }).then(r => r.json());
    return response as TrainingTemplate | ApiError
}

export const deleteTemplate = async (id: string): Promise<void | ApiError> => {
    const response = await fetch(`${BASE_URL}/${id}`, {
        method: 'delete'
    });
    if (response.status !== 204) {
        return await response.json() as ApiError;
    }
}

export const exportTemplates = async (
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

export const isTemplateArray = (object: any): object is Array<TrainingTemplate> => {
    return !("requestId" in object);
}

export const isTemplate = (object: any): object is TrainingTemplate => {
    return "id" in object && "name" in object && "group" in object;
}