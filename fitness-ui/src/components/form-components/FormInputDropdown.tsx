import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import { Controller } from "react-hook-form";

type FormInputDropdownProps = {
    name: string,
    control: any,
    register: any,
    label: string,
    options: Array<any>,
    getOptionLabel: (option: any) => any,
    getOptionValue: (option: any) => any,
    setValue?: any
};

const FormInputDropdown = (props: FormInputDropdownProps) => {
    const optionsToItems = (options: Array<any>) => {
        return options.map(opt => {
            let label = props.getOptionLabel(opt);
            let value = props.getOptionValue(opt);
            return (
                <MenuItem sx={{ width: "100%" }} key={value} value={value}>
                    {label}
                </MenuItem>
            );
        });
    }
    return (
        <FormControl size={"small"}>
            <InputLabel>{props.label}</InputLabel>
            <Controller
                name={props.name}
                control={props.control}
                render={({field: { onChange, value } }) => (
                    <Select autoWidth onChange={onChange} value={value}>
                        {optionsToItems(props.options)}
                    </Select>
                )}/>
        </FormControl>
    )
}

export default FormInputDropdown;