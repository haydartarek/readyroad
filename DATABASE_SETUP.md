ا# Database Setup Guide

## Phase 0: MySQL Configuration

### Prerequisites
You need **MySQL Server** installed and running on your machine.

---

## Option 1: Quick Setup (Recommended)

### Step 1: Install MySQL
If you don't have MySQL installed:
- **Windows**: Download from https://dev.mysql.com/downloads/installer/
- Or use **XAMPP** which includes MySQL

### Step 2: Start MySQL Service
- **XAMPP**: Open XAMPP Control Panel → Start MySQL
- **Windows Service**: Open Services → Start "MySQL" service

### Step 3: Create Database User
Open MySQL command line or phpMyAdmin and run:

```sql
-- Option A: Use root with empty password (for development only)
ALTER USER 'root'@'localhost' IDENTIFIED BY '';
FLUSH PRIVILEGES;

-- Option B: Create a new user (recommended)
CREATE USER 'readyroad'@'localhost' IDENTIFIED BY 'readyroad123';
GRANT ALL PRIVILEGES ON *.* TO 'readyroad'@'localhost';
FLUSH PRIVILEGES;
```

### Step 4: Update application.yml
Edit: `src/main/resources/application.yml`

**If using root with no password:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/readyroad_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: 
```

**If using custom user:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/readyroad_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: readyroad
    password: readyroad123
```

---

## Option 2: Using Docker (Alternative)

If you have Docker installed:

```bash
docker run --name readyroad-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=readyroad_db \
  -p 3306:3306 \
  -d mysql:8.0
```

Then keep the current `application.yml` settings (username: root, password: root).

---

## Testing the Connection

After configuring MySQL, test the connection:

```bash
mysql -u root -p
# or
mysql -u readyroad -p
```

If successful, run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

---

## Current Configuration

The application is currently configured with:
- **Host**: localhost:3306
- **Username**: root
- **Password**: root
- **Database**: readyroad_db (auto-created)

---

## What Happens Next?

When you start the application:
1. **Flyway** will automatically create the database if it doesn't exist
2. Migrations in `src/main/resources/db/migration/` will run:
   - `V1__Create_Base_Tables.sql` → Creates tables
   - `V2__Seed_Initial_Data.sql` → Inserts sample data
3. Application will start on **http://localhost:8080**

---

## Endpoints Available After Startup

- **Health Check**: http://localhost:8080/api/health
- **Categories**: http://localhost:8080/api/categories
- **Traffic Signs**: http://localhost:8080/api/traffic-signs

---

## Need Help?

1. Check if MySQL is running: `mysqladmin -u root -p status`
2. Check MySQL port: Make sure port 3306 is not in use
3. Reset root password if needed

