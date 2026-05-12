import { readTrustedIdentity } from '@/lib/auth/headers';
import { isAuthenticated } from '@/lib/auth/identity';
import { LoginView } from './login-view';

export const dynamic = 'force-dynamic';

export default function LoginPage() {
  const user = readTrustedIdentity();
  return <LoginView user={user} alreadyAuthenticated={isAuthenticated(user)} />;
}
