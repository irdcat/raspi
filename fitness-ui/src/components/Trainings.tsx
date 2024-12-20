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
import useAsyncEffect from "../hooks/useAsyncEffect";
import { DeleteTrainingDialog } from "./dialogs/DeleteTrainingDialog";
import Training from "../model/Training";
import AddTrainingDialog from "./dialogs/AddTrainingDialog";
import EditTrainingDialog from "./dialogs/EditTrainingDialog";

export const Trainings = () => {
    const [ trainingList, setTrainingList ] = useState(new Array<Training>());
    const navigate = useNavigate();
    const { height } = useWindowDimensions();

    useAsyncEffect(async () => {
        await TrainingsApi.get()
            .then(trainings => trainings.sort((a, b) => b.date.getTime() - a.date.getTime()))
            .then(trainings => setTrainingList(trainings))
    }, async () => {
        // NOOP
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
                setTrainingList(trainingList);
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
                <AddTrainingDialog response={(training) => onAddTraining(training)}>
                    {(showDialog) => (
                        <Button onClick={showDialog} variant="outlined" color="success">Add</Button>
                    )}
                </AddTrainingDialog>
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
                                        <EditTrainingDialog training={training} response={(t) => onEditTraining(t)}>
                                            {(showDialog) => (
                                                <Button onClick={showDialog} variant="outlined" color="secondary">Edit</Button>
                                            )}
                                        </EditTrainingDialog>
                                        <DeleteTrainingDialog response={() => onDeleteTraining(training.id)}>
                                            {(showDialog) => (
                                                <Button onClick={showDialog} color="error">Delete</Button>
                                            )}
                                        </DeleteTrainingDialog>
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