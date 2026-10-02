import { useEffect } from 'react';
import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material';
import { Link, Navigate, NavLink, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './auth/AuthProvider';
import { oidcManager } from './auth/oidc';
import { ProtectedRoute } from './auth/ProtectedRoute';
import { visibleNavigationItems } from './auth/navigation';
import { AdlDetailPage, AdlFormPage, AdlsPage } from './adl/AdlsPage';
import { AdrDetailPage, AdrFormPage, AdrsPage } from './adr/AdrsPage';
import { HomePage } from './home/HomePage';

function Login() {
  const { user, login } = useAuth();
  const location = useLocation();
  const returnTo = new URLSearchParams(location.search).get('returnTo') ?? '/';
  if (user) return <Navigate to={returnTo} replace />;
  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4">Sign in to ADR Manager</Typography>
      <Button sx={{ mt: 2 }} variant="contained" onClick={() => void login(returnTo)}>
        Sign in
      </Button>
    </Container>
  );
}

function Callback() {
  const navigate = useNavigate();
  useEffect(() => {
    void oidcManager.signinRedirectCallback().then((user) => {
      const state = user.state as { returnTo?: string } | undefined;
      navigate(state?.returnTo ?? '/', { replace: true });
    });
  }, [navigate]);
  return (
    <Container sx={{ py: 4 }}>
      <Typography>Completing sign-in…</Typography>
    </Container>
  );
}

function SilentCallback() {
  useEffect(() => {
    void oidcManager.signinSilentCallback();
  }, []);
  return null;
}

export function Application() {
  const { logout, user } = useAuth();
  return (
    <Box
      sx={{ minHeight: '100vh', minWidth: 0, overflowX: 'hidden', bgcolor: 'background.default' }}
    >
      <AppBar position="static" elevation={0} sx={{ bgcolor: '#2d2926', backgroundImage: 'none' }}>
        <Toolbar sx={{ gap: 1, flexWrap: 'wrap', py: 1 }}>
          <Typography
            variant="h6"
            sx={{ fontWeight: 800, flexGrow: { xs: 1, md: 0 }, mr: { md: 3 } }}
          >
            ADR Manager
          </Typography>
          <Box
            component="nav"
            aria-label="Main navigation"
            sx={{
              display: 'flex',
              gap: 0.5,
              order: { xs: 3, md: 0 },
              width: { xs: '100%', md: 'auto' },
            }}
          >
            {visibleNavigationItems().map((item) => (
              <Button
                color="inherit"
                component={NavLink}
                end={item.to === '/'}
                key={item.to}
                to={item.to}
                sx={{ '&.active': { bgcolor: 'rgba(255,255,255,0.16)' } }}
              >
                {item.label}
              </Button>
            ))}
          </Box>
          <Box sx={{ flexGrow: 1 }} />
          <Typography variant="body2" sx={{ mx: 1 }}>
            {user?.profile.preferred_username}
          </Typography>
          <Button color="inherit" onClick={() => void logout()}>
            Sign out
          </Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{ minWidth: 0 }}>
        <Box sx={{ py: { xs: 3, sm: 5 }, minWidth: 0 }}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/adls" element={<AdlsPage />} />
            <Route path="/adls/new" element={<AdlFormPage />} />
            <Route path="/adls/:identifier" element={<AdlDetailPage />} />
            <Route path="/adls/:identifier/edit" element={<AdlFormPage />} />
            <Route path="/adls/:adlIdentifier/adrs" element={<AdrsPage />} />
            <Route path="/adls/:adlIdentifier/adrs/new" element={<AdrFormPage />} />
            <Route path="/adrs/:identifier" element={<AdrDetailPage />} />
            <Route path="/adrs/:identifier/edit" element={<AdrFormPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Box>
      </Container>
    </Box>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/auth/callback" element={<Callback />} />
      <Route path="/auth/silent-callback" element={<SilentCallback />} />
      <Route
        path="*"
        element={
          <ProtectedRoute>
            <Application />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
