import { Box, IconButton, Typography } from "@mui/material";
import { useFieldArray, useFormContext } from "react-hook-form";
import { LuPlus, LuX } from "react-icons/lu";
import FormInputText from "./FormInputText";

const FormInputTrainingExerciseSets = (props: {
    name: string
}) => {
    const { name } = props;
    const { control } = useFormContext();
    const { fields, append, remove } = useFieldArray({ control, name });

    return (
        <>
            <Box sx={{ padding: '7px', display: 'flex', borderTop: '2px solid rgba(255, 255, 255, 0.23)' }}>
                <Typography sx={{ flexGrow: 1, alignSelf: 'center', fontSize: '20px' }}>Sets</Typography>
                <IconButton tabIndex={-1} color="success" onClick={() => append({ repetitions: 0, weight: 0 })}>
                    <LuPlus/>
                </IconButton>
            </Box>
            {fields.map((field, index) => (
                <Box 
                    key={field.id} 
                    sx={{ 
                        borderTop: '1px solid rgba(255, 255, 255, 0.23)',
                        display: 'flex',
                        columnGap: '10px',
                        padding: '5px',
                        alignItems: 'center'
                    }}>
                    <Box>{index + 1}</Box>
                    <Box sx={{ flexGrow: 1}}>
                        <FormInputText
                            name={`${name}.${index}.repetitions`}
                            options={{ 
                                required: { value: true, message: "Field is required" },
                                validate: { positive: (v: number) => parseInt(v.toString()) >= 0 || "Value must be positive integer number" }
                                }}/>
                    </Box>
                    <Box>x</Box>
                    <Box sx={{ flexGrow: 1}}>
                        <FormInputText
                            name={`${name}[${index}].weight`}
                            options={{ 
                                required: { value: true, message: "Field is required" },
                                validate: { positive: (v: number) => parseFloat(v.toString()) >= 0 || "Value must be positive real number" }
                                }}/>
                    </Box>
                    <Box>
                        <IconButton tabIndex={-1} color="error" onClick={() => remove(index)}>
                            <LuX/>
                        </IconButton>
                    </Box>
                </Box>
            ))}
        </>
    )
}

export default FormInputTrainingExerciseSets;