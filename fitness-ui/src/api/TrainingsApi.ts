import ExerciseSummary from "../model/ExerciseSummary";
import ExerciseSummaryQuery from "../model/ExerciseSummaryQuery";
import Training from "../model/Training";

export default class TrainingsApi {
    private static getBaseUrl = (): string => {
        let fitnessServerUrlEnvValue = process.env.FITNESS_SERVER_URL;
        if (fitnessServerUrlEnvValue === undefined) {
            return ""
        } else {
            return fitnessServerUrlEnvValue;
        }
    }

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
        if (typeof value === 'string') {
            let regex = /([0-9]{4})-([0-9]{2})-([0-9]{2})[TZ:0-9]*/;
            let result = regex.exec(value);
            if (result) {
                return value.substring(0, 10)
            }
        }
        return value;
    };

    static async get(): Promise<Array<Training>> {
        return fetch(`${this.getBaseUrl()}/api/trainings`)
            .then(response => response.text())
            .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }

    static async getById(id: string): Promise<Training> {
        return fetch(`${this.getBaseUrl()}/api/trainings/${id}`)
            .then(response => response.text())
            .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }

    static async add(training: Training): Promise<Training> {
        return fetch(`${this.getBaseUrl()}/api/trainings`, {
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
        return fetch(`${this.getBaseUrl()}/api/trainings/${id}`, {
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
        return fetch(`${this.getBaseUrl()}/api/trainings/${id}`, {
            method: "delete",
            headers: {
                "Accept": "application/json"
            }
        })
        .then(response => response.text())
        .then(responseText => JSON.parse(responseText, TrainingsApi.reviver));
    }

    static async getSummary(query: ExerciseSummaryQuery): Promise<Array<ExerciseSummary>> {
        return fetch(`${this.getBaseUrl()}/api/trainings/summary`, {
            method: "post",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(query, TrainingsApi.replacer)
        })
        .then(response => response.text())
        .then(responseText => JSON.parse(responseText, TrainingsApi.reviver))
    }
}