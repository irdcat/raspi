import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import { useState } from "react";

type ButtonActivatedActionDialogProps = {
    title: string,
    text: string,
    cancelLabel: string,
    confirmLabel: string,
    onConfirm: () => void,
    buttonColor: "inherit" | "primary" | "secondary" | "success" | "error" | "info" | "warning",
    buttonLabel: string,
}

export const ButtonActivatedActionDialog = (props: ButtonActivatedActionDialogProps) => {
    const [ open, setOpen ] = useState(false);

    const confirm = () => {
        props.onConfirm();
        setOpen(false);
    }

    return (
        <>
            <Button onClick={() => setOpen(true)} color={props.buttonColor}>{props.buttonLabel}</Button>
            <Dialog fullWidth open={open} onClose={() => setOpen(false)}>
                <DialogTitle>{props.title}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {props.text}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>{props.cancelLabel}</Button>
                    <Button onClick={confirm}>{props.confirmLabel}</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}