# Proposal

## Why

[GitHub Issue #14](https://github.com/user-bcp-student43/workshop-auth-backend/issues/14) requires a signed token with explicit identity and expiry claims. The Issue describes HMAC and `AUTH_TOKEN_SECRET`, while `docs/ENDPOINTS.md` and the current behavior describe a Base64-encoded legacy value with an embedded secret; this discrepancy must be reconciled during implementation without deciding the signing format here. A verifiable token contract protects authenticated identity, scopes the token to the requesting client, and makes its advertised lifetime consistent with its expiration.

## What Changes

- Issue a signed token containing `sub`, `client`, `iat`, and `exp`; use the authenticated username as `sub` and the validated request client as `client`.
- Require `client` to be a non-blank string. This intentionally changes `docs/ENDPOINTS.md`, where `client` is optional and currently ignored: the Issue requires a client claim sourced from and validated against the request. Missing or blank `client` will use the existing 400 validation behavior.
- Keep the successful login response fields and their meanings, including `token`, `expiresIn`, `user`, and `debug`; retain the documented 3600-second lifetime and ensure `expiresIn` agrees with `exp - now` within a documented one-second tolerance for epoch-second precision.
- Do not use the lab default signing secret outside the sandbox. If a default is retained for the lab, document it as lab-only. Never log token or secret values.
- Treat a token as expired at or after its `exp`; add observable verification coverage for token claims and expiry without adding a new public authentication endpoint.
- Preserve the documented 400 behavior for null requests and blank username/password, and 401 for invalid credentials.

### Out of Scope

- Choosing or documenting the implementation format or library (HMAC versus a JWT library); that technical decision is deferred.
- Changing the `user` or `debug` response fields, username/password validation, invalid-credential status, CORS, health endpoint, SQL access, or error response contract.
- Adding protected endpoints or changing the broader authentication flow.
- Refactoring application layers or changing password storage.

### Behavior Preserved From `docs/ENDPOINTS.md`

- `POST /auth/login` remains unauthenticated and accepts JSON with `username` and `password`.
- Successful credentials still return 200 with `token`, `expiresIn` in seconds, and the same `user.username`, escaped `user.displayName`, and `user.role` fields; the existing `debug` field is retained.
- Null requests and missing/blank username or password remain 400; invalid credentials remain 401.
- The health endpoint and its contract are unaffected.

## Capabilities

### New Capabilities

- `auth-token`: signed login-token claims, expiry, secret handling, and protection of sensitive token material.

### Modified Capabilities

None. The project has no existing OpenSpec capabilities.

## Impact

- API: the `client` request field becomes required and validated; successful response shape remains stable while the token's signed claims and expiry become verifiable.
- Runtime configuration and operational logging: non-sandbox token issuance must not rely on the lab default secret, and token/secret values must not appear in logs.
- Verification: acceptance evidence covers claims, expiry, valid login, invalid credentials, invalid client, and absence of sensitive values in logs.