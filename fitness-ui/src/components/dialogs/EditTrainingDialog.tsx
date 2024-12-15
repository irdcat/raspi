import { ReactElement, useState } from "react";
import Training from "../../model/Training";
import TrainingFormData from "../../model/TrainingFormData";
import { Dialog, DialogContent, DialogTitle } from "@mui/material";
import TrainingForm from "../forms/TrainingForm";
import Exercise from "../../model/Exercise";
import useAsyncEffect from "../../hooks/useAsyncEffect";
import ExercisesApi from "../../api/ExercisesApi";

type EditTrainingDialogProps = {
    training: Training,
    response: (training: Training) => void,
    children: (callback: () => void) => ReactElement
};

const EditTrainingDialog = (props: EditTrainingDialogProps) => {
    const [ open, setOpen ] = useState(false);
    const [ exercises, setExercises ] = useState<Array<Exercise>>([]);

    useAsyncEffect(async () => {
        const exerciseIds = props.training.exercises.map(e => e.exerciseId);
        await ExercisesApi.get()
            .then(exercises => exercises.filter(e => exerciseIds.includes(e.id)))
            .then(filtered => setExercises(filtered));
    }, async () => {
        // NOOP
    }, [])

    const showDialog = () => {
        setOpen(true);
    }

    const hideDialog = () => {
        setOpen(false);
    }

    const handleSubmit = (trainingFormData: TrainingFormData) => {
        props.response({
            id: props.training.id,
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
            <Dialog fullWidth maxWidth="md"  open={open} onClose={hideDialog}>
                <DialogTitle>Edit Training</DialogTitle>
                <DialogContent>
                    <TrainingForm 
                        onSubmit={handleSubmit}
                        initialValues={{
                            date: props.training.date,
                            bodyWeight: props.training.bodyWeight,
                            exercises: props.training.exercises.map(te => ({
                                exercise: exercises.find(e => e.id === te.exerciseId)!!,
                                sets: te.sets
                            }))
                        }}/>
                </DialogContent>
            </Dialog>
        </>
    )
}

export default EditTrainingDialog;