import { Box, Button, Paper } from "@mui/material";
import { Training, TrainingFormData } from "../types";
import { createOrUpdateTraining } from "../api/trainingApi";
import { useLocation, useNavigate } from "react-router-dom";
import TrainingDetailsFormInputs from "../components/TrainingDetailsFormInputs";
import { FormProvider, useForm } from "react-hook-form";
import FormInputTrainingDate from "../components/FormInputTrainingDate";
import { useDialogs } from "@toolpad/core";

const TrainingDetails = () => {
    const navigate = useNavigate();
    const { state } = useLocation();
    const { training } = state;
    const formMethods = useForm<TrainingFormData>({
        defaultValues: training
    });
    const { handleSubmit } = formMethods;
    const dialogs = useDialogs();

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
    )
}

export default TrainingDetails;