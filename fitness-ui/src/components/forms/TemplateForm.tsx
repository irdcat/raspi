import { useState } from "react"
import Exercise from "../../model/Exercise"
import TrainingTemplateFormData from "../../model/TrainingTemplateFormData"
import { useAsyncEffect } from "../../hooks/useAsyncEffect"
import ExercisesApi from "../../api/ExercisesApi"
import { useFieldArray, useForm } from "react-hook-form"
import { Box, Button, Paper, Table, TableBody, TableCell, TableRow, Typography } from "@mui/material"
import FormInputText from "../form-components/FormInputText"
import FormInputAutocomplete from "../form-components/FormInputAutocomplete"

type TemplateFromProps = {
    onSubmit: (data: TrainingTemplateFormData) => void,
    initialValues?: TrainingTemplateFormData
}

const TemplateForm = (props: TemplateFromProps) => {
    const [ exerciseList, setExerciseList ] = useState(new Array<Exercise>());

    useAsyncEffect(async () => {
        setExerciseList(await ExercisesApi.get());
    }, []);

    const defaultValues = {
        name: "",
        groupName: "",
        description: "",
        exercises: []
    };

    const { register, handleSubmit, control } = useForm<TrainingTemplateFormData>({
        defaultValues: props.initialValues ? props.initialValues : defaultValues
    })

    const exercisesFieldHookArray = useFieldArray({
        control: control,
        name: "exercises"
    });

    return (
        <Paper
            style={{
                display: "grid",
                gridRowGap: "20px",
                padding: "20px",
                backgroundColor: "transparent"
            }}>
            <FormInputText
                register={register("name", { required: { value: true, message: "Field is required" } })}
                name="name"
                control={control}
                label="Name"/>
            <FormInputText
                register={register("groupName", { required: { value: false, message: "" } })}
                name="groupName"
                control={control}
                label="Group name"/>
            <FormInputText
                register={register("description", { required: { value: true, message: "Field is required" } })}
                name="description"
                control={control}
                label="Description"/>
            <Box sx={{ display: "flex" }}>
                <Typography variant="h5" sx={{ flexGrow: 1 }}>Exercises</Typography>
                <Button onClick={() => exercisesFieldHookArray.append({ id: "", name: "", isBodyWeight: false })} variant="outlined">
                    Add Exercise
                </Button>
            </Box>
            <Table>
                <TableBody>
                    {exercisesFieldHookArray.fields.map((field, index) => (
                        <TableRow key={field.id}>
                            <TableCell>
                                <FormInputAutocomplete
                                    name={`exercises.${index}`}
                                    control={control}
                                    register={register(`exercises.${index}`, { required: { value: true, message: "Field is required" } })}
                                    label="Exercise"
                                    options={exerciseList}
                                    getOptionLabel={(e) => e.name}
                                    getOptionValue={(e) => e}/>
                            </TableCell>
                            <TableCell align="right">
                                <Button variant="outlined" onClick={() => exercisesFieldHookArray.remove(index)}>
                                    Delete
                                </Button>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
            <Button onClick={handleSubmit(props.onSubmit)} variant="outlined">
                Submit
            </Button>
        </Paper>
    )
}


export default TemplateForm;