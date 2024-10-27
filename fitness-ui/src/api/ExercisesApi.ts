import { Exercise } from "../types";

export default class ExercisesApi {

    static async get(): Promise<Array<Exercise>> {
        return fetch("/api/exercises")
            .then(response => response.json())
    }

    static async add(exercise: Exercise): Promise<Exercise> {
        return fetch("/api/exercises", {
            method: "post",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(exercise)
        }).then(response => response.json())
    }

    static async update(id: string, exercise: Exercise): Promise<Exercise> {
        return fetch(`/api/exercises/${id}`, {
            method: "put",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(exercise)
        }).then(response => response.json())
    }

    static async delete(id: string): Promise<Exercise> {
        return fetch(`/api/exercises/${id}`, {
            method: "delete",
            headers: {
                "Accept": "application/json"
            }
        }).then(response => response.json())
    }
}