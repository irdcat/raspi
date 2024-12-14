import { useForm } from "react-hook-form"
import TrainingFormData from "../../model/TrainingFormData"
import { Button, Paper } from "@mui/material"
import FormInputDate from "../form-components/FormInputDate"
import FormInputText from "../form-components/FormInputText"
import Exercise from "../../model/Exercise"
import { useState } from "react"
import useAsyncEffect from "../../hooks/useAsyncEffect"
import ExercisesApi from "../../api/ExercisesApi"
import FormInputTrainingExercises from "../form-components/FormInputTrainingExercises"
import TrainingExerciseFormData from "../../model/TrainingExerciseFormData"

type TrainingFormProps = {
    onSubmit: (data: TrainingFormData) => void,
    initialValues?: TrainingFormData
}

const TrainingForm = (props: TrainingFormProps) => {
    const [ exerciseList, setExerciseList ] = useState<Array<Exercise>>([]);

    useAsyncEffect(async () => {
        setExerciseList(await ExercisesApi.get());
    }, async () => {
        // NOOP
    }, [])

    const defaultValues = {
        date: new Date(),
        bodyWeight: 0,
        exercises: Array<TrainingExerciseFormData>()
    };

    const { register, handleSubmit, control } = useForm<TrainingFormData>({
        defaultValues: props.initialValues ? props.initialValues : defaultValues
    });

    return (
        <Paper
            style={{
                display: "grid",
                gridRowGap: "20px",
                padding: "20px",
                backgroundColor: "transparent"
            }}>
            <FormInputDate
                register={register("date", { required: { value: true, message: "Field is required" } })} 
                name="date" 
                control={control} 
                label="Date"/>
            <FormInputText
                register={register(
                    "bodyWeight", { 
                        required: { value: true, message: "Field is required"},
                        validate: { positive: (v) => v > 0 || "Value must be positive real number" }
                    })}
                name="bodyWeight"
                control={control}
                label="Body Weight (kg)"/>
            <FormInputTrainingExercises 
                name={"exercises"} 
                control={control} 
                registerFunction={register} 
                label={"Exercises"} 
                options={exerciseList}/>
            <Button onClick={handleSubmit(props.onSubmit)} variant="outlined">
                Submit
            </Button>
        </Paper>
    )
}

export default TrainingForm;