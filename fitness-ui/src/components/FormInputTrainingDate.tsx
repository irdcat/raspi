import { Typography } from "@mui/material";
import { format, parseISO } from "date-fns";
import { Controller, useFormContext } from "react-hook-form";

const FormInputTrainingDate = (props: {
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
                    <input type="hidden" {...register(name)}/>
                    <Typography sx={{ fontSize: '24px' }}>
                        { value === undefined ? 
                            "01.01.1970" : 
                            format(parseISO(value), "dd.MM.yyy")}
                    </Typography>
                </>
            )}/>
    )
}

export default FormInputTrainingDate;