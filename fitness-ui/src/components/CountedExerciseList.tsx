import { ListItem, ListItemButton, Box, Typography, List } from "@mui/material";
import { GiMuscleUp, GiWeight } from "react-icons/gi";
import { CountedExercise } from "../types";
import ExerciseWithIcon from "./ExerciseWithIcon";

const CountedExerciseList = (props: { exercises: Array<CountedExercise> }) => {
    const { exercises } = props;

    return (
        <List>
            {exercises.map((countedExercise, index) => (
                <ListItem key={index} disablePadding>
                    <ListItemButton>
                        <Box sx={{ display: 'flex', width: '100%', columnGap: 1, padding: '3px' }}>
                            <ExerciseWithIcon exercise={countedExercise.exercise} sx={{ flexGrow: 1 }}/>
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