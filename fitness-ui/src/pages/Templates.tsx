import { Backdrop, Box, CircularProgress, Paper } from "@mui/material"
import { useCallback, useEffect, useState } from "react";
import { TrainingTemplate } from "../types";
import TemplatesList from "../components/TemplatesList";
import { deleteTemplate, exportTemplates, fetchTemplates, importTemplates, isTemplateArray } from "../api/templateApi";
import { LuDownload, LuPlus, LuUpload } from "react-icons/lu";
import { useNavigate } from "react-router-dom";
import { useDialogs } from "@toolpad/core";
import TooltipedIconButton from "../components/TooltipedIconButton";
import ExportPromptDialog from "../components/ExportPromptDialog";
import ImportPromptDialog from "../components/ImportPromptDialog";

const Templates = () => {
    const navigate = useNavigate();
    const dialogs = useDialogs();
    const [loading, setLoading] = useState(true);
    const [templates, setTemplates] = useState<Array<TrainingTemplate>>([]);

    const fetchData = useCallback(async () => {
        setLoading(true);
        const result = await fetchTemplates();
        if (isTemplateArray(result)) {
            setTemplates(result);
        }
        setLoading(false);
    }, []);

    useEffect(() => { 
        fetchData();
    }, [fetchData]);

    const handleTemplateAdd = () => {
        navigate("/templates/new");
    }

    const handleTemplateEdit = (id: string) => {
        navigate(`/templates/${id}`);
    }

    const handleTemplateDelete = async (id: string) => {
        const result = await dialogs.confirm(`Are you sure you want to delete template?`);
        if(!result) {
            return;
        }
        await deleteTemplate(id);
        fetchData();
    }

    const handleTemplateImport = async () => {
        const promptResult = await dialogs.open(ImportPromptDialog, { title: "Inport Templates", accept: ".json,.yaml" });
        if (promptResult === null) {
            return;
        }
        await importTemplates(promptResult.file);
        fetchData();
    }

    const handleTemplateExport = async () => {
        const promptResult = await dialogs.open(ExportPromptDialog, { title: "Export Templates" });
        if (promptResult === null) {
            return;
        }
        const blob = await exportTemplates(promptResult.fileType);
        if (!("requestId" in blob)) {
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = `templates.${promptResult.fileType.toString()}`
            link.click();
            link.remove();
        }
    }

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '5px' }}>
                <Box component={Paper} sx={{ height: '64px', paddingY: '12px', paddingX: '4px', display: 'flex', justifyContent: 'flex-end', columnGap: '2px' }}>
                    <Box sx={{ display: 'flex', columnGap: '4px' }}>
                        <TooltipedIconButton onClick={handleTemplateImport} tooltipTitle="Import">
                            <LuUpload/>
                        </TooltipedIconButton>
                        <TooltipedIconButton onClick={handleTemplateExport} tooltipTitle="Export">
                            <LuDownload/>
                        </TooltipedIconButton>
                        <TooltipedIconButton tooltipTitle="Add" color="success" onClick={handleTemplateAdd}>
                            <LuPlus/>
                        </TooltipedIconButton>
                    </Box>
                </Box>
                <Box sx={{ height: 'calc(100% - 128px)', padding: '6px', overflowY: 'auto' }}>
                    <TemplatesList
                        onEdit={handleTemplateEdit}
                        onDelete={handleTemplateDelete} 
                        templates={templates}/>
                </Box>
            </Box>
            <Backdrop 
                sx={(theme) => ({ color: '#fff', zIndex: theme.zIndex.drawer })}
                open={loading}>
                <CircularProgress color="inherit"/>
            </Backdrop>
        </>
    )
}

export default Templates;