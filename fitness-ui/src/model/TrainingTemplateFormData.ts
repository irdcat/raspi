import Exercise from "./Exercise"

type TrainingTemplateFormData = {
    name: string,
    groupName: string,
    description: string,
    exercises: Array<Exercise>
}

export default TrainingTemplateFormData;