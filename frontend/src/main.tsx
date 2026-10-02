import React from 'react';
import { createRoot } from 'react-dom/client';
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './auth/AuthProvider';

const queryClient = new QueryClient();
const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#9b4a2e', contrastText: '#fffaf6' },
    secondary: { main: '#2f6b62' },
    background: { default: '#f8f4ef', paper: '#fffdfa' },
    text: { primary: '#2d2926', secondary: '#625a54' },
    success: { main: '#237a57' },
    warning: { main: '#9a5a12' },
    error: { main: '#b33a3a' },
  },
  shape: { borderRadius: 14 },
  typography: {
    fontFamily: 'Inter, ui-sans-serif, system-ui, sans-serif',
    h3: { fontWeight: 700, letterSpacing: '-0.035em' },
    h4: { fontWeight: 700, letterSpacing: '-0.025em' },
    h5: { fontWeight: 700 },
    button: { fontWeight: 700, textTransform: 'none' },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: { border: '1px solid #eadfd5', boxShadow: '0 8px 24px rgba(67, 47, 35, 0.06)' },
      },
    },
    MuiButton: { styleOverrides: { root: { borderRadius: 10 } } },
  },
});
createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <AuthProvider>
            <App />
          </AuthProvider>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  </React.StrictMode>,
);
