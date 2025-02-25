import { SyntheticEvent, useState } from "react";
import { Training } from "../types";
import { Accordion, AccordionDetails, AccordionSummary, Box, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from "@mui/material";
import { format } from "date-fns";
import { LuPencilLine, LuTrash2 } from "react-icons/lu";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const TrainingList = (props: { 
    trainings: Array<Training>,
    onEdit: (date: Date) => void,
    onDelete: (date: Date) => void 
}) => {
    const { trainings, onEdit, onDelete } = props;
    const [expanded, setExpanded] = useState<number | false>(false);

    const handleExpansion = (panel: number) => (_: SyntheticEvent, isExpanded: boolean) => {
        setExpanded(isExpanded ? panel : false);
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
                                    onClick={() => onEdit(training.date)}>
                                    <LuPencilLine/>
                                </IconButton>
                            </Tooltip>
                            <Tooltip title="Delete" arrow>
                                <IconButton 
                                    sx={{ border: '1px solid gray', borderRadius: '4px' }}
                                    color="error"
                                    onClick={() => onDelete(training.date)}>
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