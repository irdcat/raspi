import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Home } from './components/Home';
import { Exercises } from './components/Exercises';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { Trainings } from './components/Trainings';
import ExerciseDetails from './components/ExerciseDetails';
import TrainingDetails from './components/TrainingDetails';
import { Templates } from './components/Templates';
import TemplateDetails from './components/TemplateDetails';

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