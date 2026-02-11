# ReadyRoad Backend - Startup Instructions

## 🎯 Quick Start (Recommended)

### Option 1: Use the Quick Run Script
```powershell
.\QUICK-RUN.ps1
```

This script will:
- Set all required environment variables
- Build the project
- Start the application

---

## 🔧 Manual Setup (If needed)

### Step 1: Clean Up Failed Flyway Migration (One-time only)

If you see an error about "Schema contains a failed migration to version 77", run this in MySQL:

```sql
USE readyroad_prod;
DELETE FROM flyway_schema_history WHERE version = '77' AND success = 0;
```

**Or use the provided script:**
```powershell
mysql -u haydar -p -e "USE readyroad_prod; DELETE FROM flyway_schema_history WHERE version = '77' AND success = 0;"
```
(Enter password: `Hh06101987@` when prompted)

### Step 2: Set Environment Variables
```powershell
$env:DB_USERNAME = "haydar"
$env:DB_PASSWORD = "Hh06101987@"
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "readyroad_prod"
$env:ADMIN_DEFAULT_PASSWORD = "Admin2026Secure!"
$env:JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351"
$env:SPRING_PROFILES_ACTIVE = "prod"
```

### Step 3: Build and Run
```powershell
# Build (skip tests for faster startup)
./mvnw clean compile -DskipTests

# Run application
./mvnw spring-boot:run
```

---

## 📊 Application Endpoints

Once started, the application will be available at:

| Endpoint | URL |
|----------|-----|
| **Backend API** | http://localhost:8890 |
| **Swagger UI** | http://localhost:8890/swagger-ui.html |
| **Health Check** | http://localhost:8890/actuator/health |
| **API Docs** | http://localhost:8890/v3/api-docs |

---

## 🔐 Default Login Credentials

```
Username: admin
Password: Admin2026Secure!
```

---

## 🐛 Troubleshooting

### Error: "Access denied for user 'haydar'@'localhost' to database 'readyroad'"
**Solution:** This happens when the wrong profile is active. Ensure `SPRING_PROFILES_ACTIVE=prod` is set.

### Error: "Schema contains a failed migration to version 77"
**Solution:** Clean up the failed migration record (see Step 1 above).

### Error: "Cannot connect to database"
**Solution:** Ensure MySQL is running and the database `readyroad_prod` exists.

### Error: "Compilation error in ExamController"
**Solution:** This has been fixed. Run `./mvnw clean compile -DskipTests` to rebuild.

---

## ✅ Recent Fixes Applied

1. **Fixed IDOR Vulnerability** - `AuthenticationUtil` properly injected in `ExamController`
2. **Fixed Schema Validation** - `score_percentage` column type corrected in `ExamSimulation`
3. **Fixed Quiz Schema** - Created migration V77 to align `quiz_user_answers` table with entity
4. **Fixed Profile Configuration** - `application.yml` now respects `SPRING_PROFILES_ACTIVE` environment variable
5. **Fixed Database Connection** - Application now correctly uses `readyroad_prod` database when running with `prod` profile

---

## 📝 Configuration Details

### Active Profile: prod
- Database: `readyroad_prod`
- Port: `8890`
- Hibernate: `validate` (schema validation only, no auto-updates)
- Connection Pool: HikariCP (optimized for production)
- Logging: INFO level
- JWT: Enabled with 24-hour expiration

### Database Schema
- Managed by Flyway migrations
- Current version: V77 (Quiz User Answers Schema Fix)
- Location: `src/main/resources/db/migration/`

---

## 🚀 Next Steps

After successful startup:
1. Test the health endpoint: http://localhost:8890/actuator/health
2. Access Swagger UI: http://localhost:8890/swagger-ui.html
3. Login with admin credentials
4. Run any additional tests if needed

---

## 📞 Support

If you encounter any issues not covered here:
1. Check the application logs in the console
2. Verify all environment variables are set correctly
3. Ensure MySQL service is running
4. Verify database `readyroad_prod` exists and is accessible
