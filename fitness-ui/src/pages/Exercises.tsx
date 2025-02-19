import { Backdrop, Box, CircularProgress, Pagination, Paper, TextField } from "@mui/material";
import CountedExerciseList from "../components/CountedExerciseList";
import { useEffect, useState } from "react";
import { CountedExercise, Page } from "../types";

type Filters = {
    name: string,
    page: number
}

const Exercises = () => {
    const pageSize = 30;
    const [countedExercises, setCountedExercises] = useState<Array<CountedExercise>>([])
    const [pageCount, setPageCount] = useState(1);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState<Filters>({
        name: "",
        page: 1
    });

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            const result = (await fetch(`/api/exercises/counted?page=${filters.page-1}&pageSize=${pageSize}${filters.name.length === 0 ? '' : '&name=' + filters.name}`)
                .then(r => r.json())) as Page<CountedExercise>;
            setCountedExercises(result.content);
            setPageCount(Math.ceil(result.totalResults / pageSize));
            setLoading(false);
        };
        fetchData();
    }, [filters])

    const handlePageChange = (e: React.ChangeEvent<unknown>, page: number) => {
        setFilters(prevFilters => ({
            ...prevFilters,
            page: page
        }));
    }

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFilters(prevFilters => ({
            ...prevFilters,
            name: e.target.value
        }));
    }

    return (
        <>
            <Box sx={{ height: '100%', paddingX: '7px' }}>
                <Box component={Paper} sx={{ height: '64px', paddingY: '13px', paddingX: '6px', display: 'flex', columnGap: 1 }}>
                    <TextField
                        fullWidth
                        size="small"
                        label="Search"
                        name="search"
                        value={filters.name}
                        onChange={handleNameChange}/>
                </Box>
                <Box sx={{ padding: 1, overflowY: 'scroll', height: 'calc(100% - 192px)' }}>
                    <CountedExerciseList exercises={countedExercises}/>
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

export default Exercises;