import { Box, Button, FormControl, Table, TableBody, Typography } from '@mui/material';
import { useFieldArray } from 'react-hook-form';
import FormInputTrainingExerciseSetRow from './FormInputTrainingExerciseSetRow';

type FormInputTrainingExerciseSetsProps = {
  name: string;
  control: any;
  registerFunction: any;
  label: string;
};

const FormInputTrainingExerciseSets = (props: FormInputTrainingExerciseSetsProps) => {
  const fieldArrayHook = useFieldArray({
    control: props.control,
    name: props.name,
  });

  return (
    <FormControl
      sx={{
        display: 'grid',
        gridRowGap: '10px',
        padding: '10px 10px',
        marginBottom: '10px',
        borderRadius: '4px',
        borderStyle: 'solid',
        borderWidth: '1px',
        borderColor: 'rgba(255, 255, 255, 0.23)',
      }}
    >
      <Box sx={{ display: 'flex' }}>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          {props.label}
        </Typography>
        <Button onClick={() => fieldArrayHook.append({ reps: '0', weight: '0' })} variant="outlined">
          Add Set
        </Button>
      </Box>
      <Table>
        <TableBody>
          {fieldArrayHook.fields.map((field, index) => (
            <FormInputTrainingExerciseSetRow
              key={field.id}
              control={props.control}
              name={`${props.name}.${index}`}
              registerFunction={props.registerFunction}
              onDelete={() => fieldArrayHook.remove(index)}
            />
          ))}
        </TableBody>
      </Table>
    </FormControl>
  );
};

export default FormInputTrainingExerciseSets;
