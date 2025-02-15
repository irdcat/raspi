import { Autocomplete, Box, FormControl, InputLabel, MenuItem, Paper, Select, SelectChangeEvent, TextField } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFnsV3";
import { format, subDays } from "date-fns";
import { useState } from "react";
import AnalysisChart from "../components/AnalysisChart";

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

const mapped = trainingData
    .filter(t => t.exercises.map(e => e.name).includes("Squat"))
    .map(t => ({ date: format(t.date, 'dd-MM-yyyy'), bodyweight: t.bodyweight, sets: t.exercises.find(e => e.name === "Squat")!!.sets, }));

const initial: { [date: string]: {
    bodyweight: number,
    volume: number,
    averageVolume: number,
    minIntensity: number,
    maxIntensity: number,
    averageIntensity: number
}} = {};

const analysisData = mapped.reduce((m, t) => (m[t.date] = {
    bodyweight: t.bodyweight,
    volume: t.sets.map(s => s.reps * s.weight).reduce((sum, v) => sum + v),
    averageVolume: t.sets.map(s => s.reps * s.weight).reduce((sum, v) => sum += v) / t.sets.length,
    minIntensity: t.sets.map(s => s.weight).reduce((min, v) => min < v ? min : v, Number.MAX_VALUE),
    maxIntensity: t.sets.map(s => s.weight).reduce((max, v) => max > v ? max : v, Number.MIN_VALUE),
    averageIntensity: t.sets.map(s => s.weight).reduce((sum, v) => sum + v) / t.sets.length        
}, m), initial)

const Analysis = () => {
    const metrics = ["Volume", "Intensity"]
    const [metric, setMetric] = useState<"Volume" | "Intensity">("Volume")

    const handleMetricChange = (e: SelectChangeEvent<"Volume" | "Intensity">) => {
        setMetric(e.target.value as ("Volume" | "Intensity"));
    }

    return (
        <Box sx={{ height: '100%', paddingX: '7px' }}>
            <Box component={Paper} sx={{ height: '64px', paddingY: '1px', paddingX: '6px', display: 'flex' }}>
                <Box sx={{ paddingY: '10px' }}>
                    <LocalizationProvider dateAdapter={AdapterDateFns}>
                        <DatePicker
                            sx={{ paddingRight: 1, width: '150px' }}
                            slotProps={{ textField: { size: "small" } }}
                            label="From"
                            name="from"
                            value={subDays(new Date(), 180)}/>
                        <DatePicker
                            sx={{ paddingRight: 1, width: '150px' }}
                            slotProps={{ textField: { size: "small" } }}
                            label="To"
                            name="to"
                            value={new Date()}/>    
                    </LocalizationProvider>
                </Box>
                <Box sx={{ paddingY: '10px', paddingRight: 1 }}>
                    <Autocomplete 
                        options={exercises}
                        value={"Squat"}
                        renderInput={params => (
                            <TextField {...params}
                                label="Exercise"
                                size="small"
                                sx={{ width: '150px'}}
                                />
                        )}/>
                </Box>
                <Box sx={{ paddingY: '10px' }}>
                    <FormControl size="small">
                        <InputLabel>Metric</InputLabel>
                        <Select value={metric} label="Metric" onChange={handleMetricChange}>
                            <MenuItem value="Volume">Volume</MenuItem>
                            <MenuItem value="Intensity">Intensity</MenuItem>
                        </Select>
                    </FormControl>
                </Box>
            </Box>
            <Box sx={{ height: 'calc(100% - 128px)', padding: '6px' }}>
                <AnalysisChart data={analysisData} metric={metric} isBodyweight={true}/>
            </Box>
        </Box>
    )
}

export default Analysis;