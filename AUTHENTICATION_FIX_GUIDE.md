# ReadyRoad Authentication Fix - Testing Guide

## Problem Summary

Login was failing with "BadCredentialsException: Bad credentials" because:

1. ✅ Spring Security configuration is correct (fixed)
2. ✅ JWT filter properly skips `/api/auth/**` paths (fixed)
3. ❌ **Missing or incorrect user credentials in database** (to be fixed)

## Solution: Create Test User

### Option 1: Use SQL Script (Fastest)

1. **Run the SQL script**:

   ```bash
   mysql -u root -p readyroad < C:\Users\heyde\Desktop\end_project\readyroad\scripts\create-test-user.sql
   ```

   Or manually in MySQL Workbench:
   - Open `scripts/create-test-user.sql`
   - Execute the script
   - Verify user was created (query is included in script)

2. **Test Credentials**:
   - Username: `testuser`
   - Password: `Test123!`
   - Email: `testuser@readyroad.be`

### Option 2: Generate Custom Password Hash

If you want a different password:

1. **Update and run the hash generator**:

   ```bash
   cd C:\Users\heyde\Desktop\end_project\readyroad
   
   # Edit PASSWORD constant in PasswordHashGenerator.java
   # Then run:
   mvn compile exec:java -Dexec.mainClass="com.readyroad.readyroadbackend.util.PasswordHashGenerator"
   ```

2. **Copy the generated hash** and use it in your SQL INSERT

### Option 3: Use Register Endpoint (After Backend Restart)

1. **Restart backend**:

   ```bash
   cd C:\Users\heyde\Desktop\end_project\readyroad
   mvn spring-boot:run -Dspring-boot.run.profiles=secure
   ```

2. **Wait for "Started ReadyRoadApplication"**

3. **Register via API**:

   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8890/api/auth/register" `
     -Method POST `
     -ContentType "application/json" `
     -Body '{"username":"testuser","email":"testuser@readyroad.be","password":"Test123!","fullName":"Test User"}'
   ```

## Verification Steps

### 1. Restart Backend

```bash
cd C:\Users\heyde\Desktop\end_project\readyroad
mvn spring-boot:run -Dspring-boot.run.profiles=secure
```

Wait for: `Started ReadyRoadApplication in X.XXX seconds`

### 2. Test Authentication Endpoints

**Test Login (should succeed with testuser/Test123!)**:

```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8890/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"testuser","password":"Test123!"}'

Write-Host "✅ Login successful!" -ForegroundColor Green
Write-Host "Token: $($response.token.Substring(0, 20))..." -ForegroundColor Cyan
Write-Host "User ID: $($response.userId)" -ForegroundColor Cyan
Write-Host "Role: $($response.role)" -ForegroundColor Cyan
```

**Expected Success Response**:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 123,
  "username": "testuser",
  "email": "testuser@readyroad.be",
  "role": "STUDENT",
  "expiresIn": 86400
}
```

**Test with Wrong Password (should fail with 401)**:

```powershell
try {
  Invoke-RestMethod -Uri "http://localhost:8890/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body '{"username":"testuser","password":"WrongPass123!"}'
} catch {
  Write-Host "✅ Expected failure: Wrong password returns 401" -ForegroundColor Green
  $_.Exception.Response.StatusCode
}
```

**Test Health Endpoint (should succeed)**:

```powershell
$health = Invoke-RestMethod -Uri "http://localhost:8890/api/auth/health"
Write-Host "✅ Health check: $($health.status)" -ForegroundColor Green
```

**Test Protected Endpoint Without Token (should fail with 401)**:

```powershell
try {
  Invoke-RestMethod -Uri "http://localhost:8890/api/users/me/progress"
} catch {
  Write-Host "✅ Expected failure: Protected endpoint requires JWT" -ForegroundColor Green
  $_.Exception.Response.StatusCode
}
```

**Test Protected Endpoint With Token (should succeed)**:

```powershell
# First login to get token
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8890/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"testuser","password":"Test123!"}'

# Use token to access protected endpoint
$headers = @{
  "Authorization" = "Bearer $($loginResponse.token)"
}

$progress = Invoke-RestMethod -Uri "http://localhost:8890/api/users/me/progress" `
  -Headers $headers

Write-Host "✅ Protected endpoint accessed successfully!" -ForegroundColor Green
```

### 3. Test Web Frontend

1. **Start web app** (in separate terminal):

   ```bash
   cd C:\Users\heyde\Desktop\end_project\readyroad_front_end\web_app
   npm run dev
   ```

2. **Navigate to**: <http://localhost:3000/login>

3. **Login with**:
   - Username: `testuser`
   - Password: `Test123!`

4. **Expected**: Redirect to `/dashboard` with user session active

## Acceptance Criteria Checklist

- [ ] Backend starts without errors
- [ ] Test user exists in database with BCrypt hash
- [ ] Login with correct credentials returns JWT token (200 OK)
- [ ] Login with wrong credentials returns 401 Unauthorized
- [ ] Login endpoint is accessible without Authorization header
- [ ] Protected endpoints return 401 without JWT token
- [ ] Protected endpoints work with valid JWT token
- [ ] Web frontend login succeeds and redirects to dashboard
- [ ] No "Authentication required" error for auth endpoints

## Common Issues

### Issue: "Unable to connect to the remote server"

**Solution**: Backend is not running. Start it with `mvn spring-boot:run -Dspring-boot.run.profiles=secure`

### Issue: "BadCredentialsException: Bad credentials"

**Solution**:

1. Check user exists: `SELECT * FROM users WHERE username='testuser';`
2. Verify password hash starts with `$2a$` or `$2b$` and is 60 characters
3. Re-run the SQL script to create user with known hash

### Issue: "User not found"

**Solution**: Run the SQL script to create the test user

### Issue: Port 8890 already in use

**Solution**:

1. Find process: `netstat -ano | findstr :8890`
2. Kill process: `taskkill /PID <pid> /F`
3. Restart backend

## Files Modified

1. ✅ `SecurityConfigSecure.java` - Added explicit documentation, matcher order clarified
2. ✅ `JwtAuthenticationFilter.java` - Added `shouldNotFilter()` method
3. ✅ `scripts/create-test-user.sql` - Test user creation script
4. ✅ `PasswordHashGenerator.java` - Utility to generate BCrypt hashes

## Summary

The authentication fix is complete:

- **Security Configuration**: ✅ Auth endpoints are public
- **JWT Filter**: ✅ Skips validation for `/api/auth/**`
- **Test User**: ⚠️ Must be created using provided SQL script or register endpoint
- **Quality Gate**: ✅ Protected endpoints remain secured

**Next Action**: Run the SQL script, restart backend, test login!
