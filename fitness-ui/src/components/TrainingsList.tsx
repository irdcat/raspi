import { SyntheticEvent, useState } from "react";
import { Training } from "../types";
import { Accordion, AccordionDetails, AccordionSummary, Box, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from "@mui/material";
import { format } from "date-fns";
import { LuPencilLine, LuTrash2 } from "react-icons/lu";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { useNavigate } from "react-router-dom";
import { useDialogs } from "@toolpad/core";

const TrainingList = (props: { trainings: Array<Training> }) => {
    const { trainings } = props;
    const [expanded, setExpanded] = useState<number | false>(false);
    const navigate = useNavigate();
    const dialogs = useDialogs();

    const handleExpansion = (panel: number) => (event: SyntheticEvent, isExpanded: boolean) => {
        setExpanded(isExpanded ? panel : false);
    }

    const handleTrainingEdit = (date: Date) => {
        const dateString = format(date, "yyyy-MM-dd");
        navigate(`/trainings/${dateString}`);
    }

    const handleTrainingDelete = async (date: Date) => {
        const dateString = format(date, "dd.MM.yyyy");
        const result = await dialogs.confirm(`Are you sure you want to delete training from ${dateString}`);
        if (!result) {
            return;
        }
        // TODO: Delete training
    }

    return (
        <Box>
            {trainings.map((training, index) => (
                <Accordion expanded={expanded === index} onChange={handleExpansion(index)} key={index}>
                    <AccordionSummary expandIcon={<ExpandMoreIcon/>} id={`content-${index}`}>
                        <Typography component="span" sx={{ flexShrink: 0, width: '33%' }}>
                            {format(training.date, "dd.MM.yyyy")}
                        </Typography>
                        <Typography component="span" sx={{ color: 'text.secondary' }}>
                            {`${training.bodyweight.toString()} kg`}
                        </Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <Box sx={{ display: 'flex', columnGap: '4px' }}>
                            <Tooltip title="Edit" arrow>
                                <IconButton 
                                    sx={{ border: '1px solid gray', borderRadius: '4px' }}
                                    color="secondary"
                                    onClick={() => handleTrainingEdit(training.date)}>
                                    <LuPencilLine/>
                                </IconButton>
                            </Tooltip>
                            <Tooltip title="Delete" arrow>
                                <IconButton 
                                    sx={{ border: '1px solid gray', borderRadius: '4px' }}
                                    color="error"
                                    onClick={() => handleTrainingDelete(training.date)}>
                                    <LuTrash2/>
                                </IconButton>
                            </Tooltip>
                        </Box>
                        <TableContainer component={Paper}>
                            <Table sx={{ tableLayout: 'fixed' }}>
                                <TableHead>
                                    <TableRow>
                                        <TableCell width='170px'>
                                            <Typography variant="body2" sx={{ fontWeight: "bold" }}>Exercise</Typography>
                                        </TableCell>
                                        <TableCell>
                                            <Typography variant="body2" sx={{ fontWeight: "bold" }}>Sets</Typography>
                                        </TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {training.exercises.map((trainingExercise, exerciseIndex) => (
                                        <TableRow key={`row-${index}-${exerciseIndex}`}>
                                            <TableCell>{trainingExercise.exercise.name}</TableCell>
                                            <TableCell>{trainingExercise.sets.map(s => `${s.repetitions}x${s.weight}`).join(" ")}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </AccordionDetails>
                </Accordion>
            ))}
        </Box>
    )
}

export default TrainingList;