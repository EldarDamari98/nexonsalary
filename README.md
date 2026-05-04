# NexonSalary

A full-stack commission management system for pension and insurance agents. It imports monthly balance data from Excel files, tracks client movements between agents, and automatically calculates commissions with clawback rules.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | React 19, React Router 7, Recharts, Vite |
| Backend | Java 17, Jersey 3 (JAX-RS REST), Grizzly HTTP Server |
| ORM | Hibernate 6 / JPA |
| Database | MySQL |
| File Processing | Apache POI (Excel `.xlsx`) |
| Build | Maven |

---

## Project Structure

```
nexonsalary-2/
├── backend/
│   └── src/main/java/com/nexonsalary/
│       ├── Main.java                     # Entry point — starts Grizzly on port 8081
│       ├── config/                       # CORS filter, Jersey app config
│       ├── controller/                   # REST endpoints (JAX-RS resources)
│       │   ├── AgentController
│       │   ├── BalanceImportController
│       │   ├── CommissionController
│       │   ├── DashboardController
│       │   └── StatisticsController
│       ├── service/                      # Business logic
│       │   ├── CommissionCalculationService
│       │   ├── CommissionQueryService
│       │   ├── ExcelImportService
│       │   ├── MonthlyBalanceService
│       │   ├── handler/                  # Commission handler strategy pattern
│       │   │   ├── CommissionHandler     (interface)
│       │   │   ├── AbstractCommissionHandler
│       │   │   ├── NewClientHandler
│       │   │   ├── ExistingClientHandler
│       │   │   ├── AgentTransferHandler
│       │   │   └── ClientLeftHandler
│       │   └── CommissionRates.java      # Centralized rate constants
│       ├── model/                        # JPA entities
│       │   ├── BaseEntity.java           # Abstract base — id, createdAt
│       │   ├── Agent.java
│       │   ├── Member.java
│       │   ├── MemberAccount.java
│       │   ├── MonthlyMemberBalance.java
│       │   ├── CommissionTransaction.java
│       │   ├── ClientAgentHistory.java
│       │   └── BalanceUpload.java
│       ├── dto/                          # Data Transfer Objects (API shapes)
│       └── util/                         # HibernateUtil, DbConnectionUtil
└── frontend/
    └── src/
        ├── App.jsx                       # Root — login guard + routing
        ├── pages/
        │   ├── LoginPage.jsx
        │   ├── DashboardPage.jsx
        │   ├── AgentsPage.jsx
        │   ├── BalancesPage.jsx
        │   ├── BalanceImportPage.jsx
        │   ├── CommissionsPage.jsx
        │   └── StatisticsPage.jsx
        ├── components/layout/            # AppLayout, Sidebar, Topbar
        ├── api/                          # API call modules per domain
        └── styles/app.css
```

---

## Architecture

```
React Frontend  (port 5173)
       ↕  HTTP REST / JSON
Java Backend    (port 8081)
       ↕  Hibernate ORM
MySQL Database  (nexonsalary)
```

The backend is a stateless REST API. Every request is processed independently — no server-side sessions. The frontend stores login state in `localStorage`.

---

## Data Model

```
AGENT ──────────────────────────────────┐
  agentCode (unique), agentName, active │
                                        │ manages
MEMBER ─────────────────────┐           │
  nationalId (unique),       │ owns      │
  fullName                   ↓           ↓
                        MEMBER_ACCOUNT
                          accountNumber (unique)
                               │
               ┌───────────────┴───────────────┐
               ↓                               ↓
  MONTHLY_MEMBER_BALANCE          CLIENT_AGENT_HISTORY
  (imported monthly data)         (tenure tracking for clawback)
               │
               ↓
  COMMISSION_TRANSACTION
  (calculated output per month)

  BALANCE_UPLOAD
  (audit trail of each Excel import)
```

### Key Relationships
- One `Member` can have many `MemberAccount`s (e.g. pension + insurance)
- One `MemberAccount` has one balance record per month
- `ClientAgentHistory` tracks how long a client has been with an agent — used to calculate clawback penalties

---

## OOP Design

### Inheritance — `BaseEntity`
All entities extend `BaseEntity`, which provides the shared `id` and `createdAt` fields via `@MappedSuperclass`:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }
}
```

`ClientAgentHistory` overrides `prePersist()` to also set `updatedAt`, demonstrating method overriding.

### Polymorphism — Strategy Pattern (Commission Handlers)
Commission calculation uses the Strategy Pattern. Each client scenario is a separate class:

```
CommissionHandler (interface)
    └── AbstractCommissionHandler (shared helpers)
            ├── NewClientHandler       → perimeter fee + trail commission
            ├── ExistingClientHandler  → trail commission only
            ├── AgentTransferHandler   → clawback old agent + new client logic
            └── ClientLeftHandler      → clawback if tenure < 24 months
```

The service picks the right handler at runtime via `resolveHandler()` — the calling code never knows which concrete type it uses.

---

## Commission Rules

| Rate | Value | Applied When |
|------|-------|--------------|
| Perimeter Fee | 0.3% | New client joins, or balance increases |
| Trail Commission | 0.025% | Every month on the full balance |
| Clawback < 12 months | 50% of perimeter fees paid | Client leaves within 12 months |
| Clawback 12–24 months | 25% of perimeter fees paid | Client leaves between 12–24 months |

---

## Running Locally

### Prerequisites
- Java 17
- Maven
- Node.js 18+
- MySQL (database named `nexonsalary`)

### Backend
```bash
cd backend
JAVA_HOME=/opt/homebrew/opt/openjdk@17 mvn compile exec:java -Dexec.mainClass=com.nexonsalary.Main
```
Server starts at `http://localhost:8081`. Hibernate auto-creates/updates tables on startup.

### Frontend
```bash
cd frontend
npm install
npm run dev
```
App available at `http://localhost:5173`.

---

## Login

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin123` |

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/dashboard/summary` | Key metrics overview |
| GET | `/agents` | List all agents |
| GET | `/balances` | Paginated balance records |
| POST | `/balances/upload` | Import Excel file |
| GET | `/balances/uploads` | Import history |
| DELETE | `/balances/uploads/{id}` | Delete an upload |
| POST | `/commissions/calculate` | Calculate commissions for a month |
| POST | `/commissions/recalculate` | Delete and recalculate |
| GET | `/commissions/summary` | Commission breakdown |
| GET | `/commissions/transactions` | Paginated transaction list |
| GET | `/statistics/overview` | Statistics summary |
| GET | `/statistics/assets-trend` | Time-series asset data |
| GET | `/statistics/commission-trend` | Time-series commission data |
| GET | `/statistics/top-agents` | Top agents by commission |
| GET | `/statistics/reason-breakdown` | Commission reason distribution |
| GET | `/statistics/client-movement` | New vs. lost clients per month |
