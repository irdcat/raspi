import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { DialogsProvider } from '@toolpad/core';
import Layout from './pages/Layout';
import Home from './pages/Home';
import Database from './pages/Database';
import Collection from './pages/Collection';

const darkTheme = createTheme({
  palette: {
    mode: "dark"
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: () => ({
        body: {
          "&::-webkit-scrollbar, & *::-webkit-scrollbar": {
              backgroundColor: 'rgba(23, 23, 23, 0.23)',
              width: '5px'
          },
          "&::-webkit-scrollbar-thumb, & *::-webkit-scrollbar-thumb": {
              borderRadius: 13,
              backgroundColor: 'rgba(96, 96, 96, 0.73)',
              minHeight: 26,
              border: 'none'
          },
          "&::-webkit-scrollbar-thumb:focus, & *::-webkit-scrollbar-thumb:focus": {
              backgroundColor: 'rgba(96, 96, 96, 0.84)'
          },
          "&::-webkit-scrollbar-thumb:active, & *::-webkit-scrollbar-thumb:active": {
              backgroundColor: 'rgba(149, 149, 149, 0.93)'
          },
          "&::-webkit-scrollbar-thumb:hover, & *::-webkit-scrollbar-thumb:hover": {
              backgroundColor: 'rgba(149, 149, 149, 0.73)'
          },
          "&::-webkit-scrollbar-corner, & *::-webkit-scrollbar-corner": {
              backgroundColor: 'rgba(0, 0, 0, 1)'
          }
        }
      })
    }
  }
});

const App = () => {
  return (
    <ThemeProvider theme={ darkTheme }>
      <DialogsProvider>
        <CssBaseline/>
        <BrowserRouter basename="/mongo">
          <Routes>
            <Route path="/" element={<Layout/>}>
              <Route index element={<Home/>}/>
              <Route path="/:db" element={<Database/>}/>
              <Route path="/:db/:col" element={<Collection/>}/>
              <Route path="/:db/:col/:doc" element={<></>}/>
            </Route>
          </Routes>
        </BrowserRouter>
      </DialogsProvider>  
    </ThemeProvider>
  );
}

export default App;