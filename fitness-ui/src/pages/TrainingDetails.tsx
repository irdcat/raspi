import { Backdrop, Box, Button, CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Typography } from "@mui/material";
import { format, parseISO } from "date-fns";
import useRequiredParams from "../hooks/useRequiredParams";
import { useEffect, useState } from "react";
import { Training } from "../types";
import { fetchTraining } from "../api/trainingApi";
import ExerciseWithIcon from "../components/ExerciseWithIcon";

const TrainingDetails = () => {
    const { dateString } = useRequiredParams(["dateString"])
    const date = parseISO(dateString);
    const [loading, setLoading] = useState(true);
    const [training, setTraining] = useState<Training>({
        date: date,
        bodyweight: 0,
        exercises: []
    });

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const result = await fetchTraining(date);
            setTraining(result!!);
            setLoading(false);
        };
        fetchData();
    }, []);

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '5px' }}>
                <Box component={Paper} sx={{ height: '64px', padding: '16px' }}>
                    <Typography sx={{ fontSize: '24px' }}>
                        {format(date, "dd.MM.yyyy")}
                    </Typography>
                </Box>
                <Box sx={{ height: 'calc(100% - 192px)', padding: '6px', overflowY: 'auto' }}>
                    <Box sx={{ maxWidth: '768px', marginX: 'auto' }}>
                    <Box sx={{ padding: '3px'}}>
                        <TextField fullWidth label="Bodyweight (kg)"/>
                    </Box>
                    {training.exercises.map((trainingExercise, index) => (
                        <Box key={index} sx={{ border: '1px solid rgba(255, 255, 255, 0.23)', borderRadius: '4px', margin: '8px 3px', padding: '3px' }}>
                            <TableContainer>
                                <Table size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell colSpan={5}>
                                                <ExerciseWithIcon exercise={trainingExercise.exercise}/>
                                            </TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {trainingExercise.sets.map((set, setIndex) => (
                                            <TableRow
                                                key={setIndex}
                                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                                <TableCell width={5}>
                                                    <Box sx={{ paddingLeft: '5px' }}>
                                                        {setIndex + 1}
                                                    </Box>
                                                </TableCell>
                                                <TableCell width={10}>
                                                    {set.repetitions}
                                                </TableCell>
                                                <TableCell colSpan={3}>
                                                    {set.weight} kg
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Box>
                    ))}
                    </Box>
                </Box>
                <Box component={Paper} sx={{ height: '64px' }}>
                    <Box sx={{ width: '100%', display: 'flex', padding: '14px', columnGap: '8px', flexDirection: 'row-reverse' }}>
                        <Button variant="outlined" color="success">
                            Apply
                        </Button>
                        <Button variant="outlined" color="error">
                            Cancel
                        </Button>
                    </Box>
                </Box>
            </Box>
            <Backdrop
                sx={(theme) => ({ color: '#fff', zIndex: theme.zIndex.drawer })}
                open={loading}>
                <CircularProgress color="inherit"/>    
            </Backdrop>
        </>
    )
}

export default TrainingDetails;