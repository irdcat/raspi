import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material"
import { DialogProps } from "@toolpad/core"
import { FormProvider, useForm } from "react-hook-form"
import FormInputFile from "./FormInputFile"

type ImportPromptDialogParams = {
    title: string,
    accept?: string
}

type ImportPromptDialogResult = {
    file: File
}

const ImportPromptDialog = (
    props: DialogProps<ImportPromptDialogParams, ImportPromptDialogResult | null>
) => {

    const { open, onClose, payload } = props;
    const formMethods = useForm<ImportPromptDialogResult>();
    const { handleSubmit } = formMethods;

    const onSubmit = (result: ImportPromptDialogResult) => {
        onClose(result);
    }

    return (
        <Dialog fullWidth open={open} onClose={() => onClose(null)}>
            <DialogTitle>{payload.title}</DialogTitle>
            <DialogContent>
                <FormProvider {...formMethods}>
                    <FormInputFile 
                        name="file" 
                        label="Upload File" 
                        accept={payload.accept}/>
                </FormProvider>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleSubmit(onSubmit)}>Ok</Button>
            </DialogActions>
        </Dialog>
    )
}

export default ImportPromptDialog;