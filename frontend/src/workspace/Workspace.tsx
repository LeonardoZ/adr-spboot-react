import type { PropsWithChildren, ReactNode } from 'react';
import { Breadcrumbs, Box, Chip, Link, Paper, Stack, Typography } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const statusColor = (status: string): 'default' | 'info' | 'success' | 'warning' | 'error' => ({
  DRAFT: 'default',
  UNDER_REVIEW: 'warning',
  APPROVED: 'success',
  REJECTED: 'error',
  ARCHIVED: 'default',
})[status] ?? 'info';

export function StatusChip({ status }: { status: string }) {
  return <Chip color={statusColor(status)} label={status.replaceAll('_', ' ')} size="small" sx={{ fontWeight: 700, letterSpacing: '0.02em' }} />;
}

export function PageHeader({ eyebrow, title, description, action }: { eyebrow?: string; title: string; description?: string; action?: ReactNode }) {
  return <Box sx={{ display: 'flex', gap: 2, justifyContent: 'space-between', alignItems: { sm: 'flex-end' }, flexDirection: { xs: 'column', sm: 'row' } }}>
    <Box>
      {eyebrow && <Typography color="secondary" variant="overline" sx={{ fontWeight: 800, letterSpacing: '0.12em' }}>{eyebrow}</Typography>}
      <Typography variant="h3" component="h1">{title}</Typography>
      {description && <Typography color="text.secondary" sx={{ mt: 0.75, maxWidth: 680 }}>{description}</Typography>}
    </Box>
    {action}
  </Box>;
}

export function ContentSection({ title, action, children }: PropsWithChildren<{ title: string; action?: ReactNode }>) {
  return <Paper component="section" sx={{ p: { xs: 2, sm: 3 } }}>
    <Stack spacing={2}>
      <Box display="flex" alignItems="center" justifyContent="space-between" gap={2}>
        <Typography variant="h6" component="h2">{title}</Typography>{action}
      </Box>
      {children}
    </Stack>
  </Paper>;
}

export function EmptyState({ title, description, action }: { title: string; description: string; action?: ReactNode }) {
  return <Paper sx={{ p: { xs: 3, sm: 5 }, textAlign: 'center', borderStyle: 'dashed' }}>
    <Stack spacing={1.5} alignItems="center">
      <Typography variant="h6">{title}</Typography>
      <Typography color="text.secondary" sx={{ maxWidth: 440 }}>{description}</Typography>
      {action}
    </Stack>
  </Paper>;
}

export type BreadcrumbItem = { label: string; to?: string };

export function WorkspaceBreadcrumbs({ items }: { items: BreadcrumbItem[] }) {
  return <Breadcrumbs aria-label="Breadcrumb" separator="/">
    {items.map((item, index) => item.to && index < items.length - 1
      ? <Link component={RouterLink} key={`${item.label}-${item.to}`} to={item.to} underline="hover" color="inherit">{item.label}</Link>
      : <Typography color="text.secondary" key={`${item.label}-${index}`}>{item.label}</Typography>)}
  </Breadcrumbs>;
}
