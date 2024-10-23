import { Box, Button, ButtonGroup, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import { Exercise } from "../types";
import { useNavigate } from "react-router-dom";
import { DeleteExerciseDialog } from "./DeleteExerciseDialog";
import { EditExerciseDialog } from "./EditExerciseDialog";
import { AddExerciseDialog } from "./AddExerciseDialog";

export const Exercises = () => {
    const [ exerciseList, setExerciseList ] = useState(new Array<Exercise>());
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchExercises() {
            const result = await fetch("/api/exercises")
                .then(response => response.json());
            setExerciseList(result);
        }
        fetchExercises();
    }, []);

    const handleSummaryClick = (id: string) => {
        navigate(`/exercise/${id}`)
    }

    const onAddExercise = (exercise: Exercise) => {
        fetch("/api/exercises", {
            method: "post",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(exercise)
        })
        .then(response => response.json())
        .then(e => setExerciseList([...exerciseList, e]));
    }

    const onEditExercise = (exercise: Exercise) => {
        fetch(`/api/exercises/${exercise.id}`, {
            method: "put", 
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(exercise)
        })
        .then(response => response.json())
        .then(updated => {
            const newList = [...exerciseList];
            newList[exerciseList.findIndex(e => e.id === updated.id)] = updated;
            setExerciseList(newList);
        });
    }

    const onDeleteExercise = (id: string) => {
        fetch(`/api/exercises/${id}`, {
            method: "delete" 
        })
        .then(response => response.json())
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
            <TableContainer component={Paper}>
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