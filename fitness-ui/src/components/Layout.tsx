import { 
    AppBar, 
    Box, 
    IconButton, 
    Menu, 
    MenuItem, 
    Toolbar, 
    Typography 
} from "@mui/material"
import { MouseEvent, useState } from "react"
import { Outlet, useNavigate } from "react-router-dom"
import MenuIcon from '@mui/icons-material/Menu'

export const Layout = () => {
    const [ menuAnchorEl, setMenuAnchorEl ] = useState<null | HTMLElement>(null);
    const open = Boolean(menuAnchorEl);
    const navigate = useNavigate();
    const handleMenuClick = (event: MouseEvent<HTMLButtonElement>) => {
        setMenuAnchorEl(event.currentTarget);
    }
    const handleMenuClose = () => {
        setMenuAnchorEl(null);
    }
    const handleMenuExercisesClick = () => {
        handleMenuClose();
        navigate("/exercises");
    }
    const handleMenuTrainingsClick = () => {
        handleMenuClose();
        navigate("/trainings");
    }

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static">
                <Toolbar>
                    <IconButton 
                        size="large" 
                        edge="start" 
                        color="inherit"
                        id="main-menu-button"
                        aria-controls={ open ? "main-menu" : undefined }
                        aria-haspopup="true"
                        aria-expanded={ open ? "true" : undefined }
                        aria-label="open drawer" sx={{ mr: 2, flexGrow: 0 }}
                        onClick={ handleMenuClick }>
                        <MenuIcon/>
                    </IconButton>
                    <Menu
                        id="main-menu"
                        anchorEl={ menuAnchorEl }
                        open={ open }
                        onClose={ handleMenuClose }
                        MenuListProps={{
                            "aria-labelledby": "main-menu-button"
                        }}>
                        <MenuItem onClick={ handleMenuExercisesClick }>Exercises</MenuItem>
                        <MenuItem onClick={ handleMenuTrainingsClick }>Trainings</MenuItem>
                    </Menu>
                    <Typography 
                        variant="h5" 
                        component="div" 
                        sx={{ 
                            flexGrow: 1, 
                            cursor: "pointer" 
                        }} 
                        onClick={() => navigate("/")}>
                        Fitness
                    </Typography>
                </Toolbar>
            </AppBar>
            <Box component="main">
                <Outlet/>
            </Box>
        </Box>
    )
}