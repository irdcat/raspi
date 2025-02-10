import { AppBar, Box, Breadcrumbs, SvgIcon, SvgIconProps, Toolbar, Typography } from '@mui/material';
import { Link, Outlet, useLocation } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

const HomeIcon = (props: SvgIconProps) => {
  return (
    <SvgIcon {...props}>
      <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
    </SvgIcon>
  );
};

const LayoutBreadcrumbs = () => {
  const { pathname } = useLocation();

  if (pathname === '/') {
    return <HomeIcon color="primary" />;
  } else {
    return (
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />}>
        {pathname.split('/').map((pathPart, index, array) => {
          if (pathPart === '') {
            return (
              <Link key={index} to="/">
                <HomeIcon color="primary" />
              </Link>
            );
          } else if (index === array.length - 1) {
            return <Typography key={index}>{pathPart}</Typography>;
          } else {
            return (
              <Link key={index} style={{ textDecoration: 'none' }} to={array.slice(0, index + 1).join('/')}>
                <Typography color="primary">{pathPart}</Typography>
              </Link>
            );
          }
        })}
      </Breadcrumbs>
    );
  }
};

export const Layout = () => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <LayoutBreadcrumbs />
        </Toolbar>
      </AppBar>
      <Box component="main">
        <Outlet />
      </Box>
    </Box>
  );
};
