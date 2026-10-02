import { createContext, PropsWithChildren, useContext, useEffect, useMemo, useState } from 'react';
import type { User } from 'oidc-client-ts';
import { oidcManager } from './oidc';
type AuthContextValue = {
  user: User | null;
  isLoading: boolean;
  login: (returnTo?: string) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);
export function AuthProvider({ children }: PropsWithChildren) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    oidcManager.getUser().then((storedUser) => {
      setUser(storedUser?.expired ? null : storedUser);
      setIsLoading(false);
    });
    const onUserLoaded = (nextUser: User) => setUser(nextUser);
    const onUserUnloaded = () => setUser(null);
    oidcManager.events.addUserLoaded(onUserLoaded);
    oidcManager.events.addUserUnloaded(onUserUnloaded);
    return () => {
      oidcManager.events.removeUserLoaded(onUserLoaded);
      oidcManager.events.removeUserUnloaded(onUserUnloaded);
    };
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isLoading,
      login: (returnTo = '/') => oidcManager.signinRedirect({ state: { returnTo } }),
      logout: () => oidcManager.signoutRedirect(),
    }),
    [isLoading, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
