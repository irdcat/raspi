import { Box } from "@mui/material";
import { Outlet } from "react-router-dom"
import ResponsiveAppBar from "../components/ResponsiveAppBar";

const Layout = () => {
    return (
        <Box sx={{ width: '100vw', height: '100vh'}}>
            <ResponsiveAppBar/>
            <Box sx={{ paddingTop: '60px', height: '100%' }}>
                <Outlet/>
            </Box>
        </Box>
    )
}

export default Layout;