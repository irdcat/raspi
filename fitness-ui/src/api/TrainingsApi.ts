import { Training } from "../types";

export default class TrainingsApi {
    private static reviver = (key: string, value: any): any => {
        if (typeof value === 'string') {
            let regex = /([0-9]{4})-([0-9]{2})-([0-9]{2})/;
            let result = regex.exec(value);
            if (result) {
                return new Date(+result[1], +result[2]-1, +result[3]);
            }
        }
        return value;
    };

    private static replacer = (key: string, value: any) => {
        if (value instanceof Date) {
            let date = value as Date;
            return `${date.getFullYear()}-${date.getMonth()}-${date.getDay()}`;
        }
    };

    static async get(): Promise<Array<Training>> {
        return fetch("/api/trainings")
            .then(response => response.text())
            .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }

    static async add(training: Training): Promise<Training> {
        return fetch("/api/trainings", {
            method: "post",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(training, TrainingsApi.replacer)
        })
        .then(response => response.text())
        .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }

    static async update(id: string, training: Training): Promise<Training> {
        return fetch(`/api/trainings/${id}`, {
            method: "put",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(training, TrainingsApi.replacer)
        })
        .then(response => response.text())
        .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }

    static async delete(id: string): Promise<Training> {
        return fetch(`/api/trainings/${id}`, {
            method: "delete",
            headers: {
                "Accept": "application/json"
            }
        })
        .then(response => response.text())
        .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }
}