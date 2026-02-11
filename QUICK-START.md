# 🚀 ReadyRoad Quick Start Guide

## ✅ Prerequisites

- ✅ Docker Desktop installed and running
- ✅ Port 8890 available (backend)
- ✅ Port 3306 available (MySQL)

---

## 🎯 Option 1: Docker Compose (Recommended)

### Step 1: Verify Docker is Running

```powershell
docker --version
docker-compose --version
```

Expected output:
```
Docker version 24.x.x
Docker Compose version v2.x.x
```

If not found, start Docker Desktop.

---

### Step 2: Review Environment Variables

File: `.env` (already created)
```bash
MYSQL_ROOT_PASSWORD=rootPass2026!
ADMIN_DEFAULT_PASSWORD=Admin2026Secure!
JWT_SECRET=404E635266556A586E3272357538782F...
```

✅ Already configured with secure defaults

---

### Step 3: Start Application

```powershell
cd C:\Users\heyde\Desktop\end_project\readyroad

# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f backend
```

---

### Step 4: Wait for Services (30-60 seconds)

Watch logs for:
```
✅ JWT Service Initialization
✅ Flyway migration completed
✅ HikariPool-1 - Start completed
✅ Started ReadyroadApplication in X seconds
```

---

### Step 5: Test Application

```powershell
# Health check
curl http://localhost:8890/actuator/health

# Expected: {"status":"UP"}

# API Documentation
Start-Process http://localhost:8890/swagger-ui.html

# Test login
$body = @{
    username = "admin"
    password = "Admin2026Secure!"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri http://localhost:8890/api/auth/login `
    -Method POST `
    -Body $body `
    -ContentType "application/json"

Write-Host "JWT Token: $($response.token)"
```

---

## 🎯 Option 2: Manual (Without Docker)

### Step 1: Start MySQL

```powershell
# If MySQL is already installed locally
mysql -u root -p

# Create database
CREATE DATABASE readyroad_prod CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON readyroad_prod.* TO 'haydar'@'localhost';
FLUSH PRIVILEGES;
exit;
```

---

### Step 2: Set Environment Variables

```powershell
$env:DB_USERNAME="haydar"
$env:DB_PASSWORD="Hh06101987@"
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="readyroad_prod"
$env:ADMIN_DEFAULT_PASSWORD="Admin2026Secure!"
$env:JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351"
$env:SPRING_PROFILES_ACTIVE="prod"
```

---

### Step 3: Build and Run

```powershell
cd C:\Users\heyde\Desktop\end_project\readyroad

# Clean and build
./mvnw clean package -DskipTests

# Run application
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## 🔍 Verification Steps

### 1. Check Containers (Docker)

```powershell
docker-compose ps
```

Expected:
```
NAME                  STATUS
readyroad-mysql       Up (healthy)
readyroad-backend     Up (healthy)
```

---

### 2. Check Backend Health

```powershell
curl http://localhost:8890/actuator/health
```

Expected:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

---

### 3. Check Database Connection

```powershell
# Docker
docker exec -it readyroad-mysql mysql -u haydar -pHh06101987@ -e "SHOW DATABASES;"

# Local
mysql -u haydar -pHh06101987@ -e "SHOW DATABASES;"
```

Expected:
```
+--------------------+
| Database           |
+--------------------+
| readyroad_prod     |
+--------------------+
```

---

### 4. Test API Endpoints

```powershell
# Login
curl -X POST http://localhost:8890/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"Admin2026Secure!"}'

# Expected: {"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...","type":"Bearer","expiresIn":86400000}

# Categories (public)
curl http://localhost:8890/api/categories

# Expected: [{"id":1,"code":"A","nameEn":"Traffic Rules",...}]
```

---

## 🛠️ Common Commands

### Docker Compose

```powershell
# Start
docker-compose up -d

# Stop
docker-compose stop

# Restart
docker-compose restart backend

# View logs
docker-compose logs -f backend
docker-compose logs -f mysql

# Remove everything (⚠️ deletes data)
docker-compose down -v

# Rebuild and start
docker-compose up -d --build
```

---

### Database

```powershell
# Access MySQL shell (Docker)
docker exec -it readyroad-mysql mysql -u haydar -pHh06101987@ readyroad_prod

# Backup database (Docker)
docker exec readyroad-mysql mysqldump -u haydar -pHh06101987@ readyroad_prod > backup.sql

# Restore database (Docker)
docker exec -i readyroad-mysql mysql -u haydar -pHh06101987@ readyroad_prod < backup.sql
```

---

## 🐛 Troubleshooting

### Problem: Port 8890 already in use

```powershell
# Find process using port
netstat -ano | findstr :8890

# Kill process (replace PID)
taskkill /PID <PID> /F

# Or change port in docker-compose.yml:
# ports:
#   - "8891:8890"
```

---

### Problem: MySQL connection failed

```powershell
# Check if MySQL is healthy
docker-compose ps mysql

# Check MySQL logs
docker-compose logs mysql

# Restart MySQL
docker-compose restart mysql
```

---

### Problem: Backend fails to start

```powershell
# Check backend logs
docker-compose logs backend | Select-String -Pattern "ERROR|Exception"

# Common issues:
# 1. Database not ready → wait 30s and restart
# 2. Environment variable missing → check .env file
# 3. Port conflict → change port in docker-compose.yml
```

---

## 📊 Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| **Backend API** | http://localhost:8890 | - |
| **Swagger UI** | http://localhost:8890/swagger-ui.html | - |
| **Actuator** | http://localhost:8890/actuator | - |
| **Health** | http://localhost:8890/actuator/health | - |
| **MySQL** | localhost:3306 | haydar / Hh06101987@ |
| **Admin Login** | POST /api/auth/login | admin / Admin2026Secure! |

---

## ✅ Success Indicators

When everything is working correctly, you should see:

1. ✅ Both containers running and healthy
2. ✅ Backend logs show "Started ReadyroadApplication"
3. ✅ Health endpoint returns `{"status":"UP"}`
4. ✅ Login returns JWT token
5. ✅ No ERROR messages in logs

---

## 🎉 Next Steps

1. ✅ Test API with Postman or Swagger UI
2. ✅ Create test users
3. ✅ Load sample data
4. ✅ Test exam flow
5. ✅ Connect frontend application

---

**Ready to use! 🚀**

For detailed documentation, see `DOCKER-GUIDE.md`
