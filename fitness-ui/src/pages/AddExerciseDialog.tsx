import { 
    Button, 
    Checkbox, 
    Dialog, 
    DialogActions, 
    DialogContent, 
    DialogTitle, 
    FormControlLabel, 
    TextField 
} from "@mui/material";
import { ReactElement, useState } from "react";
import { Exercise } from "../types";

type AddExerciseDialogProps = {
    response: (exercise: Exercise) => void,
    children: (callbakc: () => void) => ReactElement
}

export const AddExerciseDialog = (props: AddExerciseDialogProps) => {
    const [ open, setOpen ] = useState(false);
    const [ exercise, setExercise ] = useState<Exercise>({ 
        id: "",
        name: "",
        isBodyWeight: false,
    });

    const showDialog = () => {
        setExercise({ id: "", name: "", isBodyWeight: false })
        setOpen(true);
    }

    const hideDialog = () => {
        setOpen(false);
    }

    const confirmRequest = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        props.response(exercise);
        hideDialog();
    }

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { target } = event;
        if (target.name === "isBodyWeight") {
            setExercise((prevState) => ({
                ...prevState,
                [target.name]: target.checked
            }));
        } else {
            setExercise((prevState) => ({
                ...prevState,
                [target.name]: target.value
            }));
        }
    }

    return (
        <>
            { props.children(showDialog) }
            <Dialog
                open={ open }
                onClose={ hideDialog }
                PaperProps={{
                    component: "form",
                    onSubmit: confirmRequest,
                }}>
                <DialogTitle>Add Exercise</DialogTitle>
                <DialogContent>
                    <TextField 
                        fullWidth 
                        label="Exercise Name" 
                        name="name" 
                        id="name"
                        margin="dense" 
                        value={exercise.name}
                        onChange={handleChange}/>
                    <FormControlLabel
                        control={
                            <Checkbox 
                                name="isBodyWeight" 
                                id="isBodyWeight" 
                                checked={exercise.isBodyWeight}
                                onChange={handleChange}/>
                        }
                        label="Body Weight Exercise"/>
                </DialogContent>
                <DialogActions>
                    <Button onClick={hideDialog}>Cancel</Button>
                    <Button type="submit">Add</Button>
                </DialogActions>
            </Dialog>
        </>
    )
}