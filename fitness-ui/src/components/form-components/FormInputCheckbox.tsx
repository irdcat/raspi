import { Checkbox, FormControlLabel } from '@mui/material';
import { Controller } from 'react-hook-form';

type FormInputCheckboxProps = {
  name: string;
  control: any;
  label: string;
  setValue?: any;
};

const FormInputCheckbox = (props: FormInputCheckboxProps) => {
  return (
    <Controller
      name={props.name}
      control={props.control}
      render={({ field: { onChange, value } }) => (
        <FormControlLabel control={<Checkbox checked={value} onChange={onChange} />} label={props.label} />
      )}
    />
  );
};

export default FormInputCheckbox;
