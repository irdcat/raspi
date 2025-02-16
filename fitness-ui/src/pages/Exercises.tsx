import { Box, IconButton, List, ListItem, ListItemButton, Pagination, Paper, TextField, Typography } from "@mui/material";
import { GiWeight, GiMuscleUp } from "react-icons/gi";
import { LuSearch } from "react-icons/lu";

const countedExercises = [
    { exercise: { name: "Squat", isBodyweight: false }, count: 45 },
    { exercise: { name: "Deadlift", isBodyweight: false }, count: 78 },
    { exercise: { name: "Bench Press", isBodyweight: false }, count: 62 },
    { exercise: { name: "Overhead Press", isBodyweight: true }, count: 53 },
    { exercise: { name: "Pull-Up", isBodyweight: true }, count: 37 },
    { exercise: { name: "Chin-Up", isBodyweight: true }, count: 48 },
    { exercise: { name: "Barbell Row", isBodyweight: false }, count: 59 },
    { exercise: { name: "Dumbbell Press", isBodyweight: false }, count: 66 },
    { exercise: { name: "Lunges", isBodyweight: true }, count: 72 },
    { exercise: { name: "Romanian Deadlift", isBodyweight: false }, count: 50 },
    { exercise: { name: "Front Squat", isBodyweight: false }, count: 55 },
    { exercise: { name: "Bulgarian Split Squat", isBodyweight: true }, count: 42 },
    { exercise: { name: "Face Pulls", isBodyweight: false }, count: 60 },
    { exercise: { name: "Lat Pulldown", isBodyweight: false }, count: 70 },
    { exercise: { name: "Seated Row", isBodyweight: false }, count: 58 },
    { exercise: { name: "Dumbbell Flyes", isBodyweight: false }, count: 63 },
    { exercise: { name: "Hammer Curl", isBodyweight: false }, count: 49 },
    { exercise: { name: "Triceps Dip", isBodyweight: true }, count: 68 },
    { exercise: { name: "Close-Grip Bench Press", isBodyweight: false }, count: 52 },
    { exercise: { name: "Cable Crossover", isBodyweight: false }, count: 47 },
    { exercise: { name: "Chest Press Machine", isBodyweight: false }, count: 64 },
    { exercise: { name: "Leg Press", isBodyweight: false }, count: 80 },
    { exercise: { name: "Calf Raise", isBodyweight: true }, count: 74 },
    { exercise: { name: "Seated Calf Raise", isBodyweight: false }, count: 61 },
    { exercise: { name: "Hip Thrust", isBodyweight: false }, count: 77 },
    { exercise: { name: "Good Morning", isBodyweight: false }, count: 56 },
    { exercise: { name: "Hanging Leg Raise", isBodyweight: true }, count: 39 },
    { exercise: { name: "Plank", isBodyweight: true }, count: 50 },
    { exercise: { name: "Side Plank", isBodyweight: true }, count: 41 },
    { exercise: { name: "Russian Twist", isBodyweight: true }, count: 58 },
    { exercise: { name: "Bicycle Crunch", isBodyweight: true }, count: 54 },
    { exercise: { name: "Cable Crunch", isBodyweight: false }, count: 65 },
    { exercise: { name: "Farmers Carry", isBodyweight: false }, count: 73 },
    { exercise: { name: "Kettlebell Swing", isBodyweight: false }, count: 79 },
    { exercise: { name: "Zercher Squat", isBodyweight: false }, count: 48 },
    { exercise: { name: "Jefferson Deadlift", isBodyweight: false }, count: 67 },
    { exercise: { name: "One-Arm Dumbbell Row", isBodyweight: false }, count: 60 },
    { exercise: { name: "Incline Bench Press", isBodyweight: false }, count: 57 },
    { exercise: { name: "Trap Bar Deadlift", isBodyweight: false }, count: 75 },
    { exercise: { name: "Snatch Grip Deadlift", isBodyweight: false }, count: 62 },
    { exercise: { name: "T-Bar Row", isBodyweight: false }, count: 71 },
    { exercise: { name: "Reverse Fly", isBodyweight: false }, count: 46 },
    { exercise: { name: "Landmine Press", isBodyweight: false }, count: 55 },
    { exercise: { name: "Pistol Squat", isBodyweight: true }, count: 44 },
    { exercise: { name: "Step-Ups", isBodyweight: true }, count: 53 },
    { exercise: { name: "Box Jumps", isBodyweight: true }, count: 69 },
    { exercise: { name: "Sled Push", isBodyweight: false }, count: 76 },
    { exercise: { name: "Battle Ropes", isBodyweight: false }, count: 66 },
    { exercise: { name: "Turkish Get-Up", isBodyweight: false }, count: 58 },
    { exercise: { name: "Dragon Flag", isBodyweight: true }, count: 50 }
];

const Exercises = () => {
    return (
        <Box sx={{ height: '100%', paddingX: '7px' }}>
            <Box component={Paper} sx={{ height: '64px', paddingY: '13px', paddingX: '6px', display: 'flex', columnGap: 1 }}>
                <TextField
                    fullWidth
                    size="small"
                    label="Search"
                    name="search"/>
                <IconButton sx={{ border: '1px solid gray', borderRadius: '4px', height: '40px' }}>
                    <LuSearch/>
                </IconButton>
            </Box>
            <Box sx={{ padding: 1, overflowY: 'scroll', height: 'calc(100% - 192px)' }}>
                <List>
                    {countedExercises.map((countedExercise, index) => (
                        <ListItem key={index} disablePadding>
                            <ListItemButton>
                                <Box sx={{ display: 'flex', width: '100%', columnGap: 1 }}>
                                    <Box sx={{ flexGrow: 1 }}>
                                        <Typography>
                                            {countedExercise.exercise.name}
                                        </Typography>
                                    </Box>
                                    <Box sx={{ padding: '1px' }}>
                                        {countedExercise.exercise.isBodyweight ? <GiMuscleUp/> : <GiWeight/>}
                                    </Box>
                                    <Box>
                                        <Typography sx={{ color: 'text.secondary'}}>
                                            {countedExercise.count}
                                        </Typography>
                                    </Box>
                                </Box>
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </Box>
            <Box component={Paper} sx={{ paddingY: '16px', paddingX: '16px', height: '64px' }}>
                <Pagination size="medium" count={10} variant="outlined" shape="rounded" />
            </Box>
        </Box>
    )
}

export default Exercises;