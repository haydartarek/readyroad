# Project Phases Execution Plan

## Binding Reference
This document is **strictly governed** by the rules defined in:

➡️ `SMART_ASSISTANT_CONTRACT.md`

All phases, tasks, and decisions described here **must comply fully** with the contract.
If a conflict exists, the contract **always takes precedence**.

---

## Phase 0: Foundation (Mandatory)

### Objective
Establish a clean, stable, and production-ready foundation for both mobile and backend systems.

### Scope
- Flutter project initialized (single codebase)
- Android + iOS support verified
- Feature-based clean architecture
- `easy_localization` configured (ar, en, nl, fr)
- Light/Dark theme support
- Networking layer (Dio)
- State management (Riverpod)
- Routing (go_router)

**Backend**
- Spring Boot initialized
- MySQL connected
- Flyway migrations enabled
- Base entities and repositories
- Health endpoint (`/api/health`)
- Initial content endpoint (`/api/lessons`)
- Seed data for development

### Deliverables
- Mobile app runs on Android and iOS
- Backend runs locally without errors
- Mobile app successfully fetches data from backend
- No hard-coded UI strings
- No Firebase database usage

### Exit Criteria
- Phase 0 is considered complete only when:
    - Mobile + backend work end-to-end
    - No architectural violations exist
    - Contract rules are respected

---

## Phase 1: MVP Core (Store-Ready)

### Objective
Deliver a complete MVP experience that can be published to app stores.

### Scope

#### Traffic Sign Content
- Categories:
    - A, B, C, D, E, F, G, Z, M
- Sign list per category
- Sign details with explanations

#### Practice Mode
- Category selection
- Difficulty levels
- Random question selection
- Instant feedback with explanations

#### Exam Mode
- Category Exam:
    - 15 random questions per category
- Full Exam:
    - 50 random questions
    - Intelligent category distribution
- Pass / Fail result
- Mistake review

#### Progress Tracking
- User statistics
- Weak category detection
- Best scores

#### Smart Notifications (v1)
- Inactivity reminder
- Weak topic alert
- Quick review suggestion
- Maximum one notification per day

### Deliverables
- Fully functional MVP
- End-to-end user flow
- Internal testing build for:
    - Google Play
    - TestFlight (iOS)

### Exit Criteria
- All MVP features work on Android and iOS
- No fixed questions or exams
- All exam logic is server-side
- Firebase used only for allowed services

---

## Phase 2: Offline-First & Content Versioning

### Objective
Enable strong offline usage with safe synchronization.

### Scope
- Initial core content download
- Offline practice and exams
- Local data storage
- Deferred progress synchronization
- Content versioning system
- Safe content updates

### Deliverables
- App usable without internet
- No data loss on reconnect
- Versioned question bank

### Exit Criteria
- Offline mode fully functional
- Content updates do not break existing data
- Sync logic is stable

---

## Phase 3: Adaptive Learning & Advanced Analytics

### Objective
Transform the app into an intelligent learning assistant.

### Scope
- Adaptive question selection
- Weighted random logic
- Spaced repetition scheduling
- Advanced statistics:
    - Time per question
    - Study time patterns
    - Anonymous comparison to averages

### Deliverables
- Personalized learning experience
- Measurable improvement in user performance

### Exit Criteria
- Adaptive logic proven with real usage data
- No impact on exam realism

---

## Phase 4: Growth & Monetization

### Objective
Enable sustainable growth and monetization.

### Scope
- Freemium model
- Premium feature unlocks
- Referral system
- Social proof indicators
- Feature flags / A-B testing support

### Deliverables
- Revenue-ready application
- Organic growth mechanisms

### Exit Criteria
- Monetization logic isolated and optional
- Free users retain full learning integrity

---

## Phase 5: Expansion & Administration

### Objective
Enable large-scale content management and expansion.

### Scope
- Scenario-based questions (real-world images)
- Admin panel for content management
- Question bank management
- Optional web version (admin-focused)

### Deliverables
- Admin-controlled content updates
- Expanded learning modes

### Exit Criteria
- Content updates no longer require app rebuilds
- Admin panel secured and stable

---

## Global Rules (Inherited from Contract)
- No phase skipping
- No deviation from architecture
- No client-side exam logic
- No Firebase database usage
- No breaking changes to working code
- Every phase must be fully closed before the next begins

---

## Final Note
This file, together with `SMART_ASSISTANT_CONTRACT.md`, forms the **only valid execution framework** for this project.

Any work performed outside these documents is considered invalid.
