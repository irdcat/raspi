import { Backdrop, Box, CircularProgress, Paper, Typography } from "@mui/material";
import { DatePicker, DateValidationError, PickerChangeHandlerContext } from "@mui/x-date-pickers";
import { parse, subDays } from "date-fns";
import { useEffect, useState } from "react";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";
import { IntensityMetrics, Summary, VolumeMetrics } from "../types";
import { fetchExerciseSummary } from "../api/summaryApi";
import { useParams } from "react-router-dom";
import ExerciseMetricsChart from "../components/ExerciseMetricsChart";

type AnalysisFilters = {
    from: Date,
    to: Date,
}

const summaryToExerciseVolumeMetrics = (summary: Summary): Map<Date, VolumeMetrics> => {
    return new Map(Object.entries(summary.parameters)
        .map(([date, parameters]) => {
            const metrics: VolumeMetrics = {
                avg: parameters.averageVolume,
                sum: parameters.volume
            };
            return [parse(date, "yyyy-MM-dd", new Date()), metrics] 
        }))
}

const summaryToExerciseIntensityMetrics = (summary: Summary): Map<Date, IntensityMetrics> => {
    return new Map(Object.entries(summary.parameters)
        .map(([date, parameters]) => {
            const metrics: IntensityMetrics = {
                avg: parameters.averageIntensity,
                min: parameters.minIntensity,
                max: parameters.maxIntensity
            };
            return [parse(date, "yyyy-MM-dd", new Date()), metrics]
        }));
}

const summaryToBodyweightVolumeMetrics = (summary: Summary): Map<Date, VolumeMetrics> => {
    const isPopulated = summary.exercise.isBodyweight
    
    if (!isPopulated) {
        return new Map();
    }

    return new Map(Object.entries(summary.parameters)
        .map(([date, parameters]) => {
            const metrics: VolumeMetrics = {
                avg: parameters.averageBodyweightVolume!!,
                sum: parameters.bodyweightVolume!!
            };
            return [parse(date, "yyyy-MM-dd", new Date()), metrics] 
        }))
}

const summaryToBodyweightIntensityMetrics = (summary: Summary): Map<Date, IntensityMetrics> => {
    const isPopulated = summary.exercise.isBodyweight
    
    if (!isPopulated) {
        return new Map();
    }
    
    return new Map(Object.entries(summary.parameters)
        .map(([date, parameters]) => {
            const metrics: IntensityMetrics = {
                avg: parameters.bodyweight,
                min: parameters.bodyweight,
                max: parameters.bodyweight
            };
            return [parse(date, "yyyy-MM-dd", new Date()), metrics]
        }));
}

const Analysis = () => {
    const { exerciseName } = useParams();
    const [loading, setLoading] = useState(true);
    const [summary, setSummary] = useState<Summary>({
        exercise: { name: "", isBodyweight: false },
        parameters: new Map()
    });
    const [analysisFilters, setAnalysisFilters] = useState<AnalysisFilters>({
        from: subDays(new Date(), 180),
        to: new Date()
    });

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const { from, to } = analysisFilters;
            if (exerciseName === "") {
                setSummary({ 
                    exercise: { name: "", isBodyweight: false },
                    parameters: new Map() 
                });
                setLoading(false);
                return;
            }
            const result = await fetchExerciseSummary(from, to, exerciseName!!);
            setSummary(result);
            setLoading(false);
        };
        fetchData();
    }, [analysisFilters, exerciseName]);

    const handleDateChange = (fieldName: keyof AnalysisFilters) =>
        (value: Date | null, _: PickerChangeHandlerContext<DateValidationError>) => {
            if (value == null) {
                return;
            }
            setAnalysisFilters(prevFilters => ({
                ...prevFilters,
                [fieldName]: value
            }))
        }

    const exerciseVolumeMetrics = summaryToExerciseVolumeMetrics(summary);
    const bodyweightVolumeMetrics = summaryToBodyweightVolumeMetrics(summary);
    const exerciseIntensityMetrics = summaryToExerciseIntensityMetrics(summary);
    const bodyweightIntensitymetrics = summaryToBodyweightIntensityMetrics(summary);

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '7px' }}>
                <Box component={Paper} sx={{ height: '64px', paddingY: '12px', paddingX: '8px', display: 'flex' }}>
                    <ResponsiveFilterBar>
                        <DatePicker
                            slotProps={{ textField: { size: "small" } }}
                            label="From"
                            name="from"
                            value={analysisFilters.from}
                            onChange={handleDateChange("from")}/>
                        <DatePicker
                            slotProps={{ textField: { size: "small" } }}
                            label="To"
                            name="to"
                            value={analysisFilters.to}
                            onChange={handleDateChange("to")}/>
                    </ResponsiveFilterBar>
                </Box>
                <Box>
                    <Typography variant="h5">
                        {exerciseName}
                    </Typography>
                </Box>
                <Box sx={{ height: 'calc(100% - 128px)', padding: '8px' }}>
                    <ExerciseMetricsChart 
                        volumeParameters={exerciseVolumeMetrics} 
                        volumeBodyweight={bodyweightVolumeMetrics}
                        intensityParameters={exerciseIntensityMetrics}
                        intensityBodyweight={bodyweightIntensitymetrics}/>
                </Box>
            </Box>
            <Backdrop 
                sx={(theme) => ({ color: '#fff', zIndex: theme.zIndex.drawer })}
                open={loading}>
                <CircularProgress color="inherit"/>
            </Backdrop>
        </>
    )
}

export default Analysis;