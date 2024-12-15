import { ReactElement, useState } from "react"
import Training from "../../model/Training"
import TrainingFormData from "../../model/TrainingFormData";
import { Dialog, DialogContent, DialogTitle } from "@mui/material";
import TrainingForm from "../forms/TrainingForm";

type AddTrainingDialogProps = {
    response: (training: Training) => void,
    children: (callback: () => void) => ReactElement
}

const AddTrainingDialog = (props: AddTrainingDialogProps) => {
    const [ open, setOpen ] = useState(false);

    const showDialog = () => {
        setOpen(true)
    }

    const hideDialog = () => {
        setOpen(false);
    }

    const handleSubmit = (trainingFormData: TrainingFormData) => {
        props.response({
            id: "",
            date: trainingFormData.date,
            bodyWeight: trainingFormData.bodyWeight,
            exercises: trainingFormData.exercises
                .map((trainingExercise, index) => ({
                    order: index,
                    exerciseId: trainingExercise.exercise.id,
                    sets: trainingExercise.sets
                        .map(trainingExerciseSet => ({
                            reps: trainingExerciseSet.reps,
                            weight: trainingExerciseSet.weight
                        }))
                }))
        });
        hideDialog();
    }

    return (
        <>
            { props.children(showDialog) }
            <Dialog fullWidth maxWidth="md" open={open} onClose={hideDialog}>
                <DialogTitle>Add Training</DialogTitle>
                <DialogContent>
                    <TrainingForm onSubmit={handleSubmit}/>
                </DialogContent>
            </Dialog>
        </>
    )
};

export default AddTrainingDialog;