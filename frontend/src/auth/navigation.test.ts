import { describe, expect, it } from 'vitest';
import { anonymousRedirect, visibleNavigationItems } from './navigation';

describe('OIDC navigation guards', () => {
  it('redirects anonymous visitors to login while retaining the requested route', () => {
    expect(anonymousRedirect('/adls')).toBe('/login?returnTo=%2Fadls');
  });

  it('shows all application navigation to authenticated users without role checks', () => {
    expect(visibleNavigationItems().map((item) => item.label)).toEqual(['Home', 'Decision Logs']);
  });
});
