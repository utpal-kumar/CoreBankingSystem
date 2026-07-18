# Core Banking System (CBS)

A full, runnable Core Banking System built from the CBS specification
(`CBS Features.txt`, `Full db code blue print.txt`).

It is a **Spring Boot (Java 17) monolithic backend** backed by an **Oracle database**
(user `UTPAL`) and a **React frontend** (no build step — plain `index.html`).

> Connection details live in `backend/src/main/resources/application.properties`.
> Adjust `spring.datasource.url` to match your Oracle SID/service name.

Banking modules implemented:

| Module | Notes |
| --- | --- |
| Customer (CIF) | Onboarding, KYC status, risk profile, type (individual/corporate) |
| Account Management | Savings / Current / FD / RD, balances, multi-currency, optimistic locking |
| Transaction Engine | Deposit, withdrawal, internal transfer (with rollback), reversal |
| Double-Entry Ledger (GL) | Every transaction posts a DEBIT + CREDIT entry |
| Loan / Credit | Origination, EMI amortization, outstanding tracking, NPA flag |
| Security | JWT auth, Spring Security, RBAC (`ADMIN`, `USER`), BCrypt |
| Dashboard / Reporting | Totals for deposits, loans, customers, transactions |

## Project layout

```
Source/
├── backend/                 Spring Boot application
│   ├── pom.xml
│   └── src/main/java/com/cbs
│       ├── CbsApplication.java
│       ├── config/          Security, CORS, DataInitializer
│       ├── model/           Customer, Account, Transaction, Loan, LedgerEntry, User
│       ├── repository/      JPA repositories
│       ├── service/         Account, Transaction, Loan, Customer, Ledger, Auth, Dashboard
│       ├── controller/      REST controllers
│       ├── security/        JWT util, filter, UserDetails
│       └── common/          ApiResponse, GlobalExceptionHandler
└── frontend/
    └── index.html           React UI (open directly or serve statically)
```

## Prerequisites

- Java 17+
- Maven 3.8+ (to build/run the backend)
- A browser (frontend is a single static HTML file)

## Run the backend

```bash
cd Source\backend
mvn spring-boot:run
```

The API is available at `http://localhost:8080/api`.
Connect to Oracle via your client using user `UTPAL` / password `UtpalOracle123`.

## Run the frontend

Either open `Source\frontend\index.html` directly in a browser,
or serve the folder (e.g. `python -m http.server 3000` from `frontend/`)
and visit `http://localhost:3000`.

## Default users (seeded on first start)

| Username | Password   | Role  |
| -------- | ---------- | ----- |
| admin    | admin123   | ADMIN |
| teller   | teller123  | USER  |

## API quick reference

```
POST /api/auth/login            { "username", "password" } -> { token }
POST /api/auth/register         (ADMIN) create user

GET  /api/customers             list customers
POST /api/customers             create customer
POST /api/customers/{id}/accounts   open account for customer

GET  /api/accounts              list accounts
GET  /api/accounts/{id}

POST /api/transactions/transfer     { fromAccount, toAccount, amount }
POST /api/transactions/deposit      { accountNumber, amount }
POST /api/transactions/withdraw     { accountNumber, amount }
POST /api/transactions/reverse/{reference}
GET  /api/transactions/account/{accountId}

POST /api/loans                  originate loan
POST /api/loans/{id}/emi         pay EMI
GET  /api/loans/customer/{id}
GET  /api/loans/npa              (ADMIN) NPA loans

GET  /api/dashboard              summary metrics
GET  /api/ledger/balance/{gl}    GL balance
```

All protected endpoints require header `Authorization: Bearer <token>`.
Sample seeded accounts: `SAV1000001` (Alice, 5000.00) and `CUR2000001` (Bob Corp, 25000.00).

## Notes

This is a functional reference implementation. For a production bank you would, as the
blueprint notes, add the Saga pattern for distributed transactions, idempotency keys,
audit trails, Oracle RAC, and Kafka-based events. The schema now targets Oracle directly
(user `UTPAL`) via `ojdbc11` and `OracleDialect`.
