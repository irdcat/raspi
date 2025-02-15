import { AppBar, Box, Button, Toolbar } from "@mui/material";
import { Outlet, useLocation, useNavigate } from "react-router-dom"

const Layout = () => {
    const location = useLocation();
    const navigate = useNavigate();

    return (
        <Box sx={{ width: '100vw', height: '100vh'}}>
            <AppBar sx={{ height: '60px' }}>
                <Toolbar sx={{ height: '60px', padding: '5px' }}>
                    <Button onClick={() => navigate("/")} disabled={location.pathname === "/"}>
                        Bodyweight
                    </Button>
                    <Button onClick={() => navigate("/exercises")} disabled={location.pathname === "/exercises"}>
                        Exercises
                    </Button>
                    <Button onClick={() => navigate("/trainings")} disabled={location.pathname === "/trainings"}>
                        Trainings
                    </Button>
                    <Button onClick={() => navigate("/analysis")} disabled={location.pathname === "/analysis"}>
                        Analysis
                    </Button>
                </Toolbar>
            </AppBar>
            <Box sx={{ paddingTop: '60px', height: '100%' }}>
                <Outlet/>
            </Box>
        </Box>
    )
}

export default Layout;