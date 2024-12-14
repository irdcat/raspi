import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFnsV3";
import { Controller } from "react-hook-form";
import { DatePicker } from "@mui/x-date-pickers";
import { pl } from "date-fns/locale/pl"

type FormInputDateProps = {
    name: string,
    control: any,
    register: any,
    label: string
};

const FormInputDate = (props: FormInputDateProps) => {
    return (
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={pl}>
            <Controller
                name={props.name}
                control={props.control}
                render={({
                    field: { onChange, value }
                }) => (
                    <DatePicker
                        label={props.label}
                        value={value}
                        inputRef={props.register} 
                        onChange={onChange}/>
                )}
            />
        </LocalizationProvider>
    );
}

export default FormInputDate;