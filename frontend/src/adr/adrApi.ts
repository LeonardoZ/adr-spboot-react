export type AdrStatus = 'DRAFT' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
export type Adr = { identifier: string; adlIdentifier: string; title: string; context: string; problem: string; optionsConsidered?: string | null; decision?: string | null; consequences?: string | null; status: AdrStatus; authorUserId: string; authorDisplayName: string; createdAt: string; submittedByUserId?: string | null; submittedAt?: string | null; decidedByUserId?: string | null; decidedByDisplayName?: string | null; decidedAt?: string | null; decisionComment?: string | null; rejectionJustification?: string | null; version: number };
export type AdrInput = { title: string; context: string; problem: string; optionsConsidered?: string; decision?: string; consequences?: string; version?: number };

async function request<T>(path: string, token: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`/api${path}`, { ...init, headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json', ...init?.headers } });
  if (!response.ok) { const body = await response.json().catch(() => null) as { message?: string } | null; throw new Error(body?.message ?? 'The ADR request could not be completed.'); }
  return response.json() as Promise<T>;
}

export const adrApi = {
  list: (token: string, adlIdentifier: string) => request<Adr[]>(`/adls/${adlIdentifier}/adrs`, token),
  detail: (token: string, identifier: string) => request<Adr>(`/adrs/${identifier}`, token),
  create: (token: string, adlIdentifier: string, value: AdrInput) => request<Adr>(`/adls/${adlIdentifier}/adrs`, token, { method: 'POST', body: JSON.stringify(value) }),
  update: (token: string, identifier: string, value: AdrInput) => request<Adr>(`/adrs/${identifier}`, token, { method: 'PUT', body: JSON.stringify(value) }),
  submit: (token: string, identifier: string, version: number) => request<Adr>(`/adrs/${identifier}/submit`, token, { method: 'POST', body: JSON.stringify({ version }) }),
  cancelReview: (token: string, identifier: string, version: number) => request<Adr>(`/adrs/${identifier}/cancel-review`, token, { method: 'POST', body: JSON.stringify({ version }) }),
  approve: (token: string, identifier: string, version: number, comment?: string) => request<Adr>(`/adrs/${identifier}/approve`, token, { method: 'POST', body: JSON.stringify({ version, comment }) }),
  reject: (token: string, identifier: string, version: number, comment: string, rejectionJustification: string) => request<Adr>(`/adrs/${identifier}/reject`, token, { method: 'POST', body: JSON.stringify({ version, comment, rejectionJustification }) }),
};
