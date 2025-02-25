import { Box, Grid2, IconButton, Typography } from "@mui/material"
import FormInputText from "./FormInputText"
import { LuPlus, LuX } from "react-icons/lu"
import { useFieldArray, useFormContext } from "react-hook-form"
import FormInputTrainingExerciseSets from "./FormInputTrainingExerciseSets"
import FormInputExercise from "./FormInputExercise"
import { useDialogs } from "@toolpad/core"
import ExercisePromptDialog from "./ExercisePromptDialog"

const TrainingDetailsFormInputs = () => {
    const { control, register } = useFormContext()
    const { fields, append, remove } = useFieldArray({
        control, name: "exercises"
    });
    const dialogs = useDialogs();

    const onAddExercise = async () => {
        const result = await dialogs.open(ExercisePromptDialog);
        if (result != null) {
            append({
                id: "",
                exercise: result,
                sets: []
            });
        }
    }

    const onRemoveExercise = (index: number) => () => {
        remove(index);
    }

    return (
        <>
            <input type="hidden" {...register("date")}/>
            <Box sx={{ padding: '8px' }}>
                <Typography variant="h6">Details</Typography>
            </Box>
            <Box sx={{ padding: '8px' }}>
                <FormInputText 
                    name="bodyweight" 
                    label="Bodyweight (kg)"
                    options={{
                        required: { value: true, message: "Field is required" },
                        validate: { positive: (v: number) => parseFloat(v.toString()) >= 0 || "Value must be positive real number" }
                        }}/>
            </Box>
            <Box sx={{ padding: '7px', display: 'flex' }}>
                <Typography sx={{ flexGrow: 1, alignSelf: 'center' }} variant="h6">Exercises</Typography>
                <IconButton tabIndex={-1} color="success" onClick={onAddExercise} 
                    sx={{ border: '1px solid rgba(255, 255, 255, 0.23)', borderRadius: '4px' }}>
                    <LuPlus/>
                </IconButton>
            </Box>
            <Grid2 container spacing={2} sx={{ padding: '5px' }}>
                {fields.map((field, index) => (
                    <Grid2 key={field.id} size={{ xs: 12, md: 6 }}
                        sx={{ border: '1px solid rgba(255, 255, 255, 0.23)', borderRadius: '4px', padding: '3px' }}>
                        <Box sx={{ display: 'flex', padding: '5px', columnGap: '5px' }}>
                            <FormInputExercise name={`exercises.${index}.exercise`}/>
                            <IconButton tabIndex={-1} color="error" onClick={onRemoveExercise(index)}>
                                <LuX/>
                            </IconButton>
                        </Box>
                        <FormInputTrainingExerciseSets name={`exercises.${index}.sets`}/>
                    </Grid2>
                ))}
            </Grid2>
        </>
    )
}

export default TrainingDetailsFormInputs;