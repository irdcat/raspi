import { TextField } from "@mui/material";
import { Controller, RegisterOptions, useFormContext } from "react-hook-form"

const FormInputText = (props: {
    name: string,
    label?: string,
    options?: RegisterOptions,
    multiline?: boolean
}) => {
    const { name, label, options, multiline } = props;
    const { control, register } = useFormContext();

    return (
        <Controller
            defaultValue={''}
            name={name}
            control={control}
            render={({
                field: { onChange, value },
                fieldState: { error }
            }) => (
                <TextField
                    helperText={error ? error.message : null}
                    inputRef={register(name, options) as any}
                    size="small"
                    error={!!error}
                    onChange={onChange}
                    value={value}
                    fullWidth
                    label={label}
                    multiline={multiline}/>
            )}/>
    )
}

export default FormInputText