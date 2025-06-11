import { ChevronRight, House } from "lucide-react";
import { AppBar, Box, Breadcrumbs, Link, Toolbar, Typography } from "@mui/material";
import { Outlet, useLocation, useNavigate } from "react-router-dom"

const TopBar = () => {
    const { pathname } = useLocation();
    const navigate = useNavigate();

    let parts = pathname
        .split('/')
        .filter(v => v.length !== 0)
        .map((value, index, array) => 
            ([value, array
                .filter((_, filterIndex) => filterIndex <= index)
                .join('/')]))
    parts.unshift(["", "/"])

    return (
        <AppBar sx={{ height: '60px' }}>
            <Toolbar sx={{ height: '100%' }}>
                <Breadcrumbs separator={<ChevronRight fontSize="small"/>}>
                {
                    parts.map(([val, path], index, array) => {
                        if (index === 0) {
                            return (
                                <Link underline="hover" color="inherit">
                                    {array.length === 1 
                                        ? (<Box><House/></Box>)
                                        : (<Box sx={{ cursor: 'pointer' }} onClick={() => navigate(`/${path}`)}><House/></Box>)
                                    }
                                </Link>
                            )
                        } else if (index < array.length - 1) {
                            return (
                                <Link underline="hover" color="inherit">
                                    <Box sx={{ cursor: 'pointer' }} onClick={() => navigate(path)}>
                                        {val}
                                    </Box>
                                </Link>
                            )
                        }
                        return <Typography>{val}</Typography>
                    })
                }
                </Breadcrumbs>
            </Toolbar>
        </AppBar>
    )
}

const Layout = () => {
    return (
        <Box sx={{ width: '100vw', height: '100vh'}}>
            <TopBar/>
            <Box sx={{ height: '100%', paddingTop: '60px' }}>
                <Outlet/>
            </Box>
        </Box>
    )
}

export default Layout;