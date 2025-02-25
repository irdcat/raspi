import { Controller, useFormContext } from "react-hook-form";
import ExerciseWithIcon from "./ExerciseWithIcon";

const FormInputExercise = (props: {
    name: string
}) => {

    const { name } = props;
    const { control, register } = useFormContext();
    
    return (
        <Controller
            name={name}
            control={control}
            render={({
                field: { value }
            }) => (
                <>
                    <input {...register(`${name}.name`)} 
                        value={value.name} type="hidden"/>
                    <input {...register(`${name}.isBodyweight`)} 
                        value={value.isBodyweight} type="hidden"/>
                    <ExerciseWithIcon exercise={value}
                        sx={{ flexGrow: 1, alignSelf: 'center' }}/>
                </>
            )}/>
    )
}

export default FormInputExercise;