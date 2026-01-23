# 🎯 Quick Reference: Registration Debugging

## 🚀 Start Backend
```powershell
cd C:\Users\fqsdg\Desktop\end_project\readyroad
.\mvnw.cmd spring-boot:run
```

## 🧪 Test Registration
```powershell
# Option 1: Use test script
.\test_registration.ps1

# Option 2: Use curl
curl -X POST http://localhost:8890/api/auth/register -H "Content-Type: application/json" -d '{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"SecurePass123!\",\"fullName\":\"Test User\"}'
```

## 📊 Expected Success Log Pattern
```
🌐 INCOMING REQUEST → Method: POST, URI: /api/auth/register
🔍 JWT Filter → No JWT token found - allowing anonymous access
📝 REGISTRATION REQUEST RECEIVED → Username: testuser
🔍 AuthService.register() - Starting registration process
✅ Username available
✅ Email available
✅ User saved successfully with ID: 1
✅ JWT token generated
✅ Registration successful!
```

## 🔍 Troubleshooting by Log Pattern

| What You See | Problem | Solution |
|-------------|---------|----------|
| No "INCOMING REQUEST" | Request not reaching backend | Check if backend running: `netstat -ano \| findstr :8890` |
| "INCOMING REQUEST" but wrong URI | Frontend using wrong URL | Verify: `http://localhost:8890/api/auth/register` |
| "INCOMING REQUEST" but no controller log | Security blocking or wrong path | Check method is POST, path is `/api/auth/register` |
| "❌ Username already exists" | Duplicate user | Use different username or delete existing user |
| Error at "Saving user to database" | Database issue | Check MySQL running, check credentials |
| Error at "Generate JWT token" | JWT config issue | Check `application-dev.yml` JWT settings |

## 🌐 Browser DevTools Checklist
1. Press **F12** → Network tab
2. Clear requests (🚫 icon)
3. Try registration
4. Click the `register` request
5. Check:
   - ✅ Request URL: `http://localhost:8890/api/auth/register`
   - ✅ Method: `POST`
   - ✅ Status: `201` (success) or error code
   - ✅ Headers: `Content-Type: application/json`
   - ✅ Payload: Contains username, email, password, fullName
   - ✅ Response: Contains token or error message

## 🎯 Files Created
- `RequestLoggingFilter.java` - Logs ALL incoming requests
- `REGISTRATION_DEBUG_GUIDE.md` - Complete debugging guide
- `LOGGING_SOLUTION_SUMMARY.md` - Detailed solution overview
- `test_registration.ps1` - Test script
- `QUICK_REFERENCE.md` - This file

## 📋 What to Share for Help
1. Backend console logs (complete flow from "INCOMING REQUEST" to result)
2. Browser Network tab screenshot (Request + Response)
3. Browser Console errors (if any)

The logs will tell us EXACTLY where it fails! 🎯

---

## 🔧 Common IDE Issues

### Error: "could not set current directory - mobile_app"
**Fixed!** The `readyroad-backend.iml` file had references to the old mobile_app folder location.

**Solution:**
- ✅ Already fixed in the `.iml` file
- If error persists: `File → Invalidate Caches and Restart` in IntelliJ IDEA
- Or reimport: Right-click `pom.xml` → Maven → Reimport

See `MOBILE_APP_PATH_FIX.md` for details.

