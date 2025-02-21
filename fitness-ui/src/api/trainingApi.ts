import { format } from "date-fns";
import { Page, Training } from "../types"

const BASE_URL = "/api/trainings";
type TrainingPage = Page<Training>;

export const fetchTrainings = async (
    from: Date, to: Date, page: number, pageSize: number
): Promise<TrainingPage> => {

    const formattedFrom = format(from, "yyyy-MM-dd");
    const formattedTo = format(to, "yyyy-MM-dd");
    const apiPage = page - 1;
    const urlWithParams = `${BASE_URL}?from=${formattedFrom}&to=${formattedTo}&page=${apiPage}&size=${pageSize}`;

    const response = await fetch(urlWithParams)
        .then(r => r.json());

    return response as TrainingPage
};

export const fetchTraining = async(
    date: Date
): Promise<Training | null> => {

    const formattedDate = format(date, "yyyy-MM-dd");
    const url = `${BASE_URL}/${formattedDate}`;

    const response = await fetch(url)
        .then(r => r.json())
        .catch(r => null);

    return response as Training;
};