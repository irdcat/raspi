type TrainingTemplateFormData = {
  name: string;
  groupName: string;
  description: string;
  exerciseIds: Array<{ id: string }>;
};

export default TrainingTemplateFormData;
