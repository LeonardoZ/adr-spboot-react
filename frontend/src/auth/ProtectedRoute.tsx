import { CircularProgress } from '@mui/material';
import { Navigate, useLocation } from 'react-router-dom';
import { anonymousRedirect } from './navigation';
import { useAuth } from './AuthProvider';

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user, isLoading } = useAuth();
  const location = useLocation();
  if (isLoading) return <CircularProgress aria-label="Loading session" />;
  if (!user) return <Navigate to={anonymousRedirect(location.pathname)} replace />;
  return <>{children}</>;
}
