import { UserManager, WebStorageStateStore } from 'oidc-client-ts';

const authority = import.meta.env.VITE_OIDC_AUTHORITY ?? 'http://localhost:8081/realms/adr-manager';

export const oidcManager = new UserManager({
  authority,
  client_id: import.meta.env.VITE_OIDC_CLIENT_ID ?? 'adr-manager-spa',
  redirect_uri: `${window.location.origin}/auth/callback`,
  silent_redirect_uri: `${window.location.origin}/auth/silent-callback`,
  post_logout_redirect_uri: window.location.origin,
  response_type: 'code',
  scope: 'openid profile email',
  automaticSilentRenew: true,
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
});
