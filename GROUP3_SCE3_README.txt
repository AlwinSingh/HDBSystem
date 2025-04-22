# HDB BTO Management System CLI

A **Java-based command-line application** that simulates the HDB BTO (Build‑To‑Order) process for Applicants, Officers, and Managers. Data persistence is handled via CSV files, and business logic is cleanly separated using interfaces and constructor-based dependency injection.

---

## 🚀 Features

- **Applicant Dashboard**
  - Browse eligible projects
  - Submit, view, and withdraw applications
  - Pay invoices and view receipts
  - Submit and track feedback & enquiries

- **Officer Dashboard**
  - View and configure project locations & amenities
  - Handle booking and registration requests
  - Process enquiries and generate receipts

- **Manager Dashboard**
  - Create, edit, delete, and toggle visibility of BTO projects
  - Review officer and applicant registrations
  - Generate booking & feedback reports with filters

- **CSV Persistence Layer**
  - All data stored in CSV (e.g., `ProjectList.csv`, `ApplicantList.csv`, etc.)
  - Utility mappers for reading, writing, and updating individual records

- **Modular Architecture**
  - Service interfaces (`src.interfaces.*`) and concrete implementations (`src.service.*`)
  - Separation of concerns: Menu, Services, Models, CSV Mappers
  - Easy to swap or extend any service via constructor injection

---

## 🏗️ Architecture Overview

```
src/
├── interfaces/      # Service & repository contracts (e.g., IApplicantApplicationService, IProjectRepository)
├── repositories/    # CSV-based repository implementations
├── model/           # Domain models (Applicant, Project, Application, Invoice, etc.)
├── service/         # Business logic & CLI menus (ApplicantMenu, OfficerMenu)
└── util/            # Shared utilities (e.g., FilePath definitions)
```

- **Dependency Injection**: All services are injected into the menu classes via constructor, ensuring loose coupling and easier testing.
- **CSV Mappers**: Each entity has a dedicated `*CsvMapper` for reading/writing. They support per-record `append()` and `update()` operations.

---

## ⚙️ Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven or Gradle (optional, for build automation)
- PLEASE EXTRACT THE PROJECT TO DESKTOP
- RUN INDEX.HTML for JAVADOC DISPLAY
---

---

## 🛠️ Configuration

- **CSV file paths** are defined in `src/util/FilePath.java`. Update them if you move or rename your data files.
- **Service wiring** lives in `src/Main.java`. Swap in any custom implementation by changing the `new ...Service(...)` calls.

---