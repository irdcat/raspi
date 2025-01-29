import { useState } from "react";
import TrainingTemplate from "../model/TrainingTemplate"
import { useAsyncEffect } from "../hooks/useAsyncEffect";
import TemplatesApi from "../api/TemplatesApi";
import useWindowDimensions from "../hooks/useWindowDimensions";
import { useNavigate } from "react-router-dom";
import { Box, Button, ButtonGroup, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { ButtonActivatedDialog } from "./dialogs/ButtonActivatedDialog";
import TemplateForm from "./forms/TemplateForm";
import TrainingTemplateFormData from "../model/TrainingTemplateFormData";
import { ButtonActivatedActionDialog } from "./dialogs/ButtonActivatedActionDialog";

export const Templates = () => {
    const [ templateList, setTemplateList ] = useState(new Array<TrainingTemplate>());
    const { height } = useWindowDimensions();
    const navigate = useNavigate();

    useAsyncEffect(async () => {
        await TemplatesApi.get()
            .then(templates => setTemplateList(templates));
    }, []);

    const onClickDetails = (id: string) => {
        navigate(`/templates/${id}`);
    } 

    const onAddTemplate = (template: TrainingTemplate) => {
        TemplatesApi
            .add(template)
            .then(added => [...templateList, added])
            .then(templates => setTemplateList(templates))
    }

    const onEditTemplate = (template: TrainingTemplate) => {
        TemplatesApi
            .update(template.id, template)
            .then(updated => {
                const newList = [...templateList];
                newList[templateList.findIndex(t => t.id === updated.id)] = updated;
                setTemplateList(newList);
            })
    }

    const onDeleteTemplate = (id: string) => {
        TemplatesApi
            .delete(id)
            .then(deleted => setTemplateList(templateList.filter(t => t.id !== deleted.id)));
    }

    return (
        <Box sx={{ px: 3 }}>
           <Box sx={{ display: "flex", paddingY: 2, paddingX: 1 }}>
                <Typography variant="h6" color="white" sx={{ flexGrow: 1}}>
                    Templates
                </Typography>
                <ButtonActivatedDialog title="Add Template" buttonColor="primary" buttonLabel="Add" buttonVariant="outlined">
                    {(close) => 
                    <TemplateForm
                        onSubmit={(formData: TrainingTemplateFormData) => {
                            onAddTemplate({
                                id: "",
                                name: formData.name,
                                groupName: formData.groupName,
                                description: formData.description,
                                exercises: formData.exercises
                            });
                            close();
                        }}/>
                    }
                </ButtonActivatedDialog>
            </Box>
            <TableContainer sx={{ maxHeight: height - 160 }} component={Paper}>
                <Table stickyHeader>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Name
                                </Typography>
                            </TableCell>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Group Name
                                </Typography>
                            </TableCell>
                            <TableCell>
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Description
                                </Typography>
                            </TableCell>
                            <TableCell align="right">
                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                    Actions
                                </Typography>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        { templateList.map((template) => (
                            <TableRow key={template.id} sx={{ '&:lastChild td, &:last-child th': { border: 0 } }}>
                                <TableCell>
                                    { template.name }
                                </TableCell>
                                <TableCell>
                                    { template.groupName }
                                </TableCell>
                                <TableCell>
                                    { template.description }
                                </TableCell>
                                <TableCell align="right">
                                    <ButtonGroup variant="outlined">
                                        <Button onClick={() => onClickDetails(template.id)} color="success">Details</Button>
                                        <ButtonActivatedDialog title="Edit Template" buttonColor="secondary" buttonVariant="outlined" buttonLabel="Edit">
                                            {(close) =>
                                            <TemplateForm
                                                onSubmit={(formData: TrainingTemplateFormData) => {
                                                    onEditTemplate({
                                                        id: template.id,
                                                        name: formData.name,
                                                        groupName: formData.groupName,
                                                        description: formData.description,
                                                        exercises: formData.exercises
                                                    });
                                                    close();
                                                }}
                                                initialValues={{
                                                    name: template.name,
                                                    groupName: template.groupName,
                                                    description: template.description,
                                                    exercises: template.exercises
                                                }}/>
                                            }
                                        </ButtonActivatedDialog>
                                        <ButtonActivatedActionDialog
                                            title="Delete Template"
                                            text="Are you sure you want to delete template?"
                                            cancelLabel="Cancel"
                                            confirmLabel="Delete"
                                            buttonColor="error"
                                            buttonLabel="Delete"
                                            onConfirm={() => onDeleteTemplate(template.id)}
                                            />
                                    </ButtonGroup>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    )
}