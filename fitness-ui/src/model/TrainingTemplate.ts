import Exercise from "./Exercise"

type TrainingTemplate = {
    id: string,
    name: string,
    groupName: string,
    description: string,
    exercises: Array<Exercise>
}

export default TrainingTemplate;