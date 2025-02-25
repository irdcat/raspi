import { DialogProps } from "@toolpad/core";
import { Exercise } from "../types";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";
import { FormProvider, useForm } from "react-hook-form";
import FormInputText from "./FormInputText";
import FormInputCheckbox from "./FormInputCheckbox";

const ExercisePromptDialog = (props: DialogProps<undefined, Exercise | null>) => {
    const { open, onClose } = props;
    const formMethods = useForm<Exercise>({
        defaultValues: {
            name: "",
            isBodyweight: false
        }
    });
    const { handleSubmit } = formMethods;

    const onSubmit = (exercise: Exercise) => {
        onClose(exercise);
    }

    return (
        <Dialog fullWidth open={open} onClose={() => onClose(null)}>
            <DialogTitle>Exercise</DialogTitle>
            <DialogContent>
                <FormProvider {...formMethods}>
                    <FormInputText
                        name="name"
                        label="Name"
                        options={{ required: { value: true, message: "Field is required" } }}/>
                    <FormInputCheckbox
                        name="isBodyweight"
                        label="Bodyweight exercise"/>
                </FormProvider>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleSubmit(onSubmit)}>Ok</Button>
            </DialogActions>
        </Dialog>
    )
}

export default ExercisePromptDialog;