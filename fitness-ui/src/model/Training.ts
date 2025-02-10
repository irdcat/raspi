import TrainingExercise from './TrainingExercise';

type Training = {
  id: string;
  templateId: string;
  date: Date;
  bodyWeight: number;
  exercises: Array<TrainingExercise>;
};

export default Training;
