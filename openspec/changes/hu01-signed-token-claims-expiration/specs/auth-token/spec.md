# Spec Delta

## Purpose

Defines the observable contract for signed tokens issued by login, including identity claims, client binding, expiry, secret isolation, and protection of sensitive token material.

## ADDED Requirements

### Requirement: Successful login issues a signed token with identity claims

The system MUST issue a signed token containing `sub`, `client`, `iat`, and `exp` for successful login. `sub` MUST equal the authenticated username, `client` MUST reflect the validated request value, and `iat` and `exp` MUST be epoch-second timestamps. Altering the token claims MUST invalidate its signature.

#### Scenario: Valid login returns claims bound to the user and client

- **WHEN** a user with valid credentials logs in with a non-blank `client`
- **THEN** the 200 response contains a signed `token` whose `sub` equals the persisted username, whose `client` equals the request value, and whose `iat` and `exp` are present

#### Scenario: Altered claims are not accepted as signed

- **WHEN** a token claim is altered after the token is issued
- **THEN** signature verification rejects the altered token

### Requirement: Login requires a non-blank client

The system MUST accept `client` as a string containing at least one non-whitespace character before issuing a token. This is an intentional change from the Day 1 contract, which describes `client` as optional and unused; Issue #14 requires it as a validated token claim.

#### Scenario: Client is omitted

- **WHEN** the login request contains valid username and password but omits `client`
- **THEN** the response is 400 Bad Request and no token is issued

#### Scenario: Client is blank

- **WHEN** the login request contains valid username and password but `client` is empty or whitespace-only
- **THEN** the response is 400 Bad Request and no token is issued

### Requirement: Token expiry agrees with the login response

The system MUST issue tokens with a 3600-second lifetime and return `expiresIn` as 3600 seconds. The difference between `exp` and the current time at issuance MUST agree with `expiresIn` within a documented tolerance of one second for epoch-second precision.

#### Scenario: Successful login reports a consistent expiry

- **WHEN** a valid login issues a token
- **THEN** `exp` is later than `iat`, `expiresIn` is 3600, and `exp - now` differs from `expiresIn` by no more than one second

### Requirement: Expired tokens are rejected

The system MUST treat a token as invalid at or after its `exp` claim. This requirement does not add a public token-consumption endpoint.

#### Scenario: Token is evaluated at or after expiry

- **WHEN** token validity is evaluated at a time greater than or equal to `exp`
- **THEN** the token is rejected as expired and does not establish an authenticated identity

### Requirement: Successful login preserves the documented response contract

The system MUST preserve the successful response fields and meanings documented for `POST /auth/login`, except for the explicitly changed token content and the newly required `client` input.

#### Scenario: Valid login retains response fields

- **WHEN** valid credentials and a valid `client` are submitted
- **THEN** the response is 200 and contains `token`, integer `expiresIn`, `user.username`, HTML-escaped `user.displayName`, `user.role`, and `debug` with its existing value `authenticated-with-legacy-flow`

### Requirement: Existing credential failures retain their documented statuses

The system MUST preserve the Day 1 status behavior for null login requests, missing or blank username/password, and invalid credentials.

#### Scenario: Request or required credentials are missing

- **WHEN** the login request is null, or username or password is null, empty, or blank
- **THEN** the response is 400 Bad Request with the documented validation message

#### Scenario: Credentials are invalid

- **WHEN** the username is unknown or the password does not match
- **THEN** the response is 401 Unauthorized with the documented invalid-credentials message and no token

### Requirement: The lab default secret is not used outside the sandbox

Outside the sandbox, the system MUST NOT issue a successful login token using the documented lab default secret when no `AUTH_TOKEN_SECRET` is configured. Any retained default MUST be documented as exclusive to the laboratory.

#### Scenario: Non-sandbox runtime has no configured secret

- **WHEN** login is attempted outside the sandbox without `AUTH_TOKEN_SECRET`
- **THEN** the system does not return a successful login response containing a token signed with the lab default

### Requirement: Login does not expose token or secret values in logs

The system MUST NOT write issued token values or signing secret values to application logs during login processing.

#### Scenario: Login processing is logged

- **WHEN** a valid or invalid login request is processed
- **THEN** application logs contain neither the issued token nor the configured or default signing secret