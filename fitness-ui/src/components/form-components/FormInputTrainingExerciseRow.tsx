import { Button, Collapse, IconButton, TableCell, TableRow } from "@mui/material";
import FormInputAutocomplete from "./FormInputAutocomplete";
import Exercise from "../../model/Exercise";
import FormInputTrainingExerciseSets from "./FormInputTrainingExerciseSets";
import { useState } from "react";
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';

type FormInputTrainingExerciseRowProps = {
    control: any,
    name: string,
    options: Array<Exercise>,
    registerFunction: any,
    onDelete: () => void
};

const FormInputTrainingExerciseRow = (props: FormInputTrainingExerciseRowProps) => {
    const [ open, setOpen ] = useState(false);

    return (
        <>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
                <TableCell>
                    <IconButton size="small" onClick={() => setOpen(!open)}>
                        {open ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell>
                    <FormInputAutocomplete 
                        name={`${props.name}.exercise`}
                        control={props.control} 
                        register={props.registerFunction(`${props.name}.exercise`, {
                            required: { value: true, message: "Field is required" }
                        })} 
                        label={"Exercise"}
                        options={props.options} 
                        getOptionLabel={(e) => e.name}
                        getOptionValue={(e) => e.id}/>
                </TableCell>
                <TableCell align="right">
                    <Button variant="outlined" onClick={() => props.onDelete()}>
                        Delete
                    </Button>
                </TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={3}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <FormInputTrainingExerciseSets
                            name={`${props.name}.sets`}
                            control={props.control}
                            registerFunction={props.registerFunction}
                            label="Sets"/>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    )
}

export default FormInputTrainingExerciseRow;