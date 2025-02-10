import { Autocomplete, TextField } from '@mui/material';
import { Controller } from 'react-hook-form';

type FormInputAutocompleteProps = {
  name: string;
  control: any;
  register: any;
  label: string;
  options: Array<any>;
  getOptionLabel: (option: any) => any;
  getOptionValue: (option: any) => any;
  setValue: any;
  disabled?: boolean;
};

const FormInputAutocomplete = (props: FormInputAutocompleteProps) => {
  return (
    <Controller
      name={props.name}
      control={props.control}
      render={({ field: { onChange, value }, fieldState: { error } }) => (
        <Autocomplete
          options={props.options}
          getOptionLabel={props.getOptionLabel}
          isOptionEqualToValue={(option, value) => props.getOptionValue(option) === value}
          onChange={(e, data) => {
            onChange(data.id);
          }}
          value={value}
          disabled={props.disabled}
          renderInput={(params) => (
            <TextField
              {...params}
              label={props.label}
              variant="outlined"
              error={!!error}
              helperText={error ? error.message : null}
              disabled={props.disabled}
            />
          )}
        />
      )}
    />
  );
};

export default FormInputAutocomplete;
