import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Layout } from './pages/Layout';
import { Home } from './pages/Home';
import { Exercises } from './pages/Exercises';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { Trainings } from './pages/Trainings';
import ExerciseDetails from './pages/ExerciseDetails';
import TrainingDetails from './pages/TrainingDetails';
import { Templates } from './pages/Templates';
import TemplateDetails from './pages/TemplateDetails';

const darkTheme = createTheme({
  palette: {
    mode: "dark"
  }
});

const App = () => {
  return (
    <ThemeProvider theme={ darkTheme }>
      <CssBaseline/>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout/>}>
            <Route index element={<Home/>}/>
            <Route path="exercises" element={<Exercises/>}/>
            <Route path="exercises/:id" element={<ExerciseDetails/>}/>
            <Route path="trainings" element={<Trainings/>}/>
            <Route path="trainings/:id" element={<TrainingDetails/>}/>
            <Route path="templates" element={<Templates/>}/>
            <Route path="templates/:id" element={<TemplateDetails/>}/>
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;