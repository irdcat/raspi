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
import { useEffect, useState } from "react";
import { Training } from "../types";
import { useNavigate } from "react-router-dom";
import TrainingsApi from "../api/TrainingsApi";

export const Trainings = () => {
    const [ trainingList, setTrainingList ] = useState(new Array<Training>());
    const navigate = useNavigate();
    const { height } = useWindowDimensions();

    useEffect(() => {
        TrainingsApi
            .get()
            .then(trainings => trainings.sort((a, b) => b.date.getTime() - a.date.getTime()))
            .then(trainings => setTrainingList(trainings))
    }, []);

    const onClickSummary = (id: string) => {
        navigate(`/trainings/${id}`);
    }

    return (
        <Box sx={{ px: 3 }}>
            <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1}}>
                    Trainings
                </Typography>
                <Button variant="outlined" color="success">
                    Add
                </Button>
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
                                        <Button color="secondary">Edit</Button>
                                        <Button color="error">Delete</Button>
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