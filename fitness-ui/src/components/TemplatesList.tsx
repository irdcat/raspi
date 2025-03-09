import { SyntheticEvent, useState } from "react";
import { TrainingTemplate } from "../types"
import { Accordion, AccordionDetails, AccordionSummary, Box, IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip, Typography } from "@mui/material";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExerciseWithIcon from "./ExerciseWithIcon";
import { LuPencilLine, LuTrash2 } from "react-icons/lu";

const TemplatesList = (props: {
    templates: Array<TrainingTemplate>,
    onEdit: (id: string) => void,
    onDelete: (id: string) => void
}) => {
    const { templates, onEdit, onDelete } = props;
    const [expanded, setExpanded] = useState<string | false>(false);

    const handleExpansion = (panel: string) => (_: SyntheticEvent, isExpanded: boolean) => {
        setExpanded(isExpanded ? panel : false)
    }

    return (
        <Box>
            {templates.map(template => (
                <Accordion expanded={expanded === template.id} key={template.id} onChange={handleExpansion(template.id)}>
                    <AccordionSummary expandIcon={<ExpandMoreIcon/>} id={`content-${template.id}`}>
                        <Box sx={{ display: 'flex', width: '100%', columnGap: '4px' }}>
                            <Box>
                                <Typography fontWeight={500}>
                                    {template.group}
                                </Typography>
                            </Box>
                            <Box>
                                <Typography fontWeight={700}>
                                    {"-"}
                                </Typography>
                            </Box>
                            <Box>
                                <Typography fontWeight={500}>
                                    {template.name}
                                </Typography>
                            </Box>
                        </Box>
                    </AccordionSummary>
                    <AccordionDetails>
                        <Box sx={{ paddingY: '5px' }}>
                            <Typography>
                                {template.description}
                            </Typography>
                        </Box>
                        <Box sx={{ display: 'flex', columnGap: '4px' }}>
                            <Tooltip title="Edit" arrow>
                                <IconButton 
                                    sx={{ border: '1px solid gray', borderRadius: '4px' }}
                                    color="secondary"
                                    onClick={() => onEdit(template.id)}>
                                    <LuPencilLine/>
                                </IconButton>
                            </Tooltip>
                            <Tooltip title="Delete" arrow>
                                <IconButton 
                                    sx={{ border: '1px solid gray', borderRadius: '4px' }}
                                    color="error"
                                    onClick={() => onDelete(template.id)}>
                                    <LuTrash2/>
                                </IconButton>
                            </Tooltip>
                        </Box>
                        <TableContainer>
                            <Table sx={{ tableLayout: 'fixed' }}>
                                <TableHead>
                                    <TableRow>
                                        <TableCell width='170px'>
                                            <Typography variant="body2" fontWeight="bold">
                                                Exercise
                                            </Typography>
                                        </TableCell>
                                        <TableCell>
                                            <Typography variant="body2" fontWeight="bold">
                                                Sets
                                            </Typography>
                                        </TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {template.exercises.map((templateExercise, index) => (
                                        <TableRow key={`row-${index}`}>
                                            <TableCell>
                                                <ExerciseWithIcon exercise={templateExercise.exercise}/>
                                            </TableCell>
                                            <TableCell>
                                                <Typography>
                                                    {templateExercise.setCount}
                                                </Typography>
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

export default TemplatesList;