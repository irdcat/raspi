import { AppBar, Box, Button, Toolbar } from "@mui/material";
import { Outlet, useLocation, useNavigate } from "react-router-dom"

const locations: { label: string, path: string }[] = [
    { label: "Bodyweight", path: "/" },
    { label: "Exercises", path: "/exercises" },
    { label: "Trainings", path: "/trainings" },
    { label: "Analysis", path: "/analysis" }
];

const Layout = () => {
    const { pathname } = useLocation();
    const navigate = useNavigate();

    return (
        <Box sx={{ width: '100vw', height: '100vh'}}>
            <AppBar sx={{ height: '60px' }}>
                <Toolbar sx={{ height: '60px', padding: '5px' }}>
                    {locations.map(location => (
                        <Button 
                            key={location.label.toLowerCase()} 
                            disabled={pathname === location.path}
                            onClick={() => navigate(location.path)}>
                            {location.label}
                        </Button> 
                    ))}
                </Toolbar>
            </AppBar>
            <Box sx={{ paddingTop: '60px', height: '100%' }}>
                <Outlet/>
            </Box>
        </Box>
    )
}

export default Layout;