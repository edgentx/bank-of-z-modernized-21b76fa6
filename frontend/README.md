# Bank-of-Z Teller — Frontend

Next.js 14 + TypeScript + Tailwind teller workstation UI for the Bank-of-Z modernization.
Bootstrapped by S-36.

## Stack

- Next.js 14 (App Router, typed routes)
- React 18 + TypeScript 5 (strict)
- Tailwind CSS 3 + clsx/tailwind-merge (`cn()` helper)
- Axios-based typed API client (`lib/api`)
- ESLint (`next/core-web-vitals`) + Prettier 3
- `.env.development` / `.env.staging` / `.env.production` for environment-scoped config

## Scripts

```
npm install
npm run dev          # local dev with HMR
npm run build        # production build
npm run start        # serve the production build
npm run lint         # eslint
npm run typecheck    # tsc --noEmit
npm run test         # node:test + tsx (lib/**/__tests__/**)
npm run format       # prettier --write .
```

## Configuration

Public, browser-exposed values are prefixed `NEXT_PUBLIC_`. Copy `.env.example` to
`.env.local` for personal overrides; `.env.{environment}` files at the repo root are
loaded by Next based on `NODE_ENV`.

| Variable                   | Notes                                                                                                                    |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| `NEXT_PUBLIC_API_BASE_URL` | Base URL for the Spring Boot teller API.                                                                                 |
| `NEXT_PUBLIC_APP_NAME`     | Display name shown in the UI shell.                                                                                      |
| `NEXT_PUBLIC_ENVIRONMENT`  | Friendly env tag (development/staging/...).                                                                              |
| `API_REQUEST_TIMEOUT_MS`   | Server-side default request timeout.                                                                                     |
| `BANK_MOCK_USER_ENABLED`   | Enables mocked teller identity when trusted headers are absent. Defaults to `true`. Set `false` when an IdP is deployed. |
| `BANK_MOCK_USER_ID`        | Mock teller user ID. Defaults to `TELLER001`.                                                                            |
| `BANK_MOCK_USER_ROLES`     | Comma-separated mock roles. Defaults to `TELLER,SUPERVISOR`.                                                             |

Production-style deployments should set `NEXT_PUBLIC_API_BASE_URL=/api` so browser calls stay on the same origin and route through the ingress to the Spring Boot API.

## API Surface

The landing dashboard loads `GET /api/dashboard/summary` and expects the typed summary shape in `lib/api/dashboard.ts`. Monetary totals are returned as minor units, matching the frontend formatting helpers.

The 3270 terminal loads screen maps from `GET /api/terminal/screens/{screenId}`. The workstation starts on `MAINMENU`; the `SIGNON` screen is a terminal screen map, not a separate browser credential form.

## Layout

```
frontend/
├── app/                # App Router routes (layout.tsx, page.tsx, accounts/, login/, ...)
├── components/         # Shared UI (ui/) and chrome (layout/)
├── lib/
│   ├── api/            # Axios client + typed envelope (Page, ApiError)
│   ├── auth/           # Trusted-header identity, AuthProvider, role helpers
│   └── utils.ts        # cn() helper
├── public/             # Static assets
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```

Downstream stories should add per-resource modules under `lib/api/` (e.g. `accounts.ts`)
that call `api.get<T>(...)` and surface `ApiError` to the UI.

## Authentication (S-37)

User identity is **never** collected by the teller frontend. Authentication is
performed upstream by the Envoy + OPA sidecar, which forwards trusted headers
on every request:

| Header                 | Meaning                                |
| ---------------------- | -------------------------------------- |
| `X-User-Id`            | Sidecar-asserted user identifier       |
| `X-User-Roles`         | Comma-separated role list              |
| `X-Session-Expires-At` | Unix epoch (seconds or ms) or ISO-8601 |

The root `app/layout.tsx` reads these via `next/headers` (`readTrustedIdentity()`)
and seeds `<AuthProvider>` with the resulting `UserIdentity`. Client code calls
`useAuth()` / `useUser()` for identity, `hasRole(...)` for authorization, and
`logout()` to clear session state. The provider also runs a session-timeout
watchdog that bounces the user back to `/login` when `X-Session-Expires-At`
elapses (or on tab refocus, in case the timer drifted during sleep).

Until the platform identity provider is deployed for BANK, the frontend falls
back to a mocked teller identity when trusted headers are missing:
`TELLER001` with `TELLER,SUPERVISOR` roles. Disable that fallback with
`BANK_MOCK_USER_ENABLED=false` once Envoy/OPA is forwarding real identity
headers.

Pure helpers live in `lib/auth/identity.ts` and are covered by
`lib/auth/__tests__/`.
