# 📡 Ready Road REST API Documentation

## Base URL
```
http://localhost:8888/api
```

## Authentication
Currently, all endpoints are **publicly accessible** (no authentication required for development).

---

## 📋 Categories API

### Get All Categories
Retrieve all active traffic sign categories.

**Endpoint:** `GET /categories`

**Response:**
```json
[
  {
    "id": 1,
    "code": "A",
    "nameAr": "علامات الخطر",
    "nameEn": "Danger Signs",
    "nameNl": "Gevaar",
    "nameFr": "Danger",
    "descriptionAr": "علامات تحذيرية للإشارة إلى المخاطر على الطريق",
    "descriptionEn": "Warning signs indicating road hazards",
    "descriptionNl": "Waarschuwingsborden voor gevaren op de weg",
    "descriptionFr": "Panneaux d'avertissement des dangers sur la route",
    "displayOrder": 1
  }
]
```

**Status Codes:**
- `200 OK` - Success

**Example:**
```bash
curl http://localhost:8888/api/categories
```

---

### Get Category by Code
Retrieve a specific category by its code.

**Endpoint:** `GET /categories/{code}`

**Parameters:**
- `code` (path) - Category code (A, B, C, D, E, F, G, M, Z)

**Response:**
```json
{
  "id": 1,
  "code": "A",
  "nameAr": "علامات الخطر",
  "nameEn": "Danger Signs",
  "nameNl": "Gevaar",
  "nameFr": "Danger",
  "descriptionAr": "علامات تحذيرية للإشارة إلى المخاطر على الطريق",
  "descriptionEn": "Warning signs indicating road hazards",
  "descriptionNl": "Waarschuwingsborden voor gevaren op de weg",
  "descriptionFr": "Panneaux d'avertissement des dangers sur la route",
  "displayOrder": 1
}
```

**Status Codes:**
- `200 OK` - Success
- `404 Not Found` - Category not found

**Example:**
```bash
curl http://localhost:8888/api/categories/A
```

---

## 🚦 Traffic Signs API

### Get All Traffic Signs
Retrieve all active traffic signs.

**Endpoint:** `GET /traffic-signs`

**Response:**
```json
[
  {
    "id": 50,
    "signCode": "A11",
    "categoryCode": "A",
    "nameAr": "طريق يؤدي إلى رصيف أو شاطئ",
    "nameEn": "Road leads to quay or waterside",
    "nameNl": "Uitweg op kaai of oever.",
    "nameFr": "Route menant au quai ou à la rive",
    "descriptionAr": "طريق يؤدي إلى رصيف أو شاطئ",
    "descriptionEn": "Road leads to quay or waterside",
    "descriptionNl": "Uitweg op kaai of oever.",
    "descriptionFr": "Route menant au quai ou à la rive",
    "imageUrl": "assets/traffic_signs/danger_signs/A11.png"
  }
]
```

**Status Codes:**
- `200 OK` - Success

**Example:**
```bash
curl http://localhost:8888/api/traffic-signs
```

---

### Get Traffic Signs by Category
Retrieve all traffic signs in a specific category.

**Endpoint:** `GET /traffic-signs/category/{categoryId}`

**Parameters:**
- `categoryId` (path) - Category ID (integer)

**Response:**
```json
[
  {
    "id": 50,
    "signCode": "A11",
    "categoryCode": "A",
    "nameAr": "طريق يؤدي إلى رصيف أو شاطئ",
    "nameEn": "Road leads to quay or waterside",
    "nameNl": "Uitweg op kaai of oever.",
    "nameFr": "Route menant au quai ou à la rive",
    "descriptionAr": "طريق يؤدي إلى رصيف أو شاطئ",
    "descriptionEn": "Road leads to quay or waterside",
    "descriptionNl": "Uitweg op kaai of oever.",
    "descriptionFr": "Route menant au quai ou à la rive",
    "imageUrl": "assets/traffic_signs/danger_signs/A11.png"
  }
]
```

**Status Codes:**
- `200 OK` - Success
- `404 Not Found` - Category not found

**Example:**
```bash
curl http://localhost:8888/api/traffic-signs/category/1
```

---

### Get Traffic Sign by Code
Retrieve a specific traffic sign by its code.

**Endpoint:** `GET /traffic-signs/{signCode}`

**Parameters:**
- `signCode` (path) - Sign code (e.g., A11, B1, C3)

**Response:**
```json
{
  "id": 50,
  "signCode": "A11",
  "categoryCode": "A",
  "nameAr": "طريق يؤدي إلى رصيف أو شاطئ",
  "nameEn": "Road leads to quay or waterside",
  "nameNl": "Uitweg op kaai of oever.",
  "nameFr": "Route menant au quai ou à la rive",
  "descriptionAr": "طريق يؤدي إلى رصيف أو شاطئ",
  "descriptionEn": "Road leads to quay or waterside",
  "descriptionNl": "Uitweg op kaai of oever.",
  "descriptionFr": "Route menant au quai ou à la rive",
  "imageUrl": "assets/traffic_signs/danger_signs/A11.png"
}
```

**Status Codes:**
- `200 OK` - Success
- `404 Not Found` - Sign not found

**Example:**
```bash
curl http://localhost:8888/api/traffic-signs/A11
```

---

### Search Traffic Signs ✨ NEW
Search for traffic signs across all fields (code, names, descriptions) in all 4 languages.

**Endpoint:** `GET /traffic-signs/search`

**Parameters:**
- `q` (query) - Search term (string)

**Response:**
```json
[
  {
    "id": 50,
    "signCode": "A11",
    "categoryCode": "A",
    "nameAr": "طريق يؤدي إلى رصيف أو شاطئ",
    "nameEn": "Road leads to quay or waterside",
    "nameNl": "Uitweg op kaai of oever.",
    "nameFr": "Route menant au quai ou à la rive",
    "descriptionAr": "طريق يؤدي إلى رصيف أو شاطئ",
    "descriptionEn": "Road leads to quay or waterside",
    "descriptionNl": "Uitweg op kaai of oever.",
    "descriptionFr": "Route menant au quai ou à la rive",
    "imageUrl": "assets/traffic_signs/danger_signs/A11.png"
  }
]
```

**Search Behavior:**
- Empty query returns all signs
- Case-insensitive search
- Searches across: sign code, all names (ar/en/nl/fr), all descriptions (ar/en/nl/fr)
- Partial match supported

**Status Codes:**
- `200 OK` - Success (may return empty array)

**Examples:**
```bash
# Search by English name
curl "http://localhost:8888/api/traffic-signs/search?q=danger"

# Search by Arabic name
curl "http://localhost:8888/api/traffic-signs/search?q=خطر"

# Search by sign code
curl "http://localhost:8888/api/traffic-signs/search?q=A11"

# Search by partial text
curl "http://localhost:8888/api/traffic-signs/search?q=road"
```

---

## 🏥 Health Check

### Get Health Status
Check if the API is running.

**Endpoint:** `GET /health`

**Response:**
```json
{
  "status": "UP",
  "message": "ReadyRoad Backend is running",
  "timestamp": "2026-01-14T15:00:00"
}
```

**Status Codes:**
- `200 OK` - Service is healthy

**Example:**
```bash
curl http://localhost:8888/api/health
```

---

## 📊 Database Statistics

### Current Data (as of January 14, 2026)
- **Total Categories:** 9 (A, B, C, D, E, F, G, M, Z)
- **Total Traffic Signs:** 215 (unique, no duplicates)
- **Languages Supported:** 4 (Arabic, English, Dutch, French)
- **Images Available:** 194 PNG files

### Category Distribution
| Code | Name (EN) | Signs Count |
|------|-----------|-------------|
| A | Danger Signs | 33 |
| B | Priority Signs | 17 |
| C | Prohibition Signs | 31 |
| D | Mandatory Signs | 18 |
| E | Parking Signs | 16 |
| F | Information Signs | 74 |
| G | Additional Signs | 2 |
| M | Bicycle Signs | 22 |
| Z | Zone Signs | 2 |

---

## 🔧 Development Notes

### CORS Configuration
- Allows origins: `http://localhost:3000`, `http://localhost:8080`
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: Supported

### Response Format
All responses are in **JSON** format with UTF-8 encoding to support multilingual content.

### Error Handling
Errors return appropriate HTTP status codes with error messages in JSON format:
```json
{
  "error": "Error message here"
}
```

---

## 🚀 Quick Start Testing

### Using PowerShell (Windows)
```powershell
# Get all categories
Invoke-RestMethod -Uri "http://localhost:8888/api/categories" -Method Get

# Get all traffic signs
Invoke-RestMethod -Uri "http://localhost:8888/api/traffic-signs" -Method Get

# Search for signs
Invoke-RestMethod -Uri "http://localhost:8888/api/traffic-signs/search?q=danger" -Method Get

# Get specific sign
Invoke-RestMethod -Uri "http://localhost:8888/api/traffic-signs/A11" -Method Get
```

### Using curl (Linux/Mac)
```bash
# Get all categories
curl http://localhost:8888/api/categories

# Get all traffic signs
curl http://localhost:8888/api/traffic-signs

# Search for signs
curl "http://localhost:8888/api/traffic-signs/search?q=danger"

# Get specific sign
curl http://localhost:8888/api/traffic-signs/A11
```

---

## 📝 Notes
- All endpoints return active records only (`isActive = true`)
- Timestamps are in ISO 8601 format with UTC+1 timezone
- Image URLs are relative paths to assets folder
- Multilingual support: All text fields available in 4 languages

---

**Last Updated:** January 14, 2026  
**API Version:** 1.0  
**Backend:** Spring Boot 4.0.1 + MySQL 8.0
