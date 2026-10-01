import { Alert, Box, CircularProgress, Stack, Typography } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../auth/AuthProvider';
import { auditApi } from './auditApi';
import { ContentSection } from '../workspace/Workspace';

export function AuditHistory({ kind, identifier }: { kind: 'adl' | 'adr'; identifier: string }) {
  const { user } = useAuth(); const token = user?.access_token ?? '';
  const history = useQuery({ queryKey: ['audit', kind, identifier], queryFn: () => auditApi[kind](token, identifier), enabled: Boolean(token && identifier) });
  if (history.isLoading) return <CircularProgress aria-label="Loading audit history"/>;
  if (history.error) return <Alert severity="error">{history.error instanceof Error ? history.error.message : 'The audit history could not be loaded.'}</Alert>;
  return <ContentSection title="Decision timeline">{history.data?.length === 0 ? <Typography color="text.secondary">No audit events have been recorded.</Typography> : <Stack spacing={2}>{history.data?.map((event) => <Box key={`${event.occurredAt}-${event.action}`} sx={{ borderLeft: 3, borderColor: 'secondary.main', pl: 2 }}><Typography variant="body2" color="text.secondary">{new Date(event.occurredAt).toLocaleString()}</Typography><Typography sx={{ fontWeight: 700 }}>{event.action}</Typography><Typography variant="body2">by {event.actorDisplayName}</Typography>{event.afterValue && <Typography component="pre" variant="body2" sx={{ whiteSpace: 'pre-wrap', m: 0.5, color: 'text.secondary' }}>{event.afterValue}</Typography>}</Box>)}</Stack>}</ContentSection>;
}
