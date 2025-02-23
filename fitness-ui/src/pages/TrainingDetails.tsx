import { Backdrop, Box, Button, CircularProgress, IconButton, Paper, TextField, Typography } from "@mui/material";
import Grid from "@mui/material/Grid2"
import { format, parseISO } from "date-fns";
import { useEffect, useState } from "react";
import { Training } from "../types";
import { fetchTraining } from "../api/trainingApi";
import ExerciseWithIcon from "../components/ExerciseWithIcon";
import { LuPlus } from "react-icons/lu";
import { useParams } from "react-router-dom";

const TrainingDetails = () => {
    const { dateString } = useParams();
    const [loading, setLoading] = useState(true);
    const [training, setTraining] = useState<Training>({
        date: new Date(),
        bodyweight: 0,
        exercises: []
    });

    useEffect(() => {
        const fetchData = async () => {
            if (dateString === undefined) {
                return;
            }
            setLoading(true);
            const date = parseISO(dateString);
            const result = await fetchTraining(date);
            setTraining(result!!);
            setLoading(false);
        };
        fetchData();
    }, [dateString]);

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '5px' }}>
                <Box component={Paper} sx={{ height: '64px', padding: '16px' }}>
                    <Typography sx={{ fontSize: '24px' }}>
                        {format(training.date, "dd.MM.yyyy")}
                    </Typography>
                </Box>
                <Box sx={{ height: 'calc(100% - 192px)', padding: '6px', overflowY: 'auto' }}>
                    <Box sx={{ padding: '8px' }}>
                        <Typography variant="h6">
                            Details
                        </Typography>
                    </Box>
                    <Box sx={{ padding: '8px' }}>
                        <TextField 
                            fullWidth
                            size="small" 
                            label="Bodyweight (kg)"
                            value={training.bodyweight}/>
                    </Box>
                    <Box sx={{ padding: '7px', display: 'flex' }}>
                        <Typography sx={{ flexGrow: 1, alignSelf: 'center' }} variant="h6">
                            Exercises
                        </Typography>
                        <IconButton
                            tabIndex={-1} 
                            color="success" 
                            sx={{ border: '1px solid rgba(255, 255, 255, 0.23)', borderRadius: '4px' }}>
                            <LuPlus/>
                        </IconButton>
                    </Box>
                    <Grid container spacing={2} sx={{ padding: '5px' }}>
                        {training.exercises.map((trainingExercise, index) => (
                            <Grid 
                                key={index} 
                                sx={{ border: '1px solid rgba(255, 255, 255, 0.23)', borderRadius: '4px', padding: '3px' }}
                                size={{ xs: 12, md: 6 }}>
                                <Box sx={{ display: 'flex', padding: '5px', columnGap: '5px' }}>
                                    <ExerciseWithIcon 
                                        exercise={trainingExercise.exercise} 
                                        sx={{ flexGrow: 1, alignSelf: 'center' }}/>
                                    <IconButton tabIndex={-1} color="success">
                                        <LuPlus/>
                                    </IconButton>
                                </Box>
                                {trainingExercise.sets.map((set, setIndex) => (
                                    <Box 
                                        key={setIndex} 
                                        sx={{ display: 'flex', borderTop: '1px solid rgba(255, 255, 255, 0.23)', columnGap: '10px', padding: '5px', alignItems: 'center' }}>
                                        <Box>
                                            {setIndex + 1}.
                                        </Box>
                                        <Box sx={{ flexGrow: 1 }}>
                                            <TextField fullWidth size="small" value={set.repetitions}/>
                                        </Box>
                                        <Box>x</Box>
                                        <Box sx={{ flexGrow: 1 }}>
                                            <TextField fullWidth size="small" value={set.weight}/>
                                        </Box>
                                    </Box>
                                ))}
                            </Grid>
                        ))}
                    </Grid>
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