import { Card, CardActionArea, CardContent, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { useNavigate } from 'react-router-dom';
import useWindowDimensions from '../hooks/useWindowDimensions';
import { ReactElement } from 'react';

type MainMenuItemProps = {
  title: string;
  description: string;
  navigateTo: string;
};

const MainMenuItem = (props: MainMenuItemProps) => {
  const navigate = useNavigate();

  return (
    <Card sx={{ height: '100%' }}>
      <CardActionArea sx={{ height: '100%' }} onClick={() => navigate(props.navigateTo)}>
        <CardContent>
          <Typography gutterBottom variant="h5" component="div">
            {props.title}
          </Typography>
          <Typography variant="body2" sx={{ color: 'text.secondary' }}>
            {props.description}
          </Typography>
        </CardContent>
      </CardActionArea>
    </Card>
  );
};

type MainMenuGridItemProps = {
  children: ReactElement;
};

const MainMenuGridItem = (props: MainMenuGridItemProps) => {
  return (
    <Grid size={{ xs: 12, sm: 6 }} sx={{ height: '50%' }}>
      {props.children}
    </Grid>
  );
};

type MainMenuGridProps = {
  layoutOffset: number;
  children: ReactElement[];
};

const MainMenuGrid = (props: MainMenuGridProps) => {
  const { height } = useWindowDimensions();

  return (
    <Grid
      container
      rowSpacing={1}
      columnSpacing={{ xs: 1, sm: 3 }}
      sx={{ height: height - props.layoutOffset, padding: 2 }}
    >
      {props.children}
    </Grid>
  );
};

export const Home = () => {
  return (
    <MainMenuGrid layoutOffset={64}>
      <MainMenuGridItem>
        <MainMenuItem
          title="Exercises"
          description="Manage exercises that will be used to populate the trainings"
          navigateTo="/exercises"
        />
      </MainMenuGridItem>
      <MainMenuGridItem>
        <MainMenuItem
          title="Trainings"
          description="Manage trainings that will be used for progress analysis"
          navigateTo="/trainings"
        />
      </MainMenuGridItem>
      <MainMenuGridItem>
        <MainMenuItem
          title="Templates"
          description="Manage Training Templates that are used to represent trainings belonging to a repeatable training plan. 
                                Templates are used to speed up the process of adding training data."
          navigateTo="/templates"
        />
      </MainMenuGridItem>
      <MainMenuGridItem>
        <MainMenuItem title="Coming soon..." description="This feature is not ready yet..." navigateTo="/" />
      </MainMenuGridItem>
    </MainMenuGrid>
  );
};
