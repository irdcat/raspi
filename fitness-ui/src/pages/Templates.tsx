import { Backdrop, Box, CircularProgress, Paper } from "@mui/material"
import { useCallback, useEffect, useState } from "react";
import { TrainingTemplate } from "../types";
import TemplatesList from "../components/TemplatesList";
import { deleteTemplate, fetchTemplates, isTemplateArray } from "../api/templateApi";
import { LuDownload, LuPlus, LuUpload } from "react-icons/lu";
import { useNavigate } from "react-router-dom";
import { useDialogs } from "@toolpad/core";
import TooltipedIconButton from "../components/TooltipedIconButton";

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

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '5px' }}>
                <Box component={Paper} sx={{ height: '64px', paddingY: '12px', paddingX: '4px', display: 'flex', justifyContent: 'flex-end', columnGap: '2px' }}>
                    <Box sx={{ display: 'flex', columnGap: '4px' }}>
                        <TooltipedIconButton tooltipTitle="Import">
                            <LuUpload/>
                        </TooltipedIconButton>
                        <TooltipedIconButton tooltipTitle="Export">
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