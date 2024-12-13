import { 
    Dialog,
    DialogContent, 
    DialogTitle
} from "@mui/material";
import { 
    ReactElement, 
    useState 
} from "react";
import ExerciseForm from "../forms/ExerciseForm";
import ExerciseFormData from "../../model/ExerciseFormData";
import Exercise from "../../model/Exercise";

type AddExerciseDialogProps = {
    response: (exercise: Exercise) => void,
    children: (callback: () => void) => ReactElement
}

export const AddExerciseDialog = (props: AddExerciseDialogProps) => {
    const [ open, setOpen ] = useState(false);

    const showDialog = () => {
        setOpen(true);
    }

    const hideDialog = () => {
        setOpen(false);
    }

    const handleSubmit = (exerciseFormData: ExerciseFormData) => {
        props.response({ 
            id: "",
            name: exerciseFormData.name,
            isBodyWeight: exerciseFormData.isBodyWeight
        });
        hideDialog();
    }

    return (
        <>
            { props.children(showDialog) }
            <Dialog
                open={ open }
                onClose={ hideDialog }>
                <DialogTitle>Add Exercise</DialogTitle>
                <DialogContent>
                    <ExerciseForm onSubmit={handleSubmit}/>
                </DialogContent>
            </Dialog>
        </>
    )
}