import ExerciseParameters from './ExerciseParameters';

type ExerciseSummary = {
  id: string;
  parameters: Map<Date, ExerciseParameters>;
};

export default ExerciseSummary;
