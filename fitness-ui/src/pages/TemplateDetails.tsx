import { useDialogs } from "@toolpad/core";
import { FormProvider, useFieldArray, useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { TrainingTemplate, TrainingTemplateFormData } from "../types";
import { Backdrop, Box, Button, CircularProgress, Grid2, IconButton, Paper, Typography } from "@mui/material";
import { createTemplate, fetchTemplateById, isTemplate, updateTemplate } from "../api/templateApi";
import FormInputText from "../components/FormInputText";
import FormInputExercise from "../components/FormInputExercise";
import { LuPlus, LuX } from "react-icons/lu";
import ExercisePromptDialog from "../components/ExercisePromptDialog";
import { useEffect, useState } from "react";

const TemplateDetails = () => {
    const { id } = useParams();
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const formMethods = useForm<TrainingTemplateFormData>();
    const { handleSubmit, control, register, setValue } = formMethods;
    const { fields, append, remove } = useFieldArray({
        control: control,
        name: "exercises"
    });

    useEffect(() => {
        const fetchData = async () => {
            if (id === undefined || id === "new") {
                setLoading(false);
                return;
            }
            setLoading(true);
            const result = await fetchTemplateById(id);
            if (isTemplate(result)) {
                setValue("id", result.id);
                setValue("name", result.name);
                setValue("group", result.group);
                setValue("description", result.description);
                setValue("exercises", result.exercises);
            }
            setLoading(false);
        };
        fetchData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const onAddExercise = async () => {
        const result = await dialogs.open(ExercisePromptDialog);
        if (result != null) {
            append({
                exercise: result,
                setCount: 0
            });
        }
    }

    const handleApply = async (trainingTemplateFormData: TrainingTemplateFormData) => {
        const template = trainingTemplateFormData as TrainingTemplate;
        if (id === undefined) {
            return
        }
        if (id !== "new") {
            await updateTemplate(id, template);
        } else {
            await createTemplate(template);
        }
        navigate("/templates");
    }

    const handleCancel = async () => {
        const result = await dialogs.confirm("Are you sure you want to cancel? Any unsaved changes will be lost!");
        if (result) {
            navigate("/templates");
        }
    }

    return (
        <>
            <Box sx={{ height: '100%', paddingx: '5px' }}>
                <FormProvider {...formMethods}>
                    <Box sx={{ height: 'calc(100% - 128px)', padding: '6px', overflowY: 'auto' }}>
                        <Box sx={{ padding: '8px' }}>
                            <Typography variant="h6">Details</Typography>
                        </Box>
                        <input type="hidden" {...register("id")}/>
                        <Box sx={{ padding: '8px' }}>
                            <FormInputText
                                name="name"
                                label="Name"
                                options={{
                                    required: { value: true, message: "Field is required" }
                                }}/>
                        </Box>
                        <Box sx={{ padding: '8px' }}>
                            <FormInputText
                                name="group"
                                label="Group"
                                options={{
                                    required: { value: true, message: "Field is required" }
                                }}/>
                        </Box>
                        <Box sx={{ padding: '8px' }}>
                            <FormInputText
                                name="description"
                                label="Description"
                                options={{
                                    required: { value: true, message: "Field is required"}
                                }}
                                multiline/>
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
                                <Grid2 key={field.id} size={{ xs: 12, md: 6 }}>
                                    <Box sx={{ display: 'flex', padding: '5px', columnGap: '5px' }}>
                                        <FormInputExercise name={`exercises.${index}.exercise`}/>
                                        <IconButton tabIndex={-1} color="error" onClick={() => remove(index)}>
                                            <LuX/>
                                        </IconButton>
                                    </Box>
                                    <FormInputText
                                        name={`exercises.${index}.setCount`}
                                        label="Number of sets"
                                        options={{
                                            required: { 
                                                value: true, 
                                                message: "Field is required" 
                                            },
                                            validate: { 
                                                positive: (v: number) => parseFloat(v.toString()) >= 0 
                                                    || "Value must be positive real number" 
                                            }
                                        }}/>
                                </Grid2>
                            ))}
                        </Grid2>
                    </Box>
                    <Box component={Paper} sx={{ height: '64px' }}>
                        <Box sx={{ width: '100%', display: 'flex', padding: '14px', columnGap: '8px', flexDirection: 'row-reverse' }}>
                            <Button onClick={handleSubmit(handleApply)} variant="outlined" color="success">
                                Apply
                            </Button>
                            <Button onClick={handleCancel} variant="outlined" color="error">
                                Cancel
                            </Button>
                        </Box>
                    </Box>
                </FormProvider>
            </Box>
            <Backdrop
                sx={(theme) => ({ color: '#fff', zIndex: theme.zIndex.drawer })}
                open={loading}>
                <CircularProgress color="inherit"/>    
            </Backdrop>
        </>
    )
}

export default TemplateDetails;