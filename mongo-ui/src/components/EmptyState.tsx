import { Box, Typography } from "@mui/material";
import { LuCircleX } from "react-icons/lu";

const EmptyState = (props: {
    title: string,
    message: string
}) => {
    const { title, message } = props;
    return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', textAlign: 'center' }}>
            <Box>
                <LuCircleX size="48px"/>
                <Typography variant="h5" fontWeight="bold">
                    {title}
                </Typography>
                <Typography variant="h6">
                    {message}
                </Typography>
            </Box>
        </Box>
    )
}

export default EmptyState;