import { Box, List, ListItem, ListItemButton, Pagination, Paper, TextField, Typography } from "@mui/material";

const exercises = [
    { name: "Squat", count: 45 },
    { name: "Deadlift", count: 78 },
    { name: "Bench Press", count: 62 },
    { name: "Overhead Press", count: 53 },
    { name: "Pull-Up", count: 37 },
    { name: "Chin-Up", count: 48 },
    { name: "Barbell Row", count: 59 },
    { name: "Dumbbell Press", count: 66 },
    { name: "Lunges", count: 72 },
    { name: "Romanian Deadlift", count: 50 },
    { name: "Front Squat", count: 55 },
    { name: "Bulgarian Split Squat", count: 42 },
    { name: "Face Pulls", count: 60 },
    { name: "Lat Pulldown", count: 70 },
    { name: "Seated Row", count: 58 },
    { name: "Dumbbell Flyes", count: 63 },
    { name: "Hammer Curl", count: 49 },
    { name: "Triceps Dip", count: 68 },
    { name: "Close-Grip Bench Press", count: 52 },
    { name: "Cable Crossover", count: 47 },
    { name: "Chest Press Machine", count: 64 },
    { name: "Leg Press", count: 80 },
    { name: "Calf Raise", count: 74 },
    { name: "Seated Calf Raise", count: 61 },
    { name: "Hip Thrust", count: 77 },
    { name: "Good Morning", count: 56 },
    { name: "Hanging Leg Raise", count: 39 },
    { name: "Plank", count: 50 },
    { name: "Side Plank", count: 41 },
    { name: "Russian Twist", count: 58 },
    { name: "Bicycle Crunch", count: 54 },
    { name: "Cable Crunch", count: 65 },
    { name: "Farmers Carry", count: 73 },
    { name: "Kettlebell Swing", count: 79 },
    { name: "Zercher Squat", count: 48 },
    { name: "Jefferson Deadlift", count: 67 },
    { name: "One-Arm Dumbbell Row", count: 60 },
    { name: "Incline Bench Press", count: 57 },
    { name: "Trap Bar Deadlift", count: 75 },
    { name: "Snatch Grip Deadlift", count: 62 },
    { name: "T-Bar Row", count: 71 },
    { name: "Reverse Fly", count: 46 },
    { name: "Landmine Press", count: 55 },
    { name: "Pistol Squat", count: 44 },
    { name: "Step-Ups", count: 53 },
    { name: "Box Jumps", count: 69 },
    { name: "Sled Push", count: 76 },
    { name: "Battle Ropes", count: 66 },
    { name: "Turkish Get-Up", count: 58 },
    { name: "Dragon Flag", count: 50 }
];

const Exercises = () => {
    return (
        <Box sx={{ height: '100%', paddingX: '7px' }}>
            <Box component={Paper} sx={{ height: '64px', paddingY: '1px', paddingX: '6px', display: 'flex' }}>
                <Box sx={{ flexGrow: 1, paddingY: '10px' }}>
                    <TextField
                        size="small"
                        label="Search"
                        name="search"/>
                </Box>
            </Box>
            <Box sx={{ padding: 1, overflowY: 'scroll', height: 'calc(100% - 192px)' }}>
                <List>
                    {exercises.map((exercise, index) => (
                        <ListItem key={index} disablePadding>
                            <ListItemButton>
                                <Box sx={{ display: 'flex', width: '100%' }}>
                                    <Box sx={{ flexGrow: 1 }}>
                                        <Typography>
                                            {exercise.name}
                                        </Typography>
                                    </Box>
                                    <Box>
                                        <Typography sx={{ color: 'text.secondary'}}>
                                            {exercise.count}
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