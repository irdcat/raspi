import BodyweightChart from "../components/BodyweightChart";
import { Box, Paper } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFnsV3";
import { subDays } from "date-fns";

const bodyWeightMap: Record<string, number> = {
    "2024-01-01": 70.0,
    "2024-01-02": 70.1,
    "2024-01-03": 70.3,
    "2024-01-04": 70.5,
    "2024-01-05": 70.7,
    "2024-01-06": 70.9,
    "2024-01-07": 71.1,
    "2024-01-08": 71.3,
    "2024-01-09": 71.6,
    "2024-01-10": 71.8,
    "2024-01-11": 72.0,
    "2024-01-12": 72.2,
    "2024-01-13": 72.5,
    "2024-01-14": 72.7,
    "2024-01-15": 72.9,
    "2024-01-16": 73.1,
    "2024-01-17": 73.4,
    "2024-01-18": 73.6,
    "2024-01-19": 73.8,
    "2024-01-20": 74.0,
    "2024-01-21": 74.3,
    "2024-01-22": 74.5,
    "2024-01-23": 74.7,
    "2024-01-24": 75.0,
    "2024-01-25": 75.2,
    "2024-01-26": 75.4,
    "2024-01-27": 75.6,
    "2024-01-28": 75.9,
    "2024-01-29": 76.1,
    "2024-01-30": 76.3,
    "2024-01-31": 76.5,
    "2024-02-01": 76.8,
    "2024-02-02": 77.0,
    "2024-02-03": 77.2,
    "2024-02-04": 77.5,
    "2024-02-05": 77.7,
    "2024-02-06": 77.9,
    "2024-02-07": 78.1,
    "2024-02-08": 78.4,
    "2024-02-09": 78.6,
    "2024-02-10": 78.8,
    "2024-02-11": 79.0,
    "2024-02-12": 79.3,
    "2024-02-13": 79.5,
    "2024-02-14": 79.7,
    "2024-02-15": 80.0
};

const Bodyweight = () => {
    return (
        <Box sx={{ height: '100%', paddingX: '7px' }}>
            <Box component={Paper} sx={{ height: '64px', paddingY: '1px', paddingX: '6px', display: 'flex' }}>
                <Box sx={{ paddingY: '10px' }}>
                    <LocalizationProvider dateAdapter={AdapterDateFns}>
                        <DatePicker
                            sx={{ paddingRight: 1, width: '160px' }}
                            slotProps={{ textField: { size: "small" } }}
                            label="From"
                            name="from"
                            value={subDays(new Date(), 180)}/>
                        <DatePicker
                            sx={{ paddingRight: 1, width: '160px' }}
                            slotProps={{ textField: { size: "small" } }}
                            label="To"
                            name="to"
                            value={new Date()}/>    
                    </LocalizationProvider>
                </Box>
            </Box>
            <Box sx={{ height: 'calc(100% - 128px)', padding: '6px' }}>
                <BodyweightChart data={bodyWeightMap}/>
            </Box>
        </Box>
    )
}

export default Bodyweight;