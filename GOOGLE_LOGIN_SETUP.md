# Google Login Setup

This project now supports a production-grade Google social login foundation with:

- OAuth code exchange on the backend
- BFF-managed PKCE + state handling in Next.js
- Explicit Google account linking for existing password accounts
- No automatic merge when a password-based account already exists for the same email

## UX Policy

The implemented behavior intentionally matches common global-product patterns:

- `Continue with Google` appears on login and register pages
- Email/password login remains available as a fallback
- If Google returns an email that already belongs to a password-based ReadyRoad account:
  - login is blocked
  - the user is asked to sign in normally first
  - Google can then be linked manually from the profile page
- Google linking is explicit only
- New Google users are created as standard ReadyRoad users and get a generated unique username

## Local Development

### 1. Google Cloud Console

Create or open a Google Cloud project, then:

1. Go to `APIs & Services` -> `Credentials`
2. Create an `OAuth client ID`
3. Choose `Web application`
4. Add these exact values for local development:

Authorized JavaScript origins:

- `http://localhost:3000`

Authorized redirect URIs:

- `http://localhost:3000/api/auth/google/callback`

If you use another local frontend host or port, add that exact origin and callback pair too.

### 2. Backend env

Set these values in:

`C:\Users\haydar\Desktop\end_project\readyroad\.env`

Required:

- `FRONTEND_URL=http://localhost:3000`
- `GOOGLE_OAUTH_CLIENT_ID`
- `GOOGLE_OAUTH_CLIENT_SECRET`

Defaults already configured:

- `GOOGLE_OAUTH_TOKEN_URI=https://oauth2.googleapis.com/token`
- `GOOGLE_OAUTH_USER_INFO_URI=https://openidconnect.googleapis.com/v1/userinfo`

### 3. Frontend env

Set this value in:

`C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app\.env.local`

Required:

- `BACKEND_URL=http://localhost:8890/api`
- `FRONTEND_URL=http://localhost:3000`
- `GOOGLE_OAUTH_CLIENT_ID`

### 4. Docker Compose

`docker-compose.yml` now forwards the Google OAuth variables into:

- `backend`
- `frontend`

So once the values exist in `readyroad/.env`, Docker-based runs can consume them.

### 5. Local validation path

Recommended verification:

1. Open `/login`
2. Click `Continue with Google`
3. Complete the Google flow
4. Confirm redirect to `/dashboard`
5. Open `/profile`
6. Confirm Google appears in linked sign-in methods

Also test this conflict case:

1. Create a normal password account with an email
2. Try the same email through Google
3. Confirm login is blocked with the account-exists message
4. Log in normally
5. Link Google from `/profile`

## Production Setup

### Redirect URI

If the production frontend domain is `https://readyroad.be`, add these exact values in Google Cloud Console:

Authorized JavaScript origins:

- `https://readyroad.be`

Authorized redirect URIs:

- `https://readyroad.be/api/auth/google/callback`

If your real frontend domain changes, use that exact frontend origin and callback instead. The callback must stay on the frontend domain because the BFF handles the OAuth callback.

### Required production envs

Backend:

- `FRONTEND_URL=https://readyroad.be`
- `GOOGLE_OAUTH_CLIENT_ID`
- `GOOGLE_OAUTH_CLIENT_SECRET`
- `GOOGLE_OAUTH_TOKEN_URI`
- `GOOGLE_OAUTH_USER_INFO_URI`

Frontend:

- `GOOGLE_OAUTH_CLIENT_ID`
- `BACKEND_URL`

### Docker note

`docker-compose.yml` now reads these values from:

- `C:\Users\haydar\Desktop\end_project\readyroad\.env`

That means Docker-based local or server runs only need the Google values and `FRONTEND_URL` filled in once there.

If you run the frontend outside Docker during development, you still need:

- `C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app\.env.local`

### Final activation checklist

Local:

1. Fill `GOOGLE_OAUTH_CLIENT_ID` and `GOOGLE_OAUTH_CLIENT_SECRET` in `readyroad/.env`
2. Fill `GOOGLE_OAUTH_CLIENT_ID` in `readyroad_front_end/web_app/.env.local`
3. Add the localhost origin and callback in Google Cloud Console
4. Restart backend and frontend
5. Test `/login`, `/register`, and `/profile`

Production:

1. Add the production origin and callback in Google Cloud Console
2. Set `FRONTEND_URL` to your public frontend URL
3. Set Google OAuth envs for backend and frontend
4. Deploy both layers
5. Test login, register, and explicit linking from `/profile`

## Implemented Error States

The frontend currently handles these normalized states:

- `provider_cancelled`
- `provider_denied`
- `state_mismatch`
- `exchange_failed`
- `unavailable`
- `account_exists_with_password`
- `email_not_verified`
- `profile_invalid`
- `already_linked`
- `linked_elsewhere`
- `email_mismatch`
- `login_required`

## Security Notes

- The BFF stores temporary PKCE/state flow values in short-lived `HttpOnly` cookies
- Session auth remains JWT in `HttpOnly` cookie
- CSRF protection remains in place through the existing BFF cookie strategy
- Google emails must be verified
- Linking requires the Google email to match the current ReadyRoad account email

## Files Involved

Backend:

- `C:\Users\haydar\Desktop\end_project\readyroad\src\main\java\com\readyroad\readyroadbackend\service\GoogleOAuthService.java`
- `C:\Users\haydar\Desktop\end_project\readyroad\src\main\java\com\readyroad\readyroadbackend\service\SocialAuthService.java`
- `C:\Users\haydar\Desktop\end_project\readyroad\src\main\java\com\readyroad\readyroadbackend\controller\AuthController.java`
- `C:\Users\haydar\Desktop\end_project\readyroad\src\main\java\com\readyroad\readyroadbackend\controller\UserController.java`

Frontend:

- `C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app\src\app\api\auth\google\start\route.ts`
- `C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app\src\app\api\auth\google\callback\route.ts`
- `C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app\src\components\auth\google-auth-button.tsx`
- `C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app\src\app\(protected)\profile\page.tsx`

## Recommended Next Product Step

If you want the flow to feel even more like large global products, the next best enhancement is:

- add a dedicated `Linked accounts` sub-section in account settings
- show provider badges (`Google`, later `Apple`)
- add a `Disconnect Google` flow with safeguards
