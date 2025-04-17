import BodyweightChart from "../components/BodyweightChart";
import { Backdrop, Box, Checkbox, CircularProgress, FormControlLabel, Paper } from "@mui/material";
import { DatePicker, DateValidationError, PickerChangeHandlerContext } from "@mui/x-date-pickers";
import { subDays } from "date-fns";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";
import { ChangeEvent, useEffect, useState } from "react";
import { fetchBodyweightSummary } from "../api/summaryApi";

type Filters = {
    from: Date,
    to: Date,
    fillGaps: boolean
};

const Bodyweight = () => {
    const [bodyweights, setBodyweights] = useState<{ [key: string]: number }>({});
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState<Filters>({
        from: subDays(new Date(), 180),
        to: new Date(),
        fillGaps: true
    });

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const result = await fetchBodyweightSummary(filters.from, filters.to);
            setBodyweights(result.parameters);
            setLoading(false);
        }
        fetchData();
    }, [filters]);

    const handleDateChange = (fieldName: keyof Filters) => 
        (value: Date | null, _: PickerChangeHandlerContext<DateValidationError>) => {
            if (value == null) {
                return;
            }
            setFilters(prevFilter => ({
                ...prevFilter,
                [fieldName]: value
            }));
        }

    const handleFillGapsChange = (event: ChangeEvent<HTMLInputElement>, checked: boolean) => {
        setFilters((prevFilters) => ({
            ...prevFilters,
            fillGaps: checked
        }));
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
                            value={filters.from}
                            onChange={handleDateChange("from")}/>
                        <DatePicker
                            slotProps={{ textField: { size: "small" } }}
                            label="To"
                            name="to"
                            value={filters.to}
                            onChange={handleDateChange("to")}/>
                        <FormControlLabel
                            label="Fill Gaps"
                            name="fillGaps"
                            control={<Checkbox checked={filters.fillGaps} onChange={handleFillGapsChange}/>}/>
                    </ResponsiveFilterBar>
                </Box>
                <Box sx={{ height: 'calc(100% - 128px)', padding: '6px' }}>
                    <BodyweightChart data={bodyweights} fillGaps={filters.fillGaps}/>
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

export default Bodyweight;