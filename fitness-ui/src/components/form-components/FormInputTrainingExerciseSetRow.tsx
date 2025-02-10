import { Button, TableCell, TableRow } from '@mui/material';
import FormInputText from './FormInputText';

type FormInputTrainingExerciseSetRowProps = {
  control: any;
  name: string;
  registerFunction: any;
  onDelete: () => void;
};

const FormInputTrainingExerciseSetRow = (props: FormInputTrainingExerciseSetRowProps) => {
  return (
    <TableRow>
      <TableCell>
        <FormInputText
          name={`${props.name}.reps`}
          control={props.control}
          register={props.registerFunction(`${props.name}.reps`, {
            required: { value: true, message: 'Field is required' },
            validate: {
              positive: (v: number) => parseInt(v.toString()) >= 0 || 'Value must be non-negative integer',
            },
          })}
          label="Repetitions"
        />
      </TableCell>
      <TableCell>
        <FormInputText
          name={`${props.name}.weight`}
          control={props.control}
          register={props.registerFunction(`${props.name}.weight`, {
            required: { value: true, message: 'Field is required' },
            validate: {
              positive: (v: number) => parseFloat(v.toString()) >= 0 || 'Value must be non-negative real number',
            },
          })}
          label="Weight"
        />
      </TableCell>
      <TableCell align="right">
        <Button variant="outlined" onClick={() => props.onDelete()}>
          Delete
        </Button>
      </TableCell>
    </TableRow>
  );
};

export default FormInputTrainingExerciseSetRow;
