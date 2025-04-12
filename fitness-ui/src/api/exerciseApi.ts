import { CountedExercise, Page } from "../types";

const BASE_URL = "/fitness/api/exercises";
type CountedExercisePage = Page<CountedExercise>;

export const fetchCountedExercises = async (
    name: string,
    page: number,
    pageSize: number
): Promise<CountedExercisePage> => {

    const apiPage = page - 1;
    let urlWithParams = `${BASE_URL}/counted?page=${apiPage}&size=${pageSize}`;
    if (name.length !== 0) {
        urlWithParams += `&name=${name}`
    }

    const response = await fetch(urlWithParams)
        .then(r => r.json());
    return response as CountedExercisePage;
};

export const fetchExerciseNames = async (): Promise<Array<string>> => {

    const response = await fetch(`${BASE_URL}/names`)
        .then(r => r.json());
    return response as Array<string>
};