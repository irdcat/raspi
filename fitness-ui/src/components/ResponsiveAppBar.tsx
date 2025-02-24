import { AppBar, AppBarProps, Button, IconButton, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import { useState } from "react";
import { LuMenu } from "react-icons/lu";
import { useLocation, useNavigate } from "react-router-dom";

const locations: { label: string, path: string }[] = [
    { label: "Bodyweight", path: "/" },
    { label: "Exercises", path: "/exercises" },
    { label: "Trainings", path: "/trainings" },
    { label: "Analysis", path: "/analysis" }
];

const DesktopAppBar = (props: AppBarProps) => {
    const { pathname } = useLocation();
    const navigate = useNavigate();

    return (
        <AppBar {...props}>
            <Toolbar sx={{ height: '100%', padding: '5px' }}>
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

const MobileAppBar = (props: AppBarProps) => {
    const { pathname } = useLocation();
    const navigate = useNavigate();
    const [anchorElNav, setAnchorElNav] = useState<null | HTMLElement>(null);

    const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorElNav(event.currentTarget);
    }

    const handleCloseNavMenu = () => {
        setAnchorElNav(null);
    }

    const handleMenuItemClick = (path: string) => () => {
        handleCloseNavMenu();
        navigate(path);
    }

    return (
        <AppBar {...props}>
            <Toolbar sx={{ height: '100%', padding: '5px' }}>
                <IconButton 
                    edge="start" 
                    color="inherit" 
                    sx={{ marginRight: 2 }}
                    onClick={handleOpenNavMenu}>
                    <LuMenu/>
                </IconButton>
                <Menu
                    anchorEl={anchorElNav}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                    keepMounted
                    transformOrigin={{ vertical: 'top', horizontal: 'left' }}
                    open={Boolean(anchorElNav)}
                    onClose={handleCloseNavMenu}
                    sx={{ display: 'block' }}>
                    {locations.map(location => (
                        <MenuItem 
                            disabled={location.path === pathname} 
                            key={location.label.toLowerCase()} 
                            onClick={handleMenuItemClick(location.path)}>
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

const ResponsiveAppBar = () => {
    return (
        <>
            <DesktopAppBar sx={{ height: '60px', display: { xs: 'none', md: 'block' } }}/>
            <MobileAppBar sx={{ height: '60px', display: { xs: 'block', md: 'none' } }}/>
        </>
    )
}

export default ResponsiveAppBar;