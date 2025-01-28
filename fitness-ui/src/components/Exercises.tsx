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
} from "@mui/material"
import { useState } from "react"
import { useNavigate } from "react-router-dom";
import useWindowDimensions from "../hooks/useWindowDimensions";
import ExercisesApi from "../api/ExercisesApi";
import { useAsyncEffect } from "../hooks/useAsyncEffect";
import Exercise from "../model/Exercise";
import { ButtonActivatedDialog } from "./dialogs/ButtonActivatedDialog";
import ExerciseFormData from "../model/ExerciseFormData";
import ExerciseForm from "./forms/ExerciseForm";
import { ButtonActivatedActionDialog } from "./dialogs/ButtonActivatedActionDialog";

export const Exercises = () => {
    const [ exerciseList, setExerciseList ] = useState(new Array<Exercise>());
    const navigate = useNavigate();
    const { height } = useWindowDimensions();

    useAsyncEffect(async () => {
        await ExercisesApi.get()
            .then(exercises => setExerciseList(exercises));
    }, []);

    const handleSummaryClick = (id: string) => {
        navigate(`/exercises/${id}`)
    }

    const onAddExercise = (exercise: Exercise) => {
        ExercisesApi
            .add(exercise)
            .then(e => setExerciseList([...exerciseList, e]));
    }

    const onEditExercise = (exercise: Exercise) => {
        ExercisesApi
            .update(exercise.id, exercise)
            .then(updated => {
                const newList = [...exerciseList];
                newList[exerciseList.findIndex(e => e.id === updated.id)] = updated;
                setExerciseList(newList);
            });
    }

    const onDeleteExercise = (id: string) => {
        ExercisesApi
            .delete(id)
            .then(deleted => setExerciseList(exerciseList.filter(e => e.id !== deleted.id)));
    }

    return (
        <Box sx={{ px: 3 }}>
            <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1 }}>
                    Exercises
                </Typography>
                <ButtonActivatedDialog title="Add Exercise" buttonColor="primary" buttonLabel="Add" buttonVariant="outlined">
                    {(close) =>
                    <ExerciseForm 
                        onSubmit={(formData: ExerciseFormData) => {
                            onAddExercise({id: "", name: formData.name, isBodyWeight: formData.isBodyWeight});
                            close();
                            }}/>
                    }
                </ButtonActivatedDialog>
            </Box>
            <TableContainer sx={{ maxHeight: height - 160 }} component={Paper}>
                <Table stickyHeader>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Name
                                </Typography>
                            </TableCell>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Uses body weight
                                </Typography>
                            </TableCell>
                            <TableCell align="right">
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Actions
                                </Typography>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody sx={{ overflowY: "scroll" }}>
                        { exerciseList.map((exercise) => (
                            <TableRow key={ exercise.id } sx={{ '&:lastchild td, &:last-child th': { border: 0 } }}>
                                <TableCell>{ exercise.name }</TableCell>
                                <TableCell>
                                    { exercise.isBodyWeight ? "Yes" : "No" }
                                </TableCell>
                                <TableCell align="right">
                                    <ButtonGroup variant="outlined">
                                        <Button onClick={() => handleSummaryClick(exercise.id)} color="success">Summary</Button>
                                        <ButtonActivatedDialog title="Edit Exercise" buttonColor="secondary" buttonVariant="outlined" buttonLabel="Edit">
                                            {(close) =>
                                            <ExerciseForm 
                                                onSubmit={(formData: ExerciseFormData) => {
                                                    onEditExercise({id: exercise.id, name: formData.name, isBodyWeight: formData.isBodyWeight});
                                                    close();
                                                }}
                                                initialValues={{
                                                    name: exercise.name,
                                                    isBodyWeight: exercise.isBodyWeight
                                                }}/>
                                            }
                                        </ButtonActivatedDialog>
                                        <ButtonActivatedActionDialog
                                            title="Delete Exercise"
                                            text="Are you sure you want to delete exercise?"
                                            cancelLabel="Cancel"
                                            confirmLabel="Delete"
                                            buttonColor="error"
                                            buttonLabel="Delete"
                                            onConfirm={() => onDeleteExercise(exercise.id)}
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