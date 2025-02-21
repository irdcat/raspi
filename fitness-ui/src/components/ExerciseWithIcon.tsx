import { Box, BoxProps, Typography } from "@mui/material";
import { Exercise } from "../types";
import { GiMuscleUp, GiWeight } from "react-icons/gi";

const ExerciseWithIcon = (props: BoxProps & { exercise: Exercise }) => {
    const { exercise } = props;

    return (
        <Box {...props as BoxProps}>
            <Box sx={{ width: '100%', display: 'flex', columnGap: '4px' }}>
                <Box sx={{ padding: '1px' }}>
                    {exercise.isBodyweight ? <GiMuscleUp/> : <GiWeight/>}
                </Box>
                <Box sx={{ flexGrow: 1 }}>
                    <Typography>
                        {exercise.name}
                    </Typography>
                </Box>
            </Box>
        </Box>
    )
};

export default ExerciseWithIcon;