import { Box, Button, FormControl, Table, TableBody, Typography } from "@mui/material";
import { useFieldArray } from "react-hook-form";
import FormInputTrainingExerciseRow from "./FormInputTrainingExerciseRow";
import Exercise from "../../model/Exercise";

type FormInputTrainingExercisesProps = {
    name: string,
    control: any,
    registerFunction: any,
    label: string,
    options: Array<Exercise>,
    exercisesDisabled: boolean,
    setValue: any
}

const FormInputTrainingExercises = (props: FormInputTrainingExercisesProps) => {
    const fieldArrayHook = useFieldArray({
        control: props.control,
        name: props.name
    });

    return (
        <FormControl 
            sx={{
                display: "grid",
                gridRowGap: "10px",
                padding: "10px 10px", 
                borderRadius: "4px",
                borderStyle: "solid",
                borderWidth: "1px",
                borderColor: "rgba(255, 255, 255, 0.23)" 
            }}>
            <Box sx={{ display: "flex" }}>
                <Typography variant="h5" sx={{ flexGrow: 1 }}>{props.label}</Typography>
                <Button onClick={() => fieldArrayHook.append({ exercise: null, sets: []})} variant="outlined">
                    Add Exercise
                </Button>
            </Box>
            <Table>
                <TableBody>
                    {fieldArrayHook.fields.map((field, index) => (
                        <FormInputTrainingExerciseRow
                            key={field.id}
                            control={props.control}
                            name={`${props.name}.${index}`}
                            registerFunction={props.registerFunction}
                            options={props.options}
                            onDelete={() => fieldArrayHook.remove(index)}
                            exerciseDisabled={props.exercisesDisabled}
                            setValue={props.setValue}/>
                    ))}
                </TableBody>
            </Table>
        </FormControl>
    )
}

export default FormInputTrainingExercises;