import { ListItem, ListItemButton, Box, Typography, List } from "@mui/material";
import { CountedExercise } from "../types";
import ExerciseWithIcon from "./ExerciseWithIcon";
import { useNavigate } from "react-router-dom";
import EmptyState from "./EmptyState";

const CountedExerciseList = (props: { exercises: Array<CountedExercise> }) => {
    const { exercises } = props;
    const navigate = useNavigate();

    const onExerciseClick = (exerciseName: string) => () => {
        navigate("/analysis", { state: { name: exerciseName } });
    }

    if (exercises.length === 0) {
        return (
            <EmptyState 
                title="No exercises found" 
                message="Change the filters or hit the gym"/>
        )
    }

    return (
        <List>
            {exercises.map((countedExercise, index) => (
                <ListItem key={index} disablePadding>
                    <ListItemButton onClick={onExerciseClick(countedExercise.exercise.name)}>
                        <Box sx={{ display: 'flex', width: '100%', columnGap: 1, padding: '3px' }}>
                            <ExerciseWithIcon exercise={countedExercise.exercise} sx={{ flexGrow: 1 }}/>
                            { 
                                countedExercise.best === null ? <></> :
                                <Box sx={{ textAlign: 'right' }}>
                                    <Typography sx={{ display: 'inline', color: 'text.secondary', fontWeight: 'semibold', fontSize: '14px' }}>
                                        1RM: {countedExercise.best} kg
                                    </Typography>
                                </Box>
                            }
                            <Box sx={{ width: '30px', textAlign: 'right' }}>
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