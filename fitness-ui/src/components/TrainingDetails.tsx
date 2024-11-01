import { Box, Paper, Tab, Tabs, Typography } from "@mui/material"
import { useParams } from "react-router-dom"
import { Exercise, ExerciseSummary, Training } from "../types";
import { useEffect, useState } from "react";
import TrainingsApi from "../api/TrainingsApi";
import ExercisesApi from "../api/ExercisesApi";
import ExerciseChart from "./ExerciseChart";
import useWindowDimensions from "../hooks/useWindowDimensions";
import { Height } from "@mui/icons-material";

type TabPanelProps = {
    children?: React.ReactNode;
    index: number;
    value: number;
  }

const CustomTabPanel = (props: TabPanelProps) => {
    const { children, value, index, ...other } = props;
  
    return (
      <div
        role="tabpanel"
        hidden={value !== index}
        id={`simple-tabpanel-${index}`}
        style={{height: "calc(100% - 24px)"}}
        {...other}
      >
        {value === index && <Box sx={{ height: "100%", width: "100%" }}>{children}</Box>}
      </div>
    );
  }

type TrainingDetailsData = {
    exercises: Array<Exercise>,
    training: Training,
    exerciseSummaries: Array<ExerciseSummary>
}

const TrainingDetails = () => {
    const { id } = useParams();
    const { width, height } = useWindowDimensions();
    const [ data, setData ] = useState<TrainingDetailsData>({
        exercises: [],
        training: { id: "", date: new Date(), bodyWeight: 0, exercises: [] },
        exerciseSummaries: []
    });
    const [ tab, setTab ] = useState(0);

    useEffect(() => {
        async function fetchData() {
            if (id === undefined) {
                return;
            }
            const from = new Date(Date.UTC(2022, 0, 1));
            const to = new Date(Date.UTC(2022, 3, 1));

            const training = await TrainingsApi.getById(id);
            const exerciseIds = training.exercises.map(exercise => exercise.exerciseId);
            const exercises = await ExercisesApi.getByIds(exerciseIds);
            const summaries = await TrainingsApi.getSummary({
                exerciseIds: exerciseIds,
                from: from,
                to: to
            })
            summaries.sort((a, b) => {
                return training.exercises.filter(e => e.exerciseId === a.id)[0].order
                    - training.exercises.filter(e => e.exerciseId === b.id)[0].order;
            });
            setData({
                exercises: exercises,
                training: training,
                exerciseSummaries: summaries
            });
        }
        fetchData();   
    }, [id])

    const onTabChange = (event: React.SyntheticEvent, newValue: number) => {
        setTab(newValue);
    };

    return (
        <Box sx={{ px: 3 }}>
            <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1 }}>
                    { data.training.date.toDateString() }
                </Typography>
            </Box>
            <Paper sx={{ height: height - 180 }}>
                <Tabs value={tab} onChange={onTabChange}>
                    {data.exerciseSummaries.map(summary => (
                        <Tab sx={{ flexGrow: 1 }} label={data.exercises.filter(e => e.id === summary.id)[0].name} id={summary.id}/>
                    ))}
                </Tabs>
                {data.exerciseSummaries.map((summary, idx) => (
                    <CustomTabPanel key={idx} value={tab} index={idx}>
                        <ExerciseChart height="100%" width="100%" parameters={summary.parameters}/>
                    </CustomTabPanel>
                ))}
            </Paper>
        </Box>
    )
}

export default TrainingDetails;