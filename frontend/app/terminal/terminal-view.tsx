'use client';

import { useMemo } from 'react';
import { Card } from '@/components/ui/card';
import { TerminalEmulator } from '@/components/terminal/terminal-emulator';
import { BANK_SCREENS, BANK_INITIAL_SCREEN } from '@/lib/screens/bank-screens';
import { useAuth } from '@/lib/auth/context';

const VIEW_ROLES = ['TELLER', 'SUPERVISOR', 'BRANCH_MANAGER'];

/**
 * /terminal entry point. Renders the canonical @vforce360/terminal
 * component (ported from dashboard/src/components/terminal/) against
 * the BANK_SCREENS definition set in lib/screens/. Screen definitions
 * are static client-side data — the BMS-map equivalent — and the
 * emulator drives navigation + API submission internally per each
 * screen's `apiMapping` / `menu` / `functionKeys`.
 */
export function TerminalView() {
  const auth = useAuth();

  const guard = useMemo(() => {
    if (!auth.authenticated) {
      return (
        <Card
          title="Sign-in required"
          description="The 3270 workstation is only available to authenticated teller staff."
        />
      );
    }
    if (!auth.hasAnyRole(VIEW_ROLES)) {
      return (
        <Card
          title="Insufficient role"
          description={`Terminal access requires one of: ${VIEW_ROLES.join(', ')}.`}
        />
      );
    }
    return null;
  }, [auth]);

  if (guard) return guard;

  return (
    <TerminalEmulator
      screens={BANK_SCREENS}
      initialScreen={BANK_INITIAL_SCREEN}
      apiBase="/api"
      theme="green"
      fit="fluid"
      persistKey="bank-teller"
    />
  );
}
