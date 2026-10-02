import {
  Alert,
  Box,
  Button,
  CircularProgress,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Stack,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthProvider';
import { adlApi } from '../adl/adlApi';
import { ContentSection, EmptyState, PageHeader } from '../workspace/Workspace';

export function HomePage() {
  const { user } = useAuth();
  const token = user?.access_token ?? '';
  const recent = useQuery({
    queryKey: ['adls', 'home'],
    queryFn: () => adlApi.list(token, { size: '5', sort: 'createdAt,desc' }),
    enabled: Boolean(token),
  });

  return (
    <Stack spacing={3}>
      <PageHeader
        eyebrow="Decision register"
        title="Decisions that bring people along"
        description="Capture the context, document the decision, and leave a clear trail for the next person."
        action={
          <Button component={Link} to="/adls/new" variant="contained">
            Create ADL
          </Button>
        }
      />
      {recent.isLoading && (
        <Box display="flex" justifyContent="center" py={5}>
          <CircularProgress aria-label="Loading decision register" />
        </Box>
      )}
      {recent.error && (
        <Alert severity="error">
          {recent.error instanceof Error
            ? recent.error.message
            : 'The decision register could not be loaded.'}
        </Alert>
      )}
      {recent.data && (
        <ContentSection
          title="Your decision register"
          action={
            <Button component={Link} to="/adls">
              View all logs
            </Button>
          }
        >
          <Typography color="text.secondary">
            {recent.data.totalElements}{' '}
            {recent.data.totalElements === 1
              ? 'architecture decision log'
              : 'architecture decision logs'}{' '}
            in the register
          </Typography>
          {recent.data.content.length === 0 ? (
            <EmptyState
              title="Start the conversation"
              description="Create an Architecture Decision Log to give a new decision a shared home."
              action={
                <Button component={Link} to="/adls/new" variant="contained">
                  Create ADL
                </Button>
              }
            />
          ) : (
            <List disablePadding aria-label="Recently created decision logs">
              {recent.data.content.map((adl) => (
                <ListItem disablePadding key={adl.identifier} divider>
                  <ListItemButton component={Link} to={`/adls/${adl.identifier}`}>
                    <ListItemText primary={adl.title} secondary={adl.identifier} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          )}
        </ContentSection>
      )}
    </Stack>
  );
}
