import { Button, Paper } from "@mui/material";
import { useForm } from "react-hook-form";
import FormInputText from "../form-components/FormInputText";
import FormInputCheckbox from "../form-components/FormInputCheckbox";
import ExerciseFormData from "../../model/ExerciseFormData";

type ExerciseFormProps = {
    onSubmit: (data: ExerciseFormData) => void,
    initialValues?: ExerciseFormData
};

const ExerciseForm = (props: ExerciseFormProps) => {
    const defaultValues = {
        name: "",
        isBodyWeight: false
    };

    const { register, handleSubmit, control } = useForm<ExerciseFormData>({
        defaultValues: props.initialValues ? props.initialValues : defaultValues
    });

    return (
        <Paper
            style={{
                display: "grid",
                padding: "20px",
                backgroundColor: "transparent"
            }}
        >
            <FormInputText 
                register={register("name", { required: { value: true, message: "Field is required" } })}
                name="name" 
                control={control} 
                label="Exercise name"/>
            <FormInputCheckbox name="isBodyWeight" control={control} label="Bodyweight exercise"/>

            <Button onClick={handleSubmit(props.onSubmit)} variant="outlined">
                Submit
            </Button>
        </Paper>
    )
};

export default ExerciseForm;