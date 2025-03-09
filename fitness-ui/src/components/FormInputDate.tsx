import { DatePicker } from "@mui/x-date-pickers";
import { Controller, useFormContext } from "react-hook-form";

const FormInputDate = (props: {
    name: string,
    label?: string
}) => {
    const { name, label } = props;
    const { control } = useFormContext();

    return (
        <Controller
            name={name}
            control={control}
            render={({
                field: { onChange, value }
            }) => (
                <DatePicker 
                    label={label} 
                    value={value} 
                    onChange={onChange} 
                    slotProps={{ textField: { size: "small", fullWidth: true, margin: "dense" }}}/>
            )}/>
    )
}

export default FormInputDate;