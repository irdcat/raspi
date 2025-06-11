import { format } from "date-fns";
import { BodyweightSummary, Summary } from "../types";

const BASE_URL = "/fitness/api/summary";

export const fetchExerciseSummary = async (
    from: Date,
    to: Date,
    exerciseName: string
): Promise<Summary> => {

    const formattedFrom = format(from, "yyyy-MM-dd");
    const formattedTo = format(to, "yyyy-MM-dd");
    const urlWithParams
        = `${BASE_URL}/exercise?name=${exerciseName}&from=${formattedFrom}&to=${formattedTo}`;
    
    const response = await fetch(urlWithParams)
        .then(r => r.json());
    return response as Summary;
};

export const fetchBodyweightSummary = async (
    from: Date,
    to: Date
): Promise<BodyweightSummary> => {

    const formattedFrom = format(from, "yyyy-MM-dd");
    const formattedTo = format(to, "yyyy-MM-dd");
    const urlWithParams = `${BASE_URL}/bodyweight?from=${formattedFrom}&to=${formattedTo}`;

    const response = await fetch(urlWithParams)
        .then(r => r.json());
    return response as BodyweightSummary;
};