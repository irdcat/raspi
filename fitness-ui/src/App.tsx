import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline, darkScrollbar } from '@mui/material';
import Layout from './pages/Layout';
import Bodyweight from './pages/Bodyweight';
import Trainings from './pages/Trainings';
import Analysis from './pages/Analysis';
import Exercises from './pages/Exercises';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFnsV3';

const darkTheme = createTheme({
  palette: {
    mode: "dark"
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: () => ({
        body: darkScrollbar()
      })
    }
  }
});

const App = () => {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <ThemeProvider theme={ darkTheme }>
        <CssBaseline/>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Layout/>}>
              <Route index element={<Bodyweight/>}/>
              <Route path="trainings" element={<Trainings/>}/>
              <Route path="exercises" element={<Exercises/>}/>
              <Route path="analysis" element={<Analysis/>}/>
            </Route>
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </LocalizationProvider>
  );
}

export default App;