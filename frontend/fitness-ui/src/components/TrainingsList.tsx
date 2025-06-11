import { SyntheticEvent, useState } from "react";
import { Training } from "../types";
import { Accordion, AccordionDetails, AccordionSummary, Box, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { format } from "date-fns";
import { LuPencilLine, LuTrash2 } from "react-icons/lu";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExerciseWithIcon from "./ExerciseWithIcon";
import TooltipedIconButton from "./TooltipedIconButton";
import EmptyState from "./EmptyState";

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

    if (trainings.length === 0) {
        return (
            <EmptyState 
                title="No trainings found"
                message="Change the filters or hit the gym"/>
        )
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
                            <TooltipedIconButton tooltipTitle="Edit" color="secondary" onClick={() => onEdit(training.date)}>
                                <LuPencilLine/>
                            </TooltipedIconButton>    
                            <TooltipedIconButton tooltipTitle="Delete" color="error" onClick={() => onDelete(training.date)}>
                                <LuTrash2/>
                            </TooltipedIconButton>
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
                                    {training.exercises.toSorted((a, b) => a.order - b.order).map((trainingExercise, exerciseIndex) => (
                                        <TableRow key={`row-${index}-${exerciseIndex}`}>
                                            <TableCell>
                                                <ExerciseWithIcon exercise={trainingExercise.exercise}/>
                                            </TableCell>
                                            <TableCell>
                                                {trainingExercise.exercise.isBodyweight ? 
                                                    trainingExercise.sets.map(s => `${s.repetitions}x(+${s.weight})`).join(" ") :
                                                    trainingExercise.sets.map(s => `${s.repetitions}x${s.weight}`).join(" ")}
                                            </TableCell>
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