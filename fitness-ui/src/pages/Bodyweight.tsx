import BodyweightChart from "../components/BodyweightChart";
import { Backdrop, Box, CircularProgress, Paper } from "@mui/material";
import { DatePicker, DateValidationError, PickerChangeHandlerContext } from "@mui/x-date-pickers";
import { format, subDays } from "date-fns";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";
import { useEffect, useState } from "react";
import { BodyweightSummary } from "../types";

type Filters = {
    from: Date,
    to: Date
};

const Bodyweight = () => {
    const [bodyweights, setBodyweights] = useState<Map<Date, number>>(new Map());
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState<Filters>({
        from: subDays(new Date(), 180),
        to: new Date()
    });

    useEffect(() => {
        const fetchData = async () => {
            const from = format(filters.from, 'yyyy-MM-dd')
            const to = format(filters.to, 'yyyy-MM-dd')
            setLoading(true);
            const result = (await fetch(`/api/summary/bodyweight?from=${from}&to=${to}`)
                .then(r => r.json())) as BodyweightSummary
            setBodyweights(result.parameters)
            setLoading(false)
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
            }))
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
                    </ResponsiveFilterBar>
                </Box>
                <Box sx={{ height: 'calc(100% - 128px)', padding: '6px' }}>
                    <BodyweightChart data={bodyweights}/>
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