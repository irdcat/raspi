import { 
    Dialog,
    DialogContent, 
    DialogTitle, 
} from "@mui/material";
import { 
    ReactElement, 
    useState 
} from "react"
import ExerciseFormData from "../../model/ExerciseFormData";
import ExerciseForm from "../forms/ExerciseForm";
import Exercise from "../../model/Exercise";

type EditExerciseDialogProps = {
    exercise: Exercise,
    response: (exercise: Exercise) => void,
    children: (callback: () => void) => ReactElement
}

export const EditExerciseDialog = (props: EditExerciseDialogProps) => {
    const [ open, setOpen ] = useState(false);

    const showDialog = () => {
        setOpen(true);
    }

    const hideDialog = () => {
        setOpen(false);
    }

    const handleSubmit = (exerciseFormData: ExerciseFormData) => {
        props.response({
            id: props.exercise.id,
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
                <DialogTitle>Edit Exercise</DialogTitle>
                <DialogContent>
                    <ExerciseForm 
                        onSubmit={handleSubmit} 
                        initialValues={{
                            name: props.exercise.name,
                            isBodyWeight: props.exercise.isBodyWeight
                        }}
                    />
                </DialogContent>
            </Dialog>
        </>
    )
}