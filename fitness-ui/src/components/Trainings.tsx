import { 
    Box, 
    Button, 
    ButtonGroup, 
    Paper, 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer,
    TableHead, 
    TableRow, 
    Typography 
} from "@mui/material";
import useWindowDimensions from "../hooks/useWindowDimensions";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TrainingsApi from "../api/TrainingsApi";
import { useAsyncEffect } from "../hooks/useAsyncEffect";
import Training from "../model/Training";
import { ButtonActivatedDialog } from "./dialogs/ButtonActivatedDialog";
import TrainingForm from "./forms/TrainingForm";
import TrainingFormData from "../model/TrainingFormData";
import { ButtonActivatedActionDialog } from "./dialogs/ButtonActivatedActionDialog";

export const Trainings = () => {
    const [ trainingList, setTrainingList ] = useState(new Array<Training>());
    const navigate = useNavigate();
    const { height } = useWindowDimensions();

    useAsyncEffect(async () => {
        await TrainingsApi.get()
            .then(trainings => trainings.sort((a, b) => b.date.getTime() - a.date.getTime()))
            .then(trainings => setTrainingList(trainings))
    }, []);

    const onClickSummary = (id: string) => {
        navigate(`/trainings/${id}`);
    }

    const onAddTraining = (training: Training) => {
        TrainingsApi
            .add(training)
            .then(added => [...trainingList, added])
            .then(trainings => trainings.sort((a, b) => b.date.getTime() - a.date.getTime()))
            .then(trainings => setTrainingList(trainings));
    }

    const onEditTraining = (training: Training) => {
        TrainingsApi
            .update(training.id, training)
            .then(updated => {
                const newList = [...trainingList];
                newList[trainingList.findIndex(t => t.id === updated.id)] = updated;
                let sorted = newList.sort((a, b) => b.date.getTime() - a.date.getTime());
                setTrainingList(sorted);
            });
    }

    const onDeleteTraining = (id: string) => {
        TrainingsApi
            .delete(id)
            .then(deleted => setTrainingList(trainingList.filter(t => t.id !== deleted.id)));
    }

    return (
        <Box sx={{ px: 3 }}>
            <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1}}>
                    Trainings
                </Typography>
                <ButtonActivatedDialog title="Add Training" buttonColor="primary" buttonLabel="Add" buttonVariant="outlined">
                    {(close) =>
                    <TrainingForm 
                        onSubmit={(formData: TrainingFormData) => {
                            onAddTraining({
                                id: "",
                                templateId: formData.templateId,
                                date: formData.date,
                                bodyWeight: formData.bodyWeight,
                                exercises: formData.exercises
                                    .map((trainingExercise, index) => ({
                                        order: index,
                                        exerciseId: trainingExercise.exerciseId,
                                        sets: trainingExercise.sets
                                            .map(trainingExerciseSet => ({
                                                reps: trainingExerciseSet.reps,
                                                weight: trainingExerciseSet.weight
                                            }))
                                    }))
                            })
                            close();
                        }}/>
                    }
                </ButtonActivatedDialog>
            </Box>
            <TableContainer sx={{ maxHeight: height - 160 }} component={Paper}>
                <Table stickyHeader aria-label="trainings table">
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Date
                                </Typography>
                            </TableCell>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Body Weight (kg)
                                </Typography>
                            </TableCell>
                            <TableCell align="right">
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Actions
                                </Typography>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        { trainingList.map((training) => (
                            <TableRow key={ training.id } sx={{ '&:lastchild td, &:last-child th': { border: 0} }}>
                                <TableCell>
                                    { training.date.toDateString() }
                                </TableCell>
                                <TableCell>
                                    { training.bodyWeight }
                                </TableCell>
                                <TableCell align="right">
                                    <ButtonGroup variant="outlined">
                                        <Button onClick={() => onClickSummary(training.id)} color="success">Summary</Button>
                                        <ButtonActivatedDialog title="Edit Training" buttonColor="secondary" buttonVariant="outlined" buttonLabel="Edit">
                                            {(close) =>
                                            <TrainingForm
                                                onSubmit={(formData: TrainingFormData) => {
                                                    onEditTraining({
                                                        id: training.id,
                                                        templateId: training.templateId,
                                                        date: formData.date,
                                                        bodyWeight: formData.bodyWeight,
                                                        exercises: formData.exercises
                                                            .map((trainingExercise, index) => ({
                                                                order: index,
                                                                exerciseId: trainingExercise.exerciseId,
                                                                sets: trainingExercise.sets
                                                                    .map(trainingExerciseSet => ({
                                                                        reps: trainingExerciseSet.reps,
                                                                        weight: trainingExerciseSet.weight
                                                                    }))
                                                            }))
                                                    })
                                                    close();
                                                }}
                                                initialValues={{
                                                    templateId: training.templateId,
                                                    date: training.date,
                                                    bodyWeight: training.bodyWeight,
                                                    exercises: training.exercises.map(te => ({
                                                        exerciseId: te.exerciseId,
                                                        sets: te.sets
                                                    }))
                                                }}/> 
                                            }
                                        </ButtonActivatedDialog>
                                        <ButtonActivatedActionDialog
                                            title="Delete Training"
                                            text="Are you sure you want to delete training?"
                                            cancelLabel="Cancel"
                                            confirmLabel="Delete"
                                            buttonColor="error"
                                            buttonLabel="Delete"
                                            onConfirm={() => onDeleteTraining(training.id)}
                                            />
                                    </ButtonGroup>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    )
}