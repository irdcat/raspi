import { Autocomplete, Box, FormControl, InputLabel, MenuItem, Paper, Select, TextField } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers";
import { subDays } from "date-fns";
import { useState } from "react";
import AnalysisChart from "../components/AnalysisChart";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";
import { Metric, METRICS, Summary } from "../types";
import StringUtils from "../utils/StringUtils";

type AnalysisFilters = {
    from: Date,
    to: Date,
    exerciseName: string,
    metric: Metric
}

const Analysis = () => {
    const [summary] = useState<Summary>({
        exercise: { name: "Name", isBodyweight: false },
        parameters: new Map()
    });
    const [analysisFilters] = useState<AnalysisFilters>({
        from: subDays(new Date(), 180),
        to: new Date(),
        exerciseName: "",
        metric: METRICS[0]
    });

    return (
        <Box sx={{ height: '100%', paddingX: '7px' }}>
            <Box component={Paper} sx={{ height: '64px', paddingY: '12px', paddingX: '8px', display: 'flex' }}>
                <ResponsiveFilterBar>
                    <DatePicker
                        slotProps={{ textField: { size: "small" } }}
                        label="From"
                        name="from"
                        value={analysisFilters.from}/>
                    <DatePicker
                        slotProps={{ textField: { size: "small" } }}
                        label="To"
                        name="to"
                        value={analysisFilters.to}/>
                    <Autocomplete
                        options={[]}
                        value={analysisFilters.exerciseName}
                        renderInput={params => (
                            <TextField {...params}
                                label="Exercise"
                                size="small"
                                sx={{ minWidth: '200px' }}
                                />
                        )}/>
                    <FormControl size="small">
                        <InputLabel>Metric</InputLabel>
                        <Select sx={{ minWidth: '200px' }} value={analysisFilters.metric} label="Metric">
                            {METRICS.map((m, index) => (
                                <MenuItem value={m} key={index}>{StringUtils.camelCaseToSpaced(m)}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </ResponsiveFilterBar>
            </Box>
            <Box sx={{ height: 'calc(100% - 128px)', padding: '6px' }}>
                <AnalysisChart data={summary} metric={analysisFilters.metric}/>
            </Box>
        </Box>
    )
}

export default Analysis;