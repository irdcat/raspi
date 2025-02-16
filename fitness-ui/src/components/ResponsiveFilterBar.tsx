import { Box, Button, IconButton, Popover } from "@mui/material";
import { ReactElement, useState } from "react";
import { LuFilter } from "react-icons/lu";

const DesktopFilterBar = ({ children }: { children: ReactElement[] }) => {
    return (
        <Box sx={{ display: { xs: 'none', md: 'flex' }, columnGap: '4px', flexGrow: 1 }}>
            {children}
        </Box>
    )
}

const MobileFilterBar = ({ children }: { children: ReactElement[] }) => {
    const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

    const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };

    const handleClose = () => {
        setAnchorEl(null);
    };

    const open = Boolean(anchorEl);

    return (
        <Box sx={{ display: { xs: 'flex', md: 'none' }, flexGrow: 1 }}>
            <Button onClick={handleClick} sx={{ height: '40px', color: 'white', borderColor: 'rgba(255, 255, 255, 0.23)' }} variant="outlined" startIcon={<LuFilter/>}>
                Filters
            </Button>
            <Popover 
                open={open} 
                anchorEl={anchorEl}
                onClose={handleClose} 
                anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                sx={{ marginTop: '20px' }}>
                <Box sx={{ padding: '7px', display: 'flex', flexDirection: 'column', rowGap: '9px' }}>
                    {children}
                </Box>
            </Popover>
        </Box>
    )
}

const ResponsiveFilterBar = ({ children }: { children: ReactElement[] }) => {
    return (
        <>
            <DesktopFilterBar>
                {children}
            </DesktopFilterBar>
            <MobileFilterBar>
                {children}
            </MobileFilterBar>
        </>
    )
}

export default ResponsiveFilterBar;