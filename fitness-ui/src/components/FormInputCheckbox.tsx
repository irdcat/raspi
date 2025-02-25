import { Checkbox, FormControlLabel } from "@mui/material";
import { Controller, useFormContext } from "react-hook-form";

const FormInputCheckbox = (props: {
    name: string,
    label?: string
}) => {
    const { name, label } = props;
    const { control, register } = useFormContext();

    return (
        <Controller
            name={name}
            control={control}
            render={({
                field: { onChange, value }
            }) => (
                <FormControlLabel
                    label={label}
                    control={
                        <Checkbox checked={value} onChange={onChange}/>
                    }/>
            )}/>
    )
}

export default FormInputCheckbox;