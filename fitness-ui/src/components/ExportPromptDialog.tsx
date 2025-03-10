import { DialogProps } from "@toolpad/core"
import { FILE_TYPES, FileType } from "../types"
import { FormProvider, useForm } from "react-hook-form"
import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material"
import FormInputDropdown from "./FormInputDropdown"

type ExportPromptDialogParams = {
    title: string
}

type ExportPromptDialogResult = {
    fileType: FileType
}

const ExportPromptDialog = (
    props: DialogProps<ExportPromptDialogParams, ExportPromptDialogResult | null>
) => {

    const { open, onClose, payload } = props;
    const formMethods = useForm<ExportPromptDialogResult>({
        defaultValues: {
            fileType: "json"
        }
    });
    const { handleSubmit } = formMethods;

    const onSubmit = (result: ExportPromptDialogResult) => {
        onClose(result);
    }

    return (
        <Dialog fullWidth open={open} onClose={() => onClose(null)}>
            <DialogTitle>{payload.title}</DialogTitle>
            <DialogContent>
                <FormProvider {...formMethods}>
                    <FormInputDropdown
                        name="fileType"
                        label="Extension"
                        options={FILE_TYPES}
                        getOptionLabel={(o) => o}
                        getOptionValue={(o) => o}/>
                </FormProvider>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleSubmit(onSubmit)}>Ok</Button>
            </DialogActions>
        </Dialog>
    );
}

export default ExportPromptDialog;