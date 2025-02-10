import Exercise from './Exercise';

type TrainingTemplate = {
  id: string;
  name: string;
  groupName: string;
  description: string;
  exerciseIds: Array<string>;
};

export default TrainingTemplate;
