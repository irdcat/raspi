import { AppBar, Button, Toolbar } from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";

const locations: { label: string, path: string }[] = [
    { label: "Bodyweight", path: "/" },
    { label: "Exercises", path: "/exercises" },
    { label: "Trainings", path: "/trainings" },
    { label: "Analysis", path: "/analysis" }
];

const ResponsiveAppBar = () => {
    const { pathname } = useLocation();
    const navigate = useNavigate();

    return (
        <AppBar>
            <Toolbar sx={{ height: '100%', columnGap: '10px' }}>
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
    )
}

export default ResponsiveAppBar;