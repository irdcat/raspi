import { Box, Paper, Tab, Tabs, Typography } from '@mui/material';
import TrainingTemplate from '../model/TrainingTemplate';
import ExerciseSummary from '../model/ExerciseSummary';
import { useParams } from 'react-router-dom';
import useWindowDimensions from '../hooks/useWindowDimensions';
import { useState } from 'react';
import { useAsyncEffect } from '../hooks/useAsyncEffect';
import TemplatesApi from '../api/TemplatesApi';
import { subDays } from 'date-fns';
import TrainingsApi from '../api/TrainingsApi';
import ExerciseChart from '../components/ExerciseChart';
import Exercise from '../model/Exercise';
import ExercisesApi from '../api/ExercisesApi';

type TabPanelProps = {
  children?: React.ReactNode;
  index: number;
  value: number;
};

const CustomTabPanel = (props: TabPanelProps) => {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      style={{ height: 'calc(100% - 24px)' }}
      {...other}
    >
      {value === index && <Box sx={{ height: '100%', width: '100%' }}>{children}</Box>}
    </div>
  );
};

type TemplateDetailsData = {
  template: TrainingTemplate;
  exercises: Array<Exercise>;
  exerciseSummaries: Array<ExerciseSummary>;
};

const TemplateDetails = () => {
  const { id } = useParams();
  const { height } = useWindowDimensions();
  const [data, setData] = useState<TemplateDetailsData>({
    template: {
      id: '',
      name: '',
      groupName: '',
      description: '',
      exerciseIds: [],
    },
    exercises: [],
    exerciseSummaries: [],
  });
  const [tab, setTab] = useState(0);

  useAsyncEffect(async () => {
    if (id === undefined) {
      return;
    }

    const template = await TemplatesApi.getById(id);

    const to = new Date();
    const from = subDays(to, 90);

    const summaries = await TrainingsApi.getSummary({
      exerciseIds: template.exerciseIds,
      from: from,
      to: to,
    });

    const exercises = await ExercisesApi.getByIds(template.exerciseIds);

    setData({
      template: template,
      exercises: exercises,
      exerciseSummaries: summaries,
    });
  });

  const onTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTab(newValue);
  };

  return (
    <Box sx={{ px: 3 }}>
      <Box sx={{ display: 'flex', paddingY: 2, paddingX: 1 }}>
        <Typography variant="h6" color="white" sx={{ flexGrow: 1 }}>
          {data.template.name}
        </Typography>
      </Box>
      <Paper sx={{ height: height - 160 }}>
        <Tabs value={tab} onChange={onTabChange}>
          {data.exerciseSummaries.map((summary) => (
            <Tab
              sx={{ flexGrow: 1 }}
              label={data.exercises.filter((e) => e.id === summary.id)[0].name}
              id={summary.id}
            />
          ))}
        </Tabs>
        {data.exerciseSummaries.map((summary, idx) => (
          <CustomTabPanel key={idx} value={tab} index={idx}>
            <ExerciseChart height="100%" width="100%" parameters={summary.parameters} />
          </CustomTabPanel>
        ))}
      </Paper>
    </Box>
  );
};

export default TemplateDetails;
