import { useForm } from 'react-hook-form';
import TrainingFormData from '../../model/TrainingFormData';
import { Button, Paper, Typography } from '@mui/material';
import FormInputDate from '../form-components/FormInputDate';
import FormInputText from '../form-components/FormInputText';
import Exercise from '../../model/Exercise';
import { useEffect, useState } from 'react';
import { useAsyncEffect } from '../../hooks/useAsyncEffect';
import ExercisesApi from '../../api/ExercisesApi';
import FormInputTrainingExercises from '../form-components/FormInputTrainingExercises';
import TrainingExerciseFormData from '../../model/TrainingExerciseFormData';
import TrainingTemplate from '../../model/TrainingTemplate';
import TemplatesApi from '../../api/TemplatesApi';
import FormInputDropdown from '../form-components/FormInputDropdown';

type TrainingFormProps = {
  onSubmit: (data: TrainingFormData) => void;
  initialValues?: TrainingFormData;
};

const TrainingForm = (props: TrainingFormProps) => {
  const [exerciseList, setExerciseList] = useState<Array<Exercise>>([]);
  const [templateList, setTemplateList] = useState<Array<TrainingTemplate>>([]);

  useAsyncEffect(async () => {
    setExerciseList(await ExercisesApi.get());
    let templates = await TemplatesApi.get();
    let emptyTemplate: TrainingTemplate = {
      id: '',
      name: 'None',
      groupName: '',
      description: '',
      exerciseIds: [],
    };
    setTemplateList([emptyTemplate, ...templates]);
  }, []);

  const defaultValues = {
    templateId: '',
    date: new Date(),
    bodyWeight: 0,
    exercises: Array<TrainingExerciseFormData>(),
  };

  const { register, handleSubmit, control, watch, setValue } = useForm<TrainingFormData>({
    defaultValues: props.initialValues ? props.initialValues : defaultValues,
  });
  let [exercisesDisabled, setExercisesDisabled] = useState(false);

  useEffect(() => {
    const { unsubscribe } = watch((value, { name }) => {
      if (name === 'templateId') {
        if (value.templateId === '' || value.templateId === null) {
          setValue('exercises', []);
          setExercisesDisabled(false);
        } else {
          setValue(
            'exercises',
            templateList.find((t) => t.id === value.templateId)!!.exerciseIds.map((e) => ({ exerciseId: e, sets: [] }))
          );
          setExercisesDisabled(true);
        }
      }
    });
    return () => unsubscribe();
  }, [watch, setValue, templateList]);

  return (
    <Paper
      style={{
        display: 'grid',
        gridRowGap: '20px',
        padding: '20px',
        backgroundColor: 'transparent',
      }}
    >
      <FormInputDropdown
        name="templateId"
        control={control}
        label="Template"
        register={register('templateId', { required: false })}
        options={templateList}
        getOptionLabel={(opt) => {
          if (opt.groupName === '') {
            return <Typography sx={{ fontWeight: 'regular' }}>{opt.name}</Typography>;
          } else {
            return (
              <>
                <Typography sx={{ display: 'inline', fontWeight: 'bold' }}>{opt.groupName}</Typography>
                <Typography sx={{ display: 'inline', paddingX: 1 }}>-</Typography>
                <Typography sx={{ display: 'inline', fontWeight: 'regular' }}>{opt.name}</Typography>
              </>
            );
          }
        }}
        getOptionValue={(opt) => opt.id}
      />
      <FormInputDate
        register={register('date', {
          required: { value: true, message: 'Field is required' },
        })}
        name="date"
        control={control}
        label="Date"
      />
      <FormInputText
        register={register('bodyWeight', {
          required: { value: true, message: 'Field is required' },
          validate: {
            positive: (v) => v > 0 || 'Value must be positive real number',
          },
        })}
        name="bodyWeight"
        control={control}
        label="Body Weight (kg)"
      />
      <FormInputTrainingExercises
        name={'exercises'}
        control={control}
        registerFunction={register}
        label={'Exercises'}
        options={exerciseList}
        exercisesDisabled={exercisesDisabled}
        setValue={setValue}
      />
      <Button onClick={handleSubmit(props.onSubmit)} variant="outlined">
        Submit
      </Button>
    </Paper>
  );
};

export default TrainingForm;
