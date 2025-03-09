import { DialogProps } from "@toolpad/core"
import { TrainingTemplate } from "../types"
import { FormProvider, useForm } from "react-hook-form"
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material"
import FormInputDate from "./FormInputDate"
import FormInputDropdown from "./FormInputDropdown"
import { useEffect, useState } from "react"
import { fetchTemplates, isTemplateArray } from "../api/templateApi"

type TrainingCreationDialogResult = {
    date: Date,
    template: TrainingTemplate
}

const templateNone: TrainingTemplate = {
    id: "-1",
    name: "None",
    group: "None",
    description: "None",
    exercises: []
}

const TrainingCreationDialog = (props: DialogProps<undefined, TrainingCreationDialogResult | null>) => {
    const { open, onClose } = props;
    const [templates, setTemplates] = useState([templateNone])
    const formMethods = useForm<TrainingCreationDialogResult>({
        defaultValues: {
            date: new Date(),
            template: templateNone
        }
    });
    const { handleSubmit } = formMethods;
    

    const onSubmit = (result: TrainingCreationDialogResult) => {
        onClose(result);
    }

    const getTemplateValue = (template: TrainingTemplate) => template;
    const getTemplateLabel = (template: TrainingTemplate) => 
        template.id !== "-1" ? `${template.group} - ${template.name}` : "None";

    useEffect(() => {
        const fetchData = async () => {
            const result = await fetchTemplates();
            if (isTemplateArray(result)) {
                setTemplates([templateNone, ...result])
            }
        };
        fetchData();
    }, []);

    return (
        <Dialog fullWidth open={open} onClose={() => onClose(null)}>
            <DialogTitle>Training</DialogTitle>
            <DialogContent>
                <FormProvider {...formMethods}>
                    <FormInputDate name="date" label="Date"/>
                    <FormInputDropdown 
                        name="template"
                        label="Template"
                        options={templates}
                        getOptionValue={getTemplateValue}
                        getOptionLabel={getTemplateLabel}
                        defaultValue={templateNone}/>
                </FormProvider>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleSubmit(onSubmit)}>Ok</Button>
            </DialogActions>
        </Dialog>
    )
}

export default TrainingCreationDialog;