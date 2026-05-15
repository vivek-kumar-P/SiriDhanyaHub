# Siri-Dhanya Hub PRD / Requirements Document Draft

## Document Control

- Document Title: Siri-Dhanya Hub Product Requirements Document
- Project Title: Android App Development using GenAI - Siri-Dhanya Hub (Agriculture)
- Project ID: 74
- Version: 1.0-draft
- Author: Vivek Kumar P
- Reviewer / Mentor: Prof. S. Asma Begum
- Organization: MindMatrix Pvt. Ltd.
- Submission Date: [Fill before export]

## 1. Introduction

### 1.1 Purpose

This document defines the requirements, user flows, architecture references, and acceptance expectations for the Siri-Dhanya Hub Android application developed under MindMatrix Project 74.

### 1.2 Scope

Siri-Dhanya Hub is a role-based Android application for millet ecosystem support in Karnataka. It is intended to provide:

- price-awareness support for farmers
- recipe and health-awareness support for consumers
- role-based onboarding and account setup
- request/listing workflows for buyer-producer interaction

### 1.3 Intended Audience

- MindMatrix project evaluators
- internship mentors
- academic reviewers
- developers maintaining or extending the application

### 1.4 Definitions

- Siri-Dhanya: locally used term associated with millets
- FPO: Farmer Producer Organization
- PRD: Product Requirements Document
- MVVM: Model-View-ViewModel
- FR: Functional Requirement
- NFR: Non-Functional Requirement

## 2. Problem Statement and Background

Karnataka is a major millet-producing state, but two challenges remain visible:

- many small farmers lack easy access to comparable market-rate visibility across cities
- many consumers lack awareness of millet recipes, benefits, and practical usage

Project 74 defines Siri-Dhanya Hub as a dual-persona agriculture application that should bridge information gaps for both sides of the millet value chain.

## 3. Product Vision

Siri-Dhanya Hub is envisioned as a millet-focused Android application that helps:

- farmers understand market options and publish availability
- consumers discover millet recipes, health facts, and buying/request pathways
- both user groups operate through profile-based onboarding and tailored navigation

## 4. Stakeholders and Personas

### 4.1 Stakeholders

- Student developer
- MindMatrix evaluators and mentors
- City Engineering College reviewers
- Farmer users
- Consumer users

### 4.2 Persona: Farmer

- wants visibility into millet prices across markets
- wants to publish available stock
- wants better access to consumer demand
- may use limited data and simple mobile flows

### 4.3 Persona: Consumer

- wants recipe guidance
- wants reliable health information
- wants to request millet purchase from relevant sources
- prefers a simple, guided mobile app experience

## 5. Functional Requirements

### 5.1 Onboarding and Authentication

- FR-01: The system shall allow a first-time user to choose Farmer or Consumer before account creation.
- FR-02: The system shall open authentication in signup-first mode for first-time onboarding.
- FR-03: The system shall support account registration using Firebase Authentication.
- FR-04: The system shall support login for existing users.
- FR-05: The system shall preserve the selected onboarding role during first-time registration.
- FR-06: After successful registration, the system shall route the user to the matching farmer or consumer profile form.

### 5.2 Profile Setup

- FR-07: The system shall provide a dedicated farmer profile form.
- FR-08: The system shall provide a dedicated consumer profile form.
- FR-09: The system shall validate required form fields before enabling save.
- FR-10: The system shall store the locked role and profile details in local persistence.
- FR-11: The system shall resume incomplete profile setup for returning users when needed.

### 5.3 Home and Role-Based Navigation

- FR-12: The system shall provide a guest home screen for unauthenticated users.
- FR-13: The system shall provide a farmer home dashboard for completed farmer profiles.
- FR-14: The system shall provide a consumer home dashboard for completed consumer profiles.
- FR-15: The system shall route returning users automatically based on saved auth/profile state.

### 5.4 Mandi Watch

- FR-16: The system shall display millet price data across supported cities.
- FR-17: The system shall display trend direction / high-low style indicators using seeded market records.
- FR-18: The system shall support user access to market options used in farmer stock workflows.

### 5.5 Recipe Lab

- FR-19: The system shall display a list of millet recipes.
- FR-20: The system shall support recipe detail viewing.
- FR-21: The system shall support recipe filtering/searching by millet type.

### 5.6 Favourites

- FR-22: The system shall allow users to save recipes as favourites.
- FR-23: The system shall allow users to remove saved favourites.
- FR-24: The system shall persist favourites locally across app sessions.

### 5.7 Health Benefits

- FR-25: The system shall display health and nutrition information for millets.
- FR-26: The system shall display climate-related notes associated with millet usage.

### 5.8 Direct Buy and Requests

- FR-27: The system shall allow a consumer to create a millet request.
- FR-28: The system shall allow a consumer to mark a request fulfilled or deleted.
- FR-29: The system shall allow a farmer to publish stock details.
- FR-30: The system shall maintain entities for stock listings, consumer requests, and farmer request matches.

### 5.9 Supporting Features

- FR-31: The system shall provide settings and about screens.
- FR-32: The system shall provide analytics-related navigation and state where included in the app.
- FR-33: The system shall show splash-based route resolution on launch.

## 6. Non-Functional Requirements

- NFR-01: The app shall use a modular Android project structure with separation across UI, ViewModel, repository, and data layers.
- NFR-02: The app shall use MVVM architecture.
- NFR-03: The app shall support local persistence using Room.
- NFR-04: The app shall support modern Android UI using Jetpack Compose.
- NFR-05: The app shall support role-based navigation with predictable routing.
- NFR-06: The app shall maintain readable UI text and structured flow for student-demo use.
- NFR-07: The app shall avoid unsupported claims of live production data where the implementation is simulated.
- NFR-08: The project repository shall include the files required for review, setup, and code inspection.
- NFR-09: The build configuration shall remain compatible with the declared Android/Kotlin/Compose stack.
- NFR-10: The codebase shall demonstrate originality through domain-specific implementation rather than template-only content.

## 7. User Flows / Use Cases

### 7.1 First-Time Signup After Role Selection

1. User launches app.
2. User enters onboarding/profile setup path.
3. User selects Farmer or Consumer.
4. App opens auth in signup-first mode.
5. User creates account.
6. App preserves chosen role.
7. App routes user to matching profile form.
8. User fills required details.
9. User saves profile and reaches corresponding home screen.

### 7.2 Farmer Uses Market and Stock Flow

1. Farmer opens app.
2. App restores farmer session/home.
3. Farmer opens Mandi Watch.
4. Farmer reviews price and market context.
5. Farmer publishes or updates stock details.
6. Farmer checks incoming/matched requests.

### 7.3 Consumer Uses Recipe and Health Flow

1. Consumer opens app.
2. Consumer browses Recipe Lab or Health Benefits.
3. Consumer views recipe detail.
4. Consumer saves a favourite recipe.

### 7.4 Consumer Raises Millet Request

1. Consumer opens Direct Buy.
2. Consumer enters millet type, quantity, location, and related fields.
3. System validates the form.
4. System stores the request.
5. Consumer may later mark it fulfilled or delete it.

## 8. System Architecture

### 8.1 Architectural Pattern

- MVVM

### 8.2 Layer Mapping

- View layer: Compose screens and components
- ViewModel layer: state and screen logic
- Repository layer: business/data orchestration
- Room layer: entities and DAO-based local data access
- Firebase layer: authentication and shared sync support where present

### 8.3 Navigation Model

The app uses route-based navigation for splash, auth, guest home, role confirmation, profile forms, role-specific homes, and feature modules.

## 9. Data Model

Current Room entities identified in the implementation:

- UserProfileEntity
- PriceEntity
- RecipeEntity
- FavouriteEntity
- HealthBenefitEntity
- FpoEntity
- TransactionRecordEntity
- FarmerStockListingEntity
- ConsumerMilletRequestEntity
- FarmerRequestMatchEntity
- FeedbackEntity

## 10. Assumptions and Constraints

- price data is currently simulated / seeded for demonstration
- recipe, health, and FPO data use local/demo records
- project evaluation depends heavily on repository evidence and documentation quality
- full build verification should be rechecked on the submission machine
- future GenAI features are not to be claimed as completed unless directly demonstrated

## 11. Acceptance Criteria

- AC-01: A fresh user can select Farmer or Consumer and complete account creation.
- AC-02: After signup, the fresh user lands on the correct role-specific profile form.
- AC-03: Role-specific profile forms accept input and support validation.
- AC-04: Returning users are routed automatically from splash based on saved state.
- AC-05: Mandi Watch, Recipe Lab, Health Benefits, and Direct Buy are reachable from the role-appropriate flow.
- AC-06: Favourites persist across app sessions.
- AC-07: Room entities and repository structure are present in the repository for evaluation.
- AC-08: The repository includes documentation, dependency/config files, and a meaningful project structure.

## 12. Requirements Traceability Matrix

| Requirement | Related Module / Screen | Evidence in Repo | Validation Method |
| --- | --- | --- | --- |
| FR-01 to FR-06 | Auth + Role Confirmation + Profile Setup | `AuthScreen`, `ProfileSetupScreen`, `UserProfileRepository`, navigation routes | Manual onboarding flow |
| FR-07 to FR-11 | Farmer/Consumer profile forms | `FarmerProfileFormScreen`, `ConsumerProfileFormScreen`, `ProfileSetupViewModel` | Form entry and save validation |
| FR-12 to FR-15 | Home routing | `SplashScreen`, `HomeDashboardScreen`, nav host | Launch and route test |
| FR-16 to FR-18 | Mandi Watch | `MandiWatchScreen`, `MandiRepository`, `PriceEntity` | Feature demo with seeded data |
| FR-19 to FR-21 | Recipe Lab | `RecipeLabScreen`, `RecipeRepository`, `RecipeEntity` | Browse and search demo |
| FR-22 to FR-24 | Favourites | `FavouritesScreen`, `FavouriteRepository`, `FavouriteEntity` | Save/remove/persist test |
| FR-25 to FR-26 | Health Benefits | `HealthBenefitsScreen`, `HealthRepository`, `HealthBenefitEntity` | Health card demo |
| FR-27 to FR-30 | Direct Buy / Requests | `DirectBuyScreen`, `FarmerRequestsScreen`, `MarketplaceRepository` | Request/listing flow demo |
| FR-31 to FR-33 | Support features | settings/about/analytics/splash routes | Navigation walkthrough |

## 13. Open Items / Future Scope

- GenAI recipe recommendations
- Millet health chatbot
- Smarter demand or price prediction
- Production-grade external data integration
- Stronger testing and CI workflow for submission confidence

## 14. Submission Notes

Before export to PDF:

- replace placeholders with final submission details
- align the section names with the exact template wording if the PDF template requires renamed headings
- add screenshots or appendices if the requirements template expects visual evidence
- ensure the final PRD wording matches the latest implemented repo state

