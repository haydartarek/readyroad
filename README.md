# Ready Road - Traffic Signs Learning Application

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Flutter](https://img.shields.io/badge/Flutter-3.27-02569B.svg)](https://flutter.dev)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> A comprehensive multilingual mobile application for learning traffic signs and preparing for driving theory exams.

---

## 🎯 Project Overview

**Ready Road** is a full-stack application designed to help users learn traffic signs and prepare for driving exams in multiple languages (Arabic, English, Dutch, French).

### Features
- 📚 **9 Categories** of traffic signs (A-M)
- 🌍 **Multilingual Support** (Arabic, English, Dutch, French)
- 📱 **Mobile App** (Flutter - Coming Soon)
- 🎓 **Practice Mode** for learning
- 📝 **Exam Mode** with randomized questions
- 📊 **Progress Tracking**
- 🔔 **Smart Notifications**
- 📴 **Offline-First** architecture

---

## 🏗️ Architecture

This project follows **Clean Architecture** principles:

```
Backend (Spring Boot)
├── Controller Layer    (REST APIs)
├── Service Layer       (Business Logic)
├── Domain Layer        (Entities & Repositories)
└── Data Layer          (MySQL + Flyway)

Mobile (Flutter - Coming Soon)
├── Presentation Layer  (UI + State Management)
├── Domain Layer        (Use Cases + Entities)
└── Data Layer          (API + Local Storage)
```

---

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.1
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Migration**: Flyway
- **Build Tool**: Maven
- **Security**: Spring Security

### Mobile (Phase 1)
- **Framework**: Flutter 3.27+
- **Language**: Dart
- **State Management**: Riverpod
- **Routing**: go_router
- **HTTP Client**: Dio
- **Localization**: easy_localization

---

## 📋 Project Status

### Phase 0: Foundation ✅ (Complete)
- [x] Backend Setup (Spring Boot + MySQL)
- [x] Clean Architecture Structure
- [x] Flyway Migrations
- [x] Base Entities (Category, TrafficSign)
- [x] REST APIs
- [x] Multilingual Support
- [x] Seed Data (9 categories, 18 signs)
- [ ] Flutter Setup (In Progress)

### Phase 1: MVP Core ⏳ (Next)
- [ ] Traffic Signs Content (all categories)
- [ ] Practice Mode
- [ ] Exam Mode
- [ ] Progress Tracking
- [ ] Smart Notifications

### Phase 2-5: Coming Soon
- Offline-First
- Adaptive Learning
- Freemium Model
- Advanced Analytics

---

## 🛠️ Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Flutter 3.27+ (for mobile)

### Backend Setup

#### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/readyroad.git
cd readyroad
```

#### 2. Configure MySQL
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/readyroad?createDatabaseIfNotExist=true
    username: root
    password: YOUR_PASSWORD
```

#### 3. Run the application
```bash
./mvnw spring-boot:run
```

Or use the quick start script:
```bash
START.bat
```

#### 4. Verify
Open: http://localhost:8888/api/health

---

## 📡 API Endpoints

### Health Check
```http
GET /api/health
```

### Categories
```http
GET /api/categories              # Get all categories
GET /api/categories/{code}       # Get category by code (A, B, C, etc.)
```

### Traffic Signs
```http
GET /api/traffic-signs                    # Get all signs
GET /api/traffic-signs/category/{id}      # Get signs by category
GET /api/traffic-signs/{signCode}         # Get sign by code
```

### Response Example
```json
{
  "id": 1,
  "code": "A",
  "nameAr": "إشارات التحذير",
  "nameEn": "Warning Signs",
  "nameNl": "Waarschuwingsborden",
  "nameFr": "Signaux d'avertissement",
  "displayOrder": 1
}
```

---

## 🗂️ Project Structure

```
readyroad/
├── src/main/java/com/readyroad/readyroadbackend/
│   ├── config/              # Security, CORS configurations
│   ├── controller/          # REST Controllers
│   ├── domain/
│   │   ├── entity/          # JPA Entities
│   │   └── repository/      # Data Repositories
│   ├── dto/response/        # API Response DTOs
│   ├── mapper/              # Entity-DTO Mappers
│   ├── service/             # Business Logic
│   └── ReadyroadApplication.java
├── src/main/resources/
│   ├── application.yml      # Configuration
│   └── db/migration/        # Flyway Migrations
├── docs/                    # Documentation
├── pom.xml                  # Maven dependencies
└── README.md
```

---

## 📊 Database Schema

### Categories Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary Key |
| code | VARCHAR(10) | Category code (A-M) |
| name_ar/en/nl/fr | TEXT | Multilingual names |
| description_ar/en/nl/fr | TEXT | Multilingual descriptions |
| display_order | INT | Display order |
| is_active | BOOLEAN | Active status |

### Traffic Signs Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary Key |
| category_id | BIGINT | Foreign Key to Categories |
| sign_code | VARCHAR(50) | Unique sign code |
| name_ar/en/nl/fr | TEXT | Multilingual names |
| description_ar/en/nl/fr | TEXT | Multilingual descriptions |
| image_url | VARCHAR(500) | Image URL |
| is_active | BOOLEAN | Active status |

---

## 🌍 Supported Languages

- 🇸🇦 **Arabic** (ar)
- 🇬🇧 **English** (en)
- 🇳🇱 **Dutch** (nl)
- 🇫🇷 **French** (fr)

---

## 📚 Categories

| Code | Arabic | English | Dutch | French |
|------|--------|---------|-------|--------|
| A | إشارات التحذير | Warning Signs | Waarschuwingsborden | Signaux d'avertissement |
| B | إشارات الأولوية | Priority Signs | Voorrangsborden | Signaux de priorité |
| C | إشارات المنع | Prohibition Signs | Verbodsborden | Signaux d'interdiction |
| D | إشارات الإلزام | Mandatory Signs | Gebodsborden | Signaux d'obligation |
| E | إشارات الوقوف | Parking Signs | Parkeren | Signaux de stationnement |
| F | إشارات الإرشاد | Direction Signs | Richtingsborden | Signaux de direction |
| G | إشارات إضافية | Additional Signs | Onderborden | Signaux additionnels |
| Z | إشارات المناطق | Zone Signs | Zoneborden | Signaux de zone |
| M | علامات الطريق | Road Markings | Wegmarkeringen | Marquages routiers |

---

## 🤝 Contributing

This project follows a strict phase-based development approach. Please read `PHASES.md` and `SMART_ASSISTANT_CONTRACT.md` before contributing.

### Development Rules
- ✅ Follow Clean Architecture
- ✅ No phase skipping
- ✅ Multilingual from day one
- ❌ No Firebase as database
- ❌ No business logic in mobile app

---

## 📝 Documentation

- **Phase Plan**: `PHASES.md`
- **Development Contract**: `SMART_ASSISTANT_CONTRACT.md`
- **Database Setup**: `DATABASE_SETUP.md`
- **Flutter Setup**: `FLUTTER_SETUP_GUIDE.md`
- **Phase 0 Status**: `PHASE_0_COMPLETE.md`

---

## 🔧 Troubleshooting

### Port already in use
Change the port in `application.yml`:
```yaml
server:
  port: 8888  # Change to any available port
```

### MySQL Connection Failed
1. Verify MySQL is running
2. Check credentials in `application.yml`
3. Ensure database `readyroad` exists

### Flyway Migration Failed
```sql
DROP DATABASE IF EXISTS readyroad;
CREATE DATABASE readyroad;
```
Then restart the application.

---

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Authors

- **Development Team** - Initial work

---

## 🙏 Acknowledgments

- Spring Boot Team
- Flutter Team
- MySQL Community
- All contributors

---

## 📞 Contact

For questions or support, please open an issue on GitHub.

---

**Made with ❤️ for safer roads**

---

## 📈 Roadmap

- [x] Phase 0: Foundation (Backend)
- [ ] Phase 0: Foundation (Mobile)
- [ ] Phase 1: MVP Core
- [ ] Phase 2: Offline-First
- [ ] Phase 3: Adaptive Learning
- [ ] Phase 4: Growth & Monetization
- [ ] Phase 5: Expansion

---

**Status**: 🚧 In Development | **Current Phase**: Phase 0 (Backend Complete ✅)

