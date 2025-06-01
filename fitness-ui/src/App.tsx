import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import Layout from './pages/Layout';
import Bodyweight from './pages/Bodyweight';
import Trainings from './pages/Trainings';
import Analysis from './pages/Analysis';
import Exercises from './pages/Exercises';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import TrainingDetails from './pages/TrainingDetails';
import { DialogsProvider } from '@toolpad/core';
import Templates from './pages/Templates';
import TemplateDetails from './pages/TemplateDetails';

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
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <ThemeProvider theme={ darkTheme }>
        <DialogsProvider>
          <CssBaseline/>
          <BrowserRouter basename="/fitness">
            <Routes>
              <Route path="/" element={<Layout/>}>
                <Route index element={<Bodyweight/>}/>
                <Route path="trainings" element={<Trainings/>}/>
                <Route path="trainings/:dateString" element={<TrainingDetails/>}/>
                <Route path="exercises" element={<Exercises/>}/>
                <Route path="analysis/:exerciseName" element={<Analysis/>}/>
                <Route path="templates" element={<Templates/>}/>
                <Route path="templates/:id" element={<TemplateDetails/>}/>
              </Route>
            </Routes>
          </BrowserRouter>
        </DialogsProvider>  
      </ThemeProvider>
    </LocalizationProvider>
  );
}

export default App;