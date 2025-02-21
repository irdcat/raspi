import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import { DatePicker, DateValidationError, LocalizationProvider, PickerChangeHandlerContext } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFnsV3";
import { DialogProps } from "@toolpad/core";
import { useState } from "react";

const DatePromptDialog = (props: DialogProps<undefined, Date | null>) => {
    const [result, setResult] = useState(new Date());
    const { open, onClose } = props;

    const handleDateChange = (value: Date | null, _: PickerChangeHandlerContext<DateValidationError>) => {
        if (value !== null) {
            setResult(value);
        }
    }

    return (
        <Dialog fullWidth open={open} onClose={() => onClose(null)}>
            <DialogTitle>Date</DialogTitle>
            <DialogContent>
                <DialogContentText>Supply the date:</DialogContentText>
                <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <DatePicker 
                        label="Date" 
                        value={result} 
                        onChange={handleDateChange}
                        slotProps={{ textField: { size: "small", fullWidth: true } }}/>
                </LocalizationProvider>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => onClose(result)}>Ok</Button>
            </DialogActions>
        </Dialog>
    )
};

export default DatePromptDialog;