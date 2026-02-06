# Database Setup Guide

## Problem
```
Access denied for user 'haydar'@'localhost' to database 'readyroad'
```

User `haydar` exists but doesn't have permissions on `readyroad` database.

---

## Solution 1: Using MySQL CLI (Recommended)

### Step 1: Connect to MySQL as root
```bash
mysql -u root -p
```
Enter your root password when prompted.

### Step 2: Create database and grant permissions
```sql
-- Create database with UTF-8 support
CREATE DATABASE IF NOT EXISTS readyroad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Grant all privileges to haydar
GRANT ALL PRIVILEGES ON readyroad.* TO 'haydar'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;

-- Verify
SHOW GRANTS FOR 'haydar'@'localhost';

-- Exit
exit;
```

### Step 3: Test connection
```bash
mysql -u haydar -p readyroad
```
Enter password: `Hh06101987@`

If you can connect successfully, you're ready to run the application!

---

## Solution 2: Using PowerShell Script

```powershell
# Run the provided script
./setup-database.ps1
```

This will:
- ✅ Create `readyroad` database
- ✅ Grant permissions to user `haydar`
- ✅ Set character encoding to UTF-8
- ✅ Verify the setup

---

## Solution 3: Using SQL File Directly

```bash
# Execute the setup script
mysql -u root -p < setup-database.sql
```

---

## Verification Steps

### 1. Check if database exists
```bash
mysql -u root -p -e "SHOW DATABASES LIKE 'readyroad';"
```

Expected output:
```
+---------------------+
| Database (readyroad)|
+---------------------+
| readyroad           |
+---------------------+
```

### 2. Check user permissions
```bash
mysql -u root -p -e "SHOW GRANTS FOR 'haydar'@'localhost';"
```

Expected output should include:
```
GRANT ALL PRIVILEGES ON `readyroad`.* TO `haydar`@`localhost`
```

### 3. Test connection as haydar
```bash
mysql -u haydar -pHh06101987@ -e "USE readyroad; SHOW TABLES;"
```

If this works without errors, the setup is complete!

---

## Start Application

After database setup is complete:

```bash
# Clean and rebuild
./mvnw clean package -DskipTests

# Start with secure profile
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=secure"
```

---

## Troubleshooting

### Error: "Access denied for user 'haydar'@'localhost'"
**Cause:** User doesn't have permissions on `readyroad` database
**Solution:** Run the GRANT commands above as root

### Error: "Unknown database 'readyroad'"
**Cause:** Database doesn't exist
**Solution:** Run `CREATE DATABASE readyroad;` as root

### Error: "Can't connect to MySQL server"
**Cause:** MySQL service is not running
**Solution:**
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo service mysql start
```

---

## Summary

✅ **Database:** readyroad
✅ **User:** haydar
✅ **Password:** Hh06101987@
✅ **Charset:** utf8mb4_unicode_ci
✅ **Permissions:** ALL PRIVILEGES on readyroad.*

After completing these steps, your application should start successfully! 🚀
