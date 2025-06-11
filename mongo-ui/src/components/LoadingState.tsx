import { Box, CircularProgress } from "@mui/material";

const LoadingState = () => {
    return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', textAlign: 'center' }}>
            <Box>
                <CircularProgress/>
            </Box>
        </Box>
    )
}

export default LoadingState;