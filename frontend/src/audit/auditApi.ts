export type AuditEvent = {
  entityType: string;
  entityIdentifier: string;
  action: string;
  actorUserId: string;
  actorDisplayName: string;
  occurredAt: string;
  beforeValue?: string | null;
  afterValue?: string | null;
};

async function request(path: string, token: string): Promise<AuditEvent[]> {
  const response = await fetch(`/api${path}`, { headers: { Authorization: `Bearer ${token}` } });
  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as { message?: string } | null;
    throw new Error(body?.message ?? 'The audit history could not be loaded.');
  }
  return response.json() as Promise<AuditEvent[]>;
}
export const auditApi = {
  adl: (token: string, identifier: string) => request(`/adls/${identifier}/audit-events`, token),
  adr: (token: string, identifier: string) => request(`/adrs/${identifier}/audit-events`, token),
};
