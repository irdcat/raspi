import { Button, Dialog, DialogContent, DialogTitle } from "@mui/material";
import { ReactElement, useState } from "react"

type ButtonActivatedDialogProps = {
    title: string,
    buttonColor: "inherit" | "primary" | "secondary" | "success" | "error" | "info" | "warning",
    buttonVariant: "text" | "outlined" | "contained",
    buttonLabel: string,
    children: (close: () => void) => ReactElement
}

export const ButtonActivatedDialog = (props: ButtonActivatedDialogProps) => {
    const [ open, setOpen ] = useState(false);

    return (
        <>
            <Button 
                onClick={() => setOpen(true)} 
                color={props.buttonColor}
                variant={props.buttonVariant}>
                    {props.buttonLabel}
            </Button>
            <Dialog fullWidth maxWidth="lg" open={open} onClose={() => setOpen(false)}>
                <DialogTitle>{props.title}</DialogTitle>
                <DialogContent>
                    {props.children(() => setOpen(false))}
                </DialogContent>
            </Dialog>
        </>
    )
}

