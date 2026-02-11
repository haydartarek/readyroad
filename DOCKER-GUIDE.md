# 🐳 ReadyRoad Docker Guide

## 🚀 Quick Start (2 minutes)

### 1. Create .env file
```bash
cp .env.example .env
```

Edit `.env` and set:
```bash
ADMIN_DEFAULT_PASSWORD=YourStrongPassword123!
MYSQL_ROOT_PASSWORD=YourMySQLRootPassword
```

### 2. Start everything
```bash
docker-compose up -d
```

### 3. Check status
```bash
docker-compose ps
docker-compose logs -f backend
```

### 4. Access application
- **Backend API:** http://localhost:8890
- **Swagger UI:** http://localhost:8890/swagger-ui.html
- **Health Check:** http://localhost:8890/actuator/health

---

## 📋 Detailed Commands

### Build & Start

```bash
# Build and start all services
docker-compose up -d

# Build without cache (clean build)
docker-compose build --no-cache

# Start with build
docker-compose up -d --build

# Start with logs visible
docker-compose up
```

### Stop & Remove

```bash
# Stop services
docker-compose stop

# Stop and remove containers
docker-compose down

# Stop, remove containers + volumes (⚠️ deletes database data)
docker-compose down -v

# Stop, remove containers + images
docker-compose down --rmi all
```

### Logs & Monitoring

```bash
# View all logs
docker-compose logs

# Follow logs (live)
docker-compose logs -f

# Backend logs only
docker-compose logs -f backend

# MySQL logs only
docker-compose logs -f mysql

# Last 100 lines
docker-compose logs --tail=100 backend
```

### Service Management

```bash
# Restart a service
docker-compose restart backend
docker-compose restart mysql

# Rebuild specific service
docker-compose up -d --build backend

# Scale services (if needed)
docker-compose up -d --scale backend=2
```

### Database Access

```bash
# Connect to MySQL container
docker exec -it readyroad-mysql mysql -u haydar -pHh06101987@ readyroad_prod

# Or as root
docker exec -it readyroad-mysql mysql -u root -p

# Run SQL file
docker exec -i readyroad-mysql mysql -u haydar -pHh06101987@ readyroad_prod < backup.sql

# Backup database
docker exec readyroad-mysql mysqldump -u haydar -pHh06101987@ readyroad_prod > backup.sql
```

### Backend Container Access

```bash
# Enter backend container shell
docker exec -it readyroad-backend sh

# View backend logs inside container
docker exec readyroad-backend cat /app/logs/readyroad.log

# Check Java process
docker exec readyroad-backend ps aux
```

---

## 🔍 Health Checks

### Application Health

```bash
# Quick health check
curl http://localhost:8890/actuator/health

# Detailed health (if configured)
curl http://localhost:8890/actuator/health | jq

# Check all actuator endpoints
curl http://localhost:8890/actuator | jq
```

### Container Health

```bash
# Check container health status
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Inspect health check
docker inspect readyroad-backend | jq '.[0].State.Health'
```

---

## 🐛 Troubleshooting

### Problem: Container won't start

```bash
# Check logs for errors
docker-compose logs backend

# Check if port is already in use
netstat -ano | findstr :8890
netstat -ano | findstr :3306

# Remove old containers
docker-compose down
docker-compose up -d
```

### Problem: Database connection failed

```bash
# Check if MySQL is ready
docker-compose logs mysql | grep "ready for connections"

# Verify MySQL is healthy
docker ps --filter "name=readyroad-mysql"

# Test connection
docker exec readyroad-mysql mysql -u haydar -pHh06101987@ -e "SELECT 1"
```

### Problem: Backend fails to connect to MySQL

```bash
# Check environment variables
docker exec readyroad-backend env | grep DB_

# Check network connectivity
docker exec readyroad-backend ping -c 3 mysql

# Restart in correct order
docker-compose restart mysql
sleep 10
docker-compose restart backend
```

### Problem: Out of memory

```bash
# Check container memory usage
docker stats

# Increase memory in docker-compose.yml:
# backend:
#   deploy:
#     resources:
#       limits:
#         memory: 2G
#       reservations:
#         memory: 1G
```

---

## 🔐 Security Best Practices

### 1. Change Default Passwords

```bash
# Generate strong password
openssl rand -base64 32

# Update .env file
ADMIN_DEFAULT_PASSWORD=<generated-password>
MYSQL_ROOT_PASSWORD=<generated-password>
```

### 2. Use Secrets (Production)

```yaml
# docker-compose.prod.yml
services:
  backend:
    secrets:
      - db_password
      - jwt_secret

secrets:
  db_password:
    external: true
  jwt_secret:
    external: true
```

### 3. Limit Container Privileges

```yaml
# Already configured in docker-compose.yml
security_opt:
  - no-new-privileges:true
read_only: true
user: "1001:1001"
```

---

## 📊 Performance Tuning

### Backend

```yaml
# docker-compose.yml - Backend environment
JAVA_OPTS: >
  -Xms1024m
  -Xmx2048m
  -XX:+UseG1GC
  -XX:MaxGCPauseMillis=200
  -XX:+UseContainerSupport
  -XX:MaxRAMPercentage=75.0
```

### MySQL

```yaml
# docker-compose.yml - MySQL command
command:
  - --max-connections=500
  - --innodb-buffer-pool-size=1G
  - --innodb-log-file-size=256M
```

---

## 🚀 Production Deployment

### 1. Use Production Compose File

```bash
# Create docker-compose.prod.yml with:
# - No exposed MySQL port
# - Health checks
# - Restart policies
# - Resource limits
# - Logging configuration

docker-compose -f docker-compose.prod.yml up -d
```

### 2. Enable HTTPS

```yaml
# Add nginx reverse proxy
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
```

### 3. Setup Monitoring

```yaml
# Add Prometheus + Grafana
services:
  prometheus:
    image: prom/prometheus

  grafana:
    image: grafana/grafana
```

---

## 📦 Backup & Restore

### Backup

```bash
# Backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)

# Backup database
docker exec readyroad-mysql mysqldump \
  -u haydar -pHh06101987@ readyroad_prod \
  > backup_${DATE}.sql

# Backup volumes
docker run --rm \
  -v readyroad_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/mysql_data_${DATE}.tar.gz -C /data .
```

### Restore

```bash
# Restore database
docker exec -i readyroad-mysql mysql \
  -u haydar -pHh06101987@ readyroad_prod \
  < backup_20260206_143000.sql

# Restore volumes
docker run --rm \
  -v readyroad_mysql_data:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/mysql_data_20260206_143000.tar.gz -C /data
```

---

## ✅ Verification Checklist

```bash
# 1. Containers running
docker-compose ps
# ✅ mysql: Up (healthy)
# ✅ backend: Up (healthy)

# 2. Backend responds
curl http://localhost:8890/actuator/health
# ✅ {"status":"UP"}

# 3. Database accessible
docker exec readyroad-mysql mysql -u haydar -pHh06101987@ -e "SHOW DATABASES;"
# ✅ readyroad_prod listed

# 4. API works
curl http://localhost:8890/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"YourPassword"}'
# ✅ Returns JWT token

# 5. Logs clean
docker-compose logs backend | grep ERROR
# ✅ No critical errors
```

---

## 🎉 Quick Reference

| Task | Command |
|------|---------|
| Start | `docker-compose up -d` |
| Stop | `docker-compose stop` |
| Logs | `docker-compose logs -f` |
| Restart | `docker-compose restart` |
| Clean | `docker-compose down -v` |
| Build | `docker-compose build` |
| Status | `docker-compose ps` |
| DB Shell | `docker exec -it readyroad-mysql mysql -u haydar -p` |
| Backend Shell | `docker exec -it readyroad-backend sh` |

---

**Ready to deploy! 🚀**
