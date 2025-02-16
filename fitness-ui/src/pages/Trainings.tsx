import { Accordion, AccordionDetails, AccordionSummary, Box, IconButton, Pagination, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from "@mui/material";
import { SyntheticEvent, useState } from "react";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { format, subDays } from "date-fns";
import { DatePicker } from "@mui/x-date-pickers";
import { LuDownload, LuPencilLine, LuPlus, LuTrash2, LuUpload } from "react-icons/lu";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";

const exercises = ["Squat", "Bench Press", "Deadlift", "Overhead Press", "Barbell Row", "Pull-Up", "Chin-Up", "Lunges", "Dips", "Face Pulls", "Lat Pulldown", "Seated Row", "Dumbbell Press", "Romanian Deadlift", "Bulgarian Split Squat", "Leg Press", "Calf Raise", "Hip Thrust", "Good Morning", "Plank", "Russian Twist", "Bicycle Crunch", "Cable Crunch", "Farmers Carry", "Kettlebell Swing", "Step-Ups", "Trap Bar Deadlift", "Incline Bench Press", "T-Bar Row", "Reverse Fly", "Landmine Press", "Pistol Squat", "Box Jumps", "Sled Push", "Battle Ropes", "Turkish Get-Up", "Dragon Flag", "Jefferson Deadlift", "One-Arm Dumbbell Row", "Zercher Squat", "Cable Crossover", "Chest Press Machine", "Hammer Curl", "Triceps Dip", "Close-Grip Bench Press", "Seated Calf Raise", "Landmine Squat", "Snatch Grip Deadlift", "Hanging Leg Raise", "Side Plank"];
const bodyWeightMap: { [date: string]: number } = {
    "2024-01-01": 70.0,
    "2024-01-02": 70.1,
    "2024-01-03": 70.3,
    "2024-01-04": 70.5,
    "2024-01-05": 70.7,
    "2024-01-06": 70.9,
    "2024-01-07": 71.1,
    "2024-01-08": 71.3,
    "2024-01-09": 71.6,
    "2024-01-10": 71.8,
    "2024-01-11": 72.0,
    "2024-01-12": 72.2,
    "2024-01-13": 72.5,
    "2024-01-14": 72.7,
    "2024-01-15": 72.9,
    "2024-01-16": 73.1,
    "2024-01-17": 73.4,
    "2024-01-18": 73.6,
    "2024-01-19": 73.8,
    "2024-01-20": 74.0,
    "2024-01-21": 74.3,
    "2024-01-22": 74.5,
    "2024-01-23": 74.7,
    "2024-01-24": 75.0,
    "2024-01-25": 75.2,
    "2024-01-26": 75.4,
    "2024-01-27": 75.6,
    "2024-01-28": 75.9,
    "2024-01-29": 76.1,
    "2024-01-30": 76.3,
    "2024-01-31": 76.5,
    "2024-02-01": 76.8,
    "2024-02-02": 77.0,
    "2024-02-03": 77.2,
    "2024-02-04": 77.5,
    "2024-02-05": 77.7,
    "2024-02-06": 77.9,
    "2024-02-07": 78.1,
    "2024-02-08": 78.4,
    "2024-02-09": 78.6,
    "2024-02-10": 78.8,
    "2024-02-11": 79.0,
    "2024-02-12": 79.3,
    "2024-02-13": 79.5,
    "2024-02-14": 79.7,
    "2024-02-15": 80.0
  };

const trainingData = Array.from({ length: 50 }, (_, i) => {
  const date = new Date(2024, 0, i + 1);
  return {
    date,
    bodyweight: bodyWeightMap[date.toISOString().split('T')[0]] || 70 + i * 0.2,
    exercises: exercises.slice(0, 3).map((exercise) => ({
      name: exercise,
      sets: [
        { reps: 5, weight: Math.floor(Math.random() * 50) + 50 },
        { reps: 5, weight: Math.floor(Math.random() * 50) + 50 },
        { reps: 5, weight: Math.floor(Math.random() * 50) + 50 }
      ]
    }))
  };
});

const Trainings = () => {
    const [expanded, setExpanded] = useState<number | false>(false);

    const handleExpansion = (panel: number) => (event: SyntheticEvent, isExpanded: boolean) => {
        setExpanded(isExpanded ? panel : false);
    }

    return (
        <Box sx={{ height: '100%', paddingX: '5px' }}>
            <Box component={Paper} sx={{ height: '64px', paddingY: '12px', paddingX: '4px', display: 'flex', columnGap: '2px' }}>
                <ResponsiveFilterBar>
                    <DatePicker
                        slotProps={{ textField: { size: "small" } }}
                        label="From"
                        name="from"
                        value={subDays(new Date(), 180)}/>
                    <DatePicker
                        slotProps={{ textField: { size: "small" } }}
                        label="To"
                        name="to"
                        value={new Date()}/> 
                </ResponsiveFilterBar>
                <Box sx={{ display: 'flex', columnGap: '4px' }}>
                    <Tooltip title="Import" arrow>
                        <IconButton sx={{ border: '1px solid gray', borderRadius: '4px', height: '40px' }}>
                            <LuUpload/>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title="Export" arrow>
                        <IconButton sx={{ border: '1px solid gray', borderRadius: '4px', height: '40px' }}>
                            <LuDownload/>
                        </IconButton>
                    </Tooltip>
                    <Tooltip title="Add" arrow>
                        <IconButton color="success" sx={{ border: '1px solid gray', borderRadius: '4px', height: '40px' }}>
                            <LuPlus/>
                        </IconButton>
                    </Tooltip>
                </Box>
            </Box>
            <Box sx={{ height: 'calc(100% - 192px)', padding: '6px', overflowY: 'scroll' }}>
                {trainingData.sort((a, b) => b.date.getTime() - a.date.getTime()).map((training, index) => (
                    <Accordion expanded={expanded === index} onChange={handleExpansion(index)} key={index}>
                        <AccordionSummary expandIcon={<ExpandMoreIcon/>} id={`content-${index}`}>
                            <Typography component="span" sx={{ flexShrink: 0, width: '33%' }}>
                                {format(training.date, "dd.MM.yyyy")}
                            </Typography>
                            <Typography component="span" sx={{ color: 'text.secondary' }}>
                                {`${training.bodyweight.toString()} kg`}
                            </Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                            <Box sx={{ display: 'flex', columnGap: '4px' }}>
                                <Tooltip title="Edit" arrow>
                                    <IconButton color="secondary" sx={{ border: '1px solid gray', borderRadius: '4px' }}>
                                        <LuPencilLine/>
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Delete" arrow>
                                    <IconButton color="error" sx={{ border: '1px solid gray', borderRadius: '4px' }}>
                                        <LuTrash2/>
                                    </IconButton>
                                </Tooltip>
                            </Box>
                            <TableContainer component={Paper}>
                                <Table sx={{ tableLayout: 'fixed' }}>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell width='170px'>
                                                <Typography variant="body2" sx={{ fontWeight: "bold" }}>Exercise</Typography>
                                            </TableCell>
                                            <TableCell>
                                                <Typography variant="body2" sx={{ fontWeight: "bold" }}>Sets</Typography>
                                            </TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {training.exercises.map((exercise, exerciseIndex) => (
                                            <TableRow key={`row-${index}-${exerciseIndex}`}>
                                                <TableCell>{exercise.name}</TableCell>
                                                <TableCell>{exercise.sets.map(s => `${s.reps}x${s.weight}`).join(" ")}</TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </AccordionDetails>
                    </Accordion>
                ))}
            </Box>
            <Box component={Paper} sx={{ paddingY: '16px', paddingX: '16px', height: '64px' }}>
                <Pagination size="medium" count={10} variant="outlined" shape="rounded" />
            </Box>
        </Box>
    )
}

export default Trainings;