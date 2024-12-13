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
import { DeleteExerciseDialog } from "./dialogs/DeleteExerciseDialog";
import { EditExerciseDialog } from "./dialogs/EditExerciseDialog";
import { AddExerciseDialog } from "./dialogs/AddExerciseDialog";
import useWindowDimensions from "../hooks/useWindowDimensions";
import ExercisesApi from "../api/ExercisesApi";
import useAsyncEffect from "../hooks/useAsyncEffect";
import Exercise from "../model/Exercise";

export const Exercises = () => {
    const [ exerciseList, setExerciseList ] = useState(new Array<Exercise>());
    const navigate = useNavigate();
    const { height } = useWindowDimensions();

    useAsyncEffect(async () => {
        await ExercisesApi.get()
            .then(exercises => setExerciseList(exercises));
    }, async () => {
        // NOOP
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
                <AddExerciseDialog response={(exercise) => onAddExercise(exercise)}>
                    {(showDialog) => (
                        <Button onClick={showDialog} variant="outlined" color="success">Add</Button>
                    )}
                </AddExerciseDialog>
            </Box>
            <TableContainer sx={{ maxHeight: height - 160 }} component={Paper}>
                <Table stickyHeader aria-label="exercises table">
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
                                        <EditExerciseDialog exercise={exercise} response={(exercise) => onEditExercise(exercise)}>
                                            {(showDialog) => (
                                                <Button onClick={showDialog} color="secondary">Edit</Button>
                                            )}
                                        </EditExerciseDialog>
                                        <DeleteExerciseDialog response={() => onDeleteExercise(exercise.id)}>
                                            {(showDialog) => (
                                                <Button onClick={showDialog} color="error">Delete</Button>
                                            )}
                                        </DeleteExerciseDialog>
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