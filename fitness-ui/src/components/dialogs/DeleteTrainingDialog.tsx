import { 
    Button, 
    Dialog, 
    DialogActions, 
    DialogContent, 
    DialogContentText, 
    DialogTitle 
} from "@mui/material"
import { 
    ReactElement, 
    useState 
} from "react";

type DeleteTrainingDialogProps = {
    response: () => void,
    children: (callback: () => void) => ReactElement
}

export const DeleteTrainingDialog = (props: DeleteTrainingDialogProps) => {
    const [ open, setOpen ] = useState(false);

    const showDialog = () => {
        setOpen(true);
    }

    const hideDialog = () => {
        setOpen(false);
    }

    const confirmRequest = () => {
        props.response();
        hideDialog();
    }
    
    return (
        <>
            { props.children(showDialog) }
            <Dialog 
                open={ open } 
                onClose={ hideDialog }>
                <DialogTitle>
                    Delete Exercise
                </DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure that you want to delete training?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={hideDialog}>Cancel</Button>
                    <Button onClick={confirmRequest}>Delete</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}