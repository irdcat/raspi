import { Box, Button, FormControl, Typography } from "@mui/material";
import { Controller, useFormContext } from "react-hook-form"

const FormInputFile = (props: {
    name: string,
    label?: string,
    accept?: string
}) => {
    const { name, label, accept } = props;
    const { control } = useFormContext();

    return (
        <Controller
            name={name}
            control={control}
            render={({
                field: { value, onChange }
            }) => (
                <FormControl>
                    <Box sx={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        columnGap: '6px', 
                        border: '1px solid rgba(255, 255, 255, 0.23)', 
                        borderRadius: '4px'
                    }}>
                        <Button variant="outlined" component="label">
                            {label ? label : "Upload file"}
                            <input 
                                type="file"
                                name={name}
                                accept={accept} 
                                hidden
                                onChange={(e) => onChange({
                                    target: {
                                        value: e.target.files!![0],
                                        name: name
                                    }
                                })}/>
                        </Button>
                        {value ? <Typography sx={{ paddingRight: '7px' }}>{value.name}</Typography> : ""}
                    </Box>
                </FormControl>
            )}/>
    )
}

export default FormInputFile;