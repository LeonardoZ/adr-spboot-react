export type NavigationItem = {
  label: string;
  to: string;
};

export const navigationItems: NavigationItem[] = [
  { label: 'Home', to: '/' },
  { label: 'Decision Logs', to: '/adls' },
];

export function visibleNavigationItems(): NavigationItem[] {
  return navigationItems;
}

export function anonymousRedirect(pathname: string): string {
  return `/login?returnTo=${encodeURIComponent(pathname)}`;
}
