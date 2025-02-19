import { ListItem, ListItemButton, Box, Typography, List } from "@mui/material";
import { GiMuscleUp, GiWeight } from "react-icons/gi";
import { CountedExercise } from "../types";

const CountedExerciseList = (props: { exercises: Array<CountedExercise> }) => {
    const { exercises } = props;

    return (
        <List>
            {exercises.map((countedExercise, index) => (
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
    )
}

export default CountedExerciseList;