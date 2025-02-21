import { Autocomplete, Backdrop, Box, CircularProgress, FormControl, InputLabel, MenuItem, Paper, Select, SelectChangeEvent, TextField } from "@mui/material";
import { DatePicker, DateValidationError, PickerChangeHandlerContext } from "@mui/x-date-pickers";
import { subDays } from "date-fns";
import { SyntheticEvent, useEffect, useState } from "react";
import AnalysisChart from "../components/AnalysisChart";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";
import { Metric, METRICS, Summary } from "../types";
import { fetchExerciseSummary } from "../api/summaryApi";
import { fetchExerciseNames } from "../api/exerciseApi";
import { useDialogs } from "@toolpad/core";
import { camelCaseToSpaced } from "../utils/stringUtils";

type AnalysisFilters = {
    from: Date,
    to: Date,
    exerciseName: string,
}

const Analysis = () => {
    const [exerciseNames, setExerciseNames] = useState<Array<string>>([]);
    const [metric, setMetric] = useState<Metric>("volume");
    const [loading, setLoading] = useState(true);
    const [summary, setSummary] = useState<Summary>({
        exercise: { name: "", isBodyweight: false },
        parameters: new Map()
    });
    const [analysisFilters, setAnalysisFilters] = useState<AnalysisFilters>({
        from: subDays(new Date(), 180),
        to: new Date(),
        exerciseName: ""
    });
    const dialogs = useDialogs();

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const result = await fetchExerciseNames();
            setExerciseNames(result);
            setLoading(false);
        };
        fetchData();
    }, [])

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const { exerciseName, from, to } = analysisFilters;
            if (exerciseName === "") {
                setSummary({ 
                    exercise: { name: "", isBodyweight: false },
                     parameters: new Map() 
                });
                setLoading(false);
                return;
            }
            const result = await fetchExerciseSummary(from, to, exerciseName);
            setSummary(result);
            setLoading(false);
        };
        fetchData();
    }, [analysisFilters]);

    const handleDateChange = (fieldName: keyof Omit<AnalysisFilters, "exerciseName">) =>
        (value: Date | null, _: PickerChangeHandlerContext<DateValidationError>) => {
            if (value == null) {
                return;
            }
            setAnalysisFilters(prevFilters => ({
                ...prevFilters,
                [fieldName]: value
            }))
        }

    const handleExerciseChange = (e: SyntheticEvent, value: string | null) => {
        if (value === null) {
            value = "";
        }
        setAnalysisFilters(prevFilters => ({
            ...prevFilters,
            exerciseName: value
        }));
    }

    const handleMetricChange = (e: SelectChangeEvent<Metric>) => {
        setMetric(e.target.value as Metric);
    }

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
                        <Autocomplete
                            options={exerciseNames}
                            value={analysisFilters.exerciseName}
                            onChange={handleExerciseChange}
                            renderInput={params => (
                                <TextField {...params}
                                    label="Exercise"
                                    size="small"
                                    sx={{ minWidth: '200px' }}/>
                            )}/>
                        <FormControl size="small">
                            <InputLabel>Metric</InputLabel>
                            <Select 
                                sx={{ minWidth: '200px' }} 
                                value={metric} 
                                label="Metric"
                                onChange={handleMetricChange}>
                                {METRICS.map((m, index) => (
                                    <MenuItem value={m} key={index}>{camelCaseToSpaced(m)}</MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </ResponsiveFilterBar>
                </Box>
                <Box sx={{ height: 'calc(100% - 128px)', padding: '6px' }}>
                    <AnalysisChart data={summary} metric={metric}/>
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