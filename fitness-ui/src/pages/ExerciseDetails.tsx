import { Box, Paper, Typography } from '@mui/material';
import { useState } from 'react';
import { useParams } from 'react-router-dom';
import ExercisesApi from '../api/ExercisesApi';
import useWindowDimensions from '../hooks/useWindowDimensions';
import TrainingsApi from '../api/TrainingsApi';
import ExerciseChart from '../components/ExerciseChart';
import { useAsyncEffect } from '../hooks/useAsyncEffect';
import Exercise from '../model/Exercise';
import ExerciseSummary from '../model/ExerciseSummary';
import { subDays } from 'date-fns';

type ExerciseDetailsData = {
  exercise: Exercise;
  summary: ExerciseSummary;
};

const ExerciseDetails = () => {
  const { id } = useParams();
  const { height } = useWindowDimensions();
  const [data, setData] = useState<ExerciseDetailsData>({
    exercise: { id: '', name: '', isBodyWeight: false },
    summary: { id: '', parameters: new Map() },
  });

  useAsyncEffect(async () => {
    if (id === undefined) {
      return;
    }
    const to = new Date();
    const from = subDays(to, 90);

    const e = await ExercisesApi.getById(id);
    const s = await TrainingsApi.getSummary({
      exerciseIds: [id],
      from: from,
      to: to,
    }).then((s) => {
      if (s.length === 0) {
        return { id: '', parameters: new Map() };
      } else {
        return s[0];
      }
    });
    setData({ exercise: e, summary: s });
  }, [id]);

  return (
    <Box sx={{ px: 3 }}>
      <Box sx={{ display: 'flex', paddingY: 2, paddingX: 1 }}>
        <Typography variant="h6" color="white" sx={{ flexGrow: 1 }}>
          {data.exercise.name}
          {data.summary.parameters.size === 0 ? ' (No data available)' : ''}
        </Typography>
      </Box>
      <Paper sx={{ height: height - 160 }}>
        <ExerciseChart parameters={data.summary.parameters} height="100%" />
      </Paper>
    </Box>
  );
};

export default ExerciseDetails;
