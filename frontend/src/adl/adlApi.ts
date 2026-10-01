export type AdrSummary = { identifier: string; title: string; status: 'DRAFT' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED'; authorDisplayName: string; createdAt: string };
export type Adl = { identifier: string; title: string; context: string; problem: string; archivedAt?: string | null; tags: string[]; adrs: AdrSummary[]; version: number };
export type AdlInput = { title: string; context: string; problem: string; tags: string[]; version?: number };
export type AdlPage = { content: Adl[]; page: number; size: number; totalElements: number; totalPages: number };

async function request<T>(path: string, token: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`/api${path}`, { ...init, headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json', ...init?.headers } });
  if (!response.ok) { const body = await response.json().catch(() => null) as { message?: string } | null; throw new Error(body?.message ?? 'The ADL request could not be completed.'); }
  return response.json() as Promise<T>;
}

export const adlApi = {
  list: (token: string, filters: Record<string, string> = {}) => request<AdlPage>(`/adls?${new URLSearchParams(filters)}`, token),
  detail: (token: string, identifier: string) => request<Adl>(`/adls/${identifier}`, token),
  create: (token: string, value: AdlInput) => request<Adl>('/adls', token, { method: 'POST', body: JSON.stringify(value) }),
  update: (token: string, identifier: string, value: AdlInput) => request<Adl>(`/adls/${identifier}`, token, { method: 'PUT', body: JSON.stringify(value) }),
  archive: (token: string, identifier: string, version: number) => request<Adl>(`/adls/${identifier}/archive`, token, { method: 'POST', body: JSON.stringify({ version }) }),
};
