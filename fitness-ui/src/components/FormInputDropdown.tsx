import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import { Controller, useFormContext } from "react-hook-form";

const FormInputDropdown = (props: {
    name: string,
    label?: string,
    options: Array<any>,
    getOptionValue: (o: any) => any,
    getOptionLabel: (o: any) => string,
    defaultValue?: any
}) => {
    const { name, label, options, getOptionLabel, getOptionValue, defaultValue } = props;
    const { control } = useFormContext();

    return (
        <FormControl fullWidth margin="dense" size={"small"}>
            <InputLabel>{label}</InputLabel>
            <Controller render={({ field: { onChange, value } }) => (
                <Select 
                    onChange={(e) => onChange(JSON.parse(e.target.value))}
                    defaultValue={JSON.stringify(defaultValue)}
                    value={JSON.stringify(value)} 
                    renderValue={selected => getOptionLabel(JSON.parse(selected))}>
                    {options.map((option, index) => (
                        <MenuItem key={index} value={JSON.stringify(getOptionValue(option))}>
                            {getOptionLabel(option)}
                        </MenuItem>
                    ))}
                </Select>
            )}
            control={control}
            name={name}/>
        </FormControl>
      );
}

export default FormInputDropdown;