import { Box, Paper, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ExercisesApi from "../api/ExercisesApi";
import useWindowDimensions from "../hooks/useWindowDimensions";
import TrainingsApi from "../api/TrainingsApi";
import { Exercise, ExerciseSummary } from "../types";
import ExerciseChart from "./ExerciseChart";

type ExerciseDetailsData = {
    exercise: Exercise,
    summary: ExerciseSummary
}

const ExerciseDetails = () => {
    const { id } = useParams()
    const { height } = useWindowDimensions()
    const [ data, setData ] = useState<ExerciseDetailsData>({
        exercise: { id: "", name: "", isBodyWeight: false },
        summary: { id: "", parameters: new Map() }
    }) 

    useEffect(() => {
        async function fetchData() {
            if (id !== undefined) {
                const from = new Date(Date.UTC(2022, 0, 1));
                const to = new Date(Date.UTC(2022, 3, 1));

                const e = await ExercisesApi.getById(id);
                const s = await TrainingsApi
                    .getSummary({ 
                        exerciseIds: [id], 
                        from: from,
                        to: to 
                    })
                    .then(s => s[0]);
                setData({exercise: e, summary: s});
            }
        }
        fetchData();
    }, [id])

    return (
        <Box sx={{ px: 3 }}>
            <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1 }}>
                    { data.exercise.name }
                </Typography>
            </Box>
            <Paper sx={{ height: height - 160 }}>
                <ExerciseChart parameters={data.summary.parameters} height="100%"/>
            </Paper>
        </Box>
    )
}

export default ExerciseDetails;