import { AppBar, Button, IconButton, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import { useState } from "react";
import { LuMenu } from "react-icons/lu";
import { useLocation, useNavigate } from "react-router-dom";

const locations: { label: string, path: string }[] = [
    { label: "Bodyweight", path: "/" },
    { label: "Exercises", path: "/exercises" },
    { label: "Trainings", path: "/trainings" },
    { label: "Analysis", path: "/analysis" },
    { label: "Templates", path: "/templates" }
];

const ResponsiveAppBar = () => {
    const [anchorElNav, setAnchorElNav] = useState<null | HTMLElement>(null);
    const { pathname } = useLocation();
    const navigate = useNavigate();

    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNav(event.currentTarget);
    };

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    };

    return (
        <AppBar sx={{ height: '60px' }}>
            <Toolbar sx={{ height: '100%', columnGap: '10px', display: { xs: 'none', md: 'flex'} }}>
                {locations.map(location => (
                    <Button
                        key={location.label.toLowerCase()}
                        disabled={pathname === location.path}
                        onClick={() => navigate(location.path)}>
                        {location.label}
                    </Button>
                ))}
            </Toolbar>
            <Toolbar sx={{ height: '100%', display: { xs: 'flex', md: 'none' } }}>
                <IconButton size="large" onClick={handleOpenNavMenu} color="inherit">
                    <LuMenu/>
                </IconButton>
                <Menu 
                    anchorEl={anchorElNav} 
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }} 
                    keepMounted
                    transformOrigin={{ vertical: 'top', horizontal: 'left' }}
                    open={Boolean(anchorElNav)}
                    onClose={handleCloseNavMenu}
                    sx={{ display: { xs: 'block', md: 'none' } }}>
                    {locations.filter(location => pathname !== location.path).map(location => (
                        <MenuItem key={location.label.toLowerCase()} onClick={() => {
                            handleCloseNavMenu();
                            navigate(location.path);
                        }}>
                            <Typography sx={{ textAlign: 'center' }}>
                                {location.label}
                            </Typography>
                        </MenuItem>
                    ))}
                </Menu>
            </Toolbar>
        </AppBar>
    )
}

export default ResponsiveAppBar;