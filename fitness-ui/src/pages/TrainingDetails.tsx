import { Backdrop, Box, Button, CircularProgress, Paper } from "@mui/material";
import { parseISO } from "date-fns";
import { useEffect, useState } from "react";
import { Training, TrainingFormData } from "../types";
import { createOrUpdateTraining, fetchTraining, isTraining } from "../api/trainingApi";
import { useNavigate, useParams } from "react-router-dom";
import TrainingDetailsFormInputs from "../components/TrainingDetailsFormInputs";
import { FormProvider, useForm } from "react-hook-form";
import FormInputTrainingDate from "../components/FormInputTrainingDate";
import { useDialogs } from "@toolpad/core";

const TrainingDetails = () => {
    const navigate = useNavigate();
    const { dateString } = useParams();
    const [loading, setLoading] = useState(true);
    const formMethods = useForm<TrainingFormData>();
    const { handleSubmit, setValue } = formMethods;
    const dialogs = useDialogs();

    useEffect(() => {
        const fetchData = async () => {
            if (dateString === undefined) {
                return;
            }
            setLoading(true);
            const date = parseISO(dateString);
            const result = await fetchTraining(date);
            if (isTraining(result)) {
                setValue("date", result.date);
                setValue("bodyweight", result.bodyweight);
                setValue("exercises", result.exercises);
            } else {
                setValue("date", date);
            }
            setLoading(false);
        };
        fetchData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [dateString]);

    const handleApply = async (trainingFormData: TrainingFormData) => {
        const training = trainingFormData as Training;
        await createOrUpdateTraining(training);
        navigate("/trainings");
    }

    const handleCancel = async () => {
        const result = await dialogs.confirm("Are you sure you want to cancel? Any unsaved changes will be lost!");
        if (result) {
            navigate("/trainings");
        }
    }

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '5px' }}>
                <FormProvider {...formMethods}>
                    <Box component={Paper} sx={{ height: '64px', padding: '16px' }}>
                        <FormInputTrainingDate name="date"/>
                    </Box>
                    <Box sx={{ height: 'calc(100% - 192px)', padding: '6px', overflowY: 'auto' }}>
                        <TrainingDetailsFormInputs/>
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

export default TrainingDetails;