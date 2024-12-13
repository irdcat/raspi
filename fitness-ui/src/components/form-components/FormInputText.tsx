import { TextField } from "@mui/material";
import { Controller } from "react-hook-form";

type FormInputTextProps = {
    name: string,
    control: any,
    register: any,
    label: string,
    setValue?: any
};

const FormInputText = (props: FormInputTextProps) => {
    return (
        <Controller
            name={props.name}
            control={props.control}
            render={({
                field: { onChange, value },
                fieldState: { error }
            }) => (
                <TextField 
                    helperText={error ? error.message : null}
                    inputRef={props.register}
                    size="medium"
                    error={!!error}
                    onChange={onChange}
                    value={value}
                    fullWidth
                    label={props.label}
                    variant="outlined"/>
            )}
        />
    );
}

export default FormInputText;