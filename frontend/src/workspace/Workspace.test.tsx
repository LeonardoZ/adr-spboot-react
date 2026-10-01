import '@testing-library/jest-dom/vitest';
import { ThemeProvider, createTheme } from '@mui/material';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import { ContentSection, EmptyState, PageHeader, StatusChip, WorkspaceBreadcrumbs } from './Workspace';

describe('workspace primitives', () => {
  it('renders labeled content and textual status independently of color', () => {
    render(<ThemeProvider theme={createTheme()}><PageHeader eyebrow="Decision register" title="Payments" description="Choose a direction" /><ContentSection title="Overview">A shared decision</ContentSection><StatusChip status="UNDER_REVIEW" /><EmptyState title="No decisions yet" description="Create the first ADL." /></ThemeProvider>);
    expect(screen.getByRole('heading', { name: 'Payments' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: 'Overview' })).toBeInTheDocument();
    expect(screen.getByText('UNDER REVIEW')).toBeInTheDocument();
    expect(screen.getByText('No decisions yet')).toBeInTheDocument();
  });

  it('renders usable breadcrumb links for parent contexts', () => {
    render(<MemoryRouter><WorkspaceBreadcrumbs items={[{ label: 'Decision Logs', to: '/adls' }, { label: 'ADL-1', to: '/adls/ADL-1' }, { label: 'ADR-1' }]} /></MemoryRouter>);
    expect(screen.getByRole('link', { name: 'Decision Logs' })).toHaveAttribute('href', '/adls');
    expect(screen.getByRole('link', { name: 'ADL-1' })).toHaveAttribute('href', '/adls/ADL-1');
    expect(screen.getByText('ADR-1')).toBeInTheDocument();
  });
});
