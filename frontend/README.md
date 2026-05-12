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
npm run format       # prettier --write .
```

## Configuration

Public, browser-exposed values are prefixed `NEXT_PUBLIC_`. Copy `.env.example` to
`.env.local` for personal overrides; `.env.{environment}` files at the repo root are
loaded by Next based on `NODE_ENV`.

| Variable                  | Notes                                      |
| ------------------------- | ------------------------------------------ |
| `NEXT_PUBLIC_API_BASE_URL`| Base URL for the Spring Boot teller API.   |
| `NEXT_PUBLIC_APP_NAME`    | Display name shown in the UI shell.        |
| `NEXT_PUBLIC_ENVIRONMENT` | Friendly env tag (development/staging/...).|
| `API_REQUEST_TIMEOUT_MS`  | Server-side default request timeout.       |

## Layout

```
frontend/
├── app/                # App Router routes (layout.tsx, page.tsx, accounts/, ...)
├── components/         # Shared UI (ui/) and chrome (layout/)
├── lib/
│   ├── api/            # Axios client + typed envelope (Page, ApiError)
│   └── utils.ts        # cn() helper
├── public/             # Static assets
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```

Downstream stories should add per-resource modules under `lib/api/` (e.g. `accounts.ts`)
that call `api.get<T>(...)` and surface `ApiError` to the UI.
