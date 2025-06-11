import { Backdrop, Box, CircularProgress, Pagination, Paper } from "@mui/material";
import { format, subDays } from "date-fns";
import { DatePicker, DateValidationError, PickerChangeHandlerContext } from "@mui/x-date-pickers";
import { LuDownload, LuPlus, LuUpload } from "react-icons/lu";
import ResponsiveFilterBar from "../components/ResponsiveFilterBar";
import TrainingList from "../components/TrainingsList";
import { useCallback, useEffect, useState } from "react";
import { Training } from "../types";
import { deleteTraining, exportTrainings, fetchTraining, fetchTrainings, importTrainings, isTraining, isTrainingPage, trainingExists } from "../api/trainingApi";
import { useDialogs } from "@toolpad/core";
import { useNavigate } from "react-router-dom";
import TrainingCreationDialog from "../components/TrainingCreationDialog";
import ExportPromptDialog from "../components/ExportPromptDialog";
import TooltipedIconButton from "../components/TooltipedIconButton";
import ImportPromptDialog from "../components/ImportPromptDialog";

type Filters = {
    from: Date,
    to: Date,
    page: number
}

const Trainings = () => {
    const pageSize = 25;
    const [trainings, setTrainings] = useState<Array<Training>>([]);
    const [pageCount, setPageCount] = useState(1);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState<Filters>({
        from: subDays(new Date(), 180),
        to: new Date(),
        page: 1
    });
    const dialogs = useDialogs();
    const navigate = useNavigate();

    const fetchData = useCallback(async () => {
        setLoading(true);
        const result = await fetchTrainings(filters.from, filters.to, filters.page, pageSize);
        if (isTrainingPage(result)) {
            setTrainings(result.content);
            setPageCount(Math.ceil(result.totalResults / pageSize));
        }
        setLoading(false);
    }, [filters]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const handlePageChange = (e: React.ChangeEvent<unknown>, page: number) => {
        setFilters(prevFilters => ({
            ...prevFilters,
            page: page
        }));
    }

    const handleDateChange = (fieldName: keyof Omit<Filters, "page">) => 
        (value: Date | null, _: PickerChangeHandlerContext<DateValidationError>) => {
            if (value == null) {
                return;
            }
            setFilters(prevFilter => ({
                ...prevFilter,
                [fieldName]: value
            }));
        }

    const handleTrainingAdd = async () => {
        const promptResult = await dialogs.open(TrainingCreationDialog);
        if (promptResult === null) {
            return;
        }
        const exists = await trainingExists(promptResult.date);
        if (exists) {
            await dialogs.alert("Training with the given date exists! Edit appropriate training instead.");
        } else {
            const formattedDate = format(promptResult.date, "yyyy-MM-dd");
            let training: Training = {
                date: promptResult.date,
                bodyweight: 0,
                exercises: promptResult.template.exercises.map((e, i) => ({
                    id: "",
                    order: i,
                    exercise: e.exercise,
                    sets: new Array(e.setCount).fill({ repetitions: 0, weight: 0 })
                }))
            };
            navigate(`/trainings/${formattedDate}`, { state: { training: training } });
        }
    }

    const handleTrainingEdit = async (date: Date) => {
        const formattedDate = format(date, "yyyy-MM-dd");
        const training = await fetchTraining(date);
        if (isTraining(training)) {
            navigate(`/trainings/${formattedDate}`, { state: { training: training } });
        }
    }

    const handleTrainingDelete = async (date: Date) => {
        const dateString = format(date, "dd.MM.yyyy");
        const result = await dialogs.confirm(`Are you sure you want to delete training from ${dateString}`);
        if (!result) {
            return;
        }
        await deleteTraining(date);
        fetchData();
    }

    const handleTrainingImport = async () => {
        const promptResult = await dialogs.open(ImportPromptDialog, { title: "Import Trainings", accept: ".json,.yaml" });
        if (promptResult === null) {
            return;
        }
        await importTrainings(promptResult.file);
        fetchData();
    }

    const handleTrainingExport = async () => {
        const promptResult = await dialogs.open(ExportPromptDialog, { title: "Export Trainings" });
        if (promptResult === null) {
            return;
        }
        const blob = await exportTrainings(promptResult.fileType);
        if (!("requestId" in blob)) {
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = `trainings.${promptResult.fileType.toString()}`
            link.click();
            link.remove();
        }
    }

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '5px' }}>
                <Box component={Paper} sx={{ height: '64px', paddingY: '12px', paddingX: '4px', display: 'flex', columnGap: '2px' }}>
                    <ResponsiveFilterBar>
                        <DatePicker
                            slotProps={{ textField: { size: "small" } }}
                            label="From"
                            name="from"
                            value={filters.from}
                            onChange={handleDateChange("from")}/>
                        <DatePicker
                            slotProps={{ textField: { size: "small" } }}
                            label="To"
                            name="to"
                            value={filters.to}
                            onChange={handleDateChange("to")}/> 
                    </ResponsiveFilterBar>
                    <Box sx={{ display: 'flex', columnGap: '4px' }}>
                        <TooltipedIconButton onClick={handleTrainingImport} tooltipTitle="Import">
                            <LuUpload/>
                        </TooltipedIconButton>
                        <TooltipedIconButton onClick={handleTrainingExport} tooltipTitle="Export">
                            <LuDownload/>
                        </TooltipedIconButton>
                        <TooltipedIconButton color="success" onClick={handleTrainingAdd} tooltipTitle="Add">
                            <LuPlus/>
                        </TooltipedIconButton>
                    </Box>
                </Box>
                <Box sx={{ height: 'calc(100% - 192px)', padding: '6px', overflowY: 'auto' }}>
                    <TrainingList 
                        onEdit={handleTrainingEdit} 
                        onDelete={handleTrainingDelete} 
                        trainings={trainings}/>
                </Box>
                <Box component={Paper} sx={{ paddingY: '16px', paddingX: '16px', height: '64px' }}>
                    <Pagination 
                        size="medium" 
                        count={pageCount} 
                        variant="outlined" 
                        shape="rounded"
                        page={filters.page}
                        onChange={handlePageChange}/>
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

export default Trainings;