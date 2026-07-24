import { Navigate, Route, Routes } from 'react-router-dom';
import { RootLayout } from './components/RootLayout';
import { CurrentWorkoutPage } from './pages/CurrentWorkoutPage';
import { MesocyclesPage } from './pages/MesocyclesPage';
import { MesoBuilderPage } from './pages/MesoBuilderPage';
import { MesoBoardPage } from './pages/MesoBoardPage';
import { DayViewPage } from './pages/DayViewPage';
import { ExercisesPage } from './pages/ExercisesPage';
import { ProfilePage } from './pages/ProfilePage';

export default function App() {
  return (
    <Routes>
      <Route element={<RootLayout />}>
        <Route index element={<CurrentWorkoutPage />} />
        <Route path="mesocycles" element={<MesocyclesPage />} />
        <Route path="mesocycles/new" element={<MesoBuilderPage />} />
        <Route path="mesocycles/:id" element={<MesoBoardPage />} />
        <Route path="days/:id" element={<DayViewPage />} />
        <Route path="exercises" element={<ExercisesPage />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
