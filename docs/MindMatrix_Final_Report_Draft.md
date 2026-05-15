# Siri-Dhanya Hub Final Project Report

## Cover Page

- Project Title: Android App Development using GenAI - Siri-Dhanya Hub (Agriculture)
- Project ID: 74
- Student Name: Vivek Kumar P
- USN: 1CE22AI059
- Company: MindMatrix Pvt. Ltd.
- Department: Android Development / VTU Industry Readiness Programme
- Guide / Mentor: Prof. S. Asma Begum
- College: City Engineering College, Department of AI & ML
- Submission Date: [Fill before export]
- GitHub Repository URL: [Insert public repository URL]

## Executive Summary

Siri-Dhanya Hub is an Android application developed as part of MindMatrix Project 74 under the VTU Industry Readiness Programme. The project addresses two linked problems in the millet ecosystem of Karnataka. Small millet farmers often lack access to clear market-price visibility across cities, while consumers have limited awareness of millet recipes, health benefits, and practical ways to connect with producers.

The application is designed for two user roles: farmers and consumers. It brings together multiple modules in a single Android app: Mandi Watch for simulated city-wise millet prices and trend tracking, Recipe Lab for millet recipes, Health Benefits for nutritional awareness, Direct Buy and request workflows for farmer-consumer/FPO interaction, and profile-driven onboarding with role-based navigation.

The implementation uses Kotlin, Jetpack Compose, MVVM architecture, Room Database, Firebase Authentication, Hilt dependency injection, Coroutines, Navigation Compose, and Coil for image loading. The repository also includes supporting modules such as favourites, analytics, farmer requests, settings, and role-based onboarding/profile flows.

This report documents the problem context, objectives, architecture, implementation status, module design, technical decisions, limitations, and submission-readiness considerations aligned with MindMatrix’s project evaluation criteria.

## 1. Project Overview

### 1.1 Problem Statement

Karnataka is one of the leading millet-producing regions in India. However:

- farmers do not always have easy access to price information across multiple markets
- consumers may not know how to cook millet-based food or understand its health value
- the value chain between producer knowledge, consumer demand, and practical access remains fragmented

### 1.2 Vision

Siri-Dhanya Hub is a millet value-chain Android application designed to serve both farmers and consumers through a single mobile experience. The app aims to:

- improve visibility of millet market rates
- encourage millet consumption through recipes and health information
- support climate-resilient crop awareness
- create a structured digital flow for requests, listings, and profile-based access

### 1.3 Target Users

- Farmers
  Farmers use the app to view market trends, publish available stock, and respond to consumer demand.
- Consumers
  Consumers use the app to explore recipes, health benefits, raise buying requests, and manage saved content.

### 1.4 Impact Goals

- Nutrition Security: promote healthier millet-based diets
- Climate Action: encourage lower-water, climate-resilient crops
- Farmer Prosperity: support better price awareness and market access

## 2. Objectives

### 2.1 Functional Objectives

- provide role-based onboarding for farmer and consumer users
- display simulated millet market data across Karnataka cities
- support recipe browsing and favourites
- show health-benefit information for selected millets
- allow request/listing workflows related to direct buying

### 2.2 Technical Objectives

- implement the app using Kotlin and Jetpack Compose
- follow MVVM architecture with repository-based separation
- use Room for local persistence
- integrate Firebase Authentication for account access
- structure the codebase cleanly for MindMatrix evaluation

### 2.3 Documentation and Submission Objectives

- maintain a report aligned with the project brief
- maintain a formal requirements document / PRD
- prepare the repository to satisfy evaluation criteria such as README quality, structure, code completeness, and build readiness

## 3. System Scope and Feature Set

### 3.1 Core User-Facing Modules

- Mandi Watch
  Shows simulated millet prices, city-wise market options, and trend direction.
- Recipe Lab
  Provides recipe listing, recipe detail, and millet-based search.
- Health Benefits
  Displays factual nutrition and climate notes for different millets.
- Direct Buy
  Supports consumer buying requests and farmer listing-related workflows.

### 3.2 Supporting Implemented Modules

- Firebase-based Authentication
- Role-based onboarding and profile setup
- Favourites for saved recipes
- Farmer Requests inbox flow
- Analytics screen
- Settings and About screens
- Splash and home dashboards for guest, farmer, and consumer states

### 3.3 User Roles

- Guest
  Can browse basic content and is prompted to continue through onboarding.
- Farmer
  Can complete farmer profile, publish stock, view market references, and access farmer-specific request flows.
- Consumer
  Can complete consumer profile, raise requests, browse educational content, and manage saved items.

## 4. User Flow

### 4.1 Fresh User Flow

1. User launches the app.
2. If no saved authenticated session exists, the user reaches the guest/home onboarding entry.
3. User chooses a role: Farmer or Consumer.
4. The app opens account creation in signup-first mode.
5. After successful registration, the selected role is locked for the account.
6. The user is routed directly to the matching profile form:
   - Farmer -> Farmer Profile Form
   - Consumer -> Consumer Profile Form
7. After successful profile completion, the user reaches the matching home dashboard.

### 4.2 Returning User Flow

1. User launches the app on the same device.
2. Splash screen resolves the saved auth/profile state.
3. The app routes automatically to:
   - Farmer Home if farmer profile is complete
   - Consumer Home if consumer profile is complete
   - Matching incomplete profile screen if role is locked but form is unfinished

### 4.3 Farmer Flow

- login/register
- role lock and profile completion
- inspect market data
- publish stock
- view farmer requests / matched consumer demand
- update settings or profile details

### 4.4 Consumer Flow

- login/register
- role lock and profile completion
- browse recipes and health information
- save favourites
- raise and manage buying requests
- update settings or profile details

## 5. Technical Implementation

### 5.1 Technology Stack

- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM
- Local Storage: Room Database
- Authentication: Firebase Auth
- Dependency Injection: Hilt
- Navigation: Navigation Compose
- Async / State: Coroutines and StateFlow
- Image Loading: Coil

### 5.2 Architecture

The application follows the MVVM pattern:

- UI layer: Compose screens and components
- ViewModel layer: screen state, validation, business logic, user actions
- Repository layer: data access orchestration and domain logic
- Local data layer: Room entities and DAO interfaces
- Remote/auth layer: Firebase authentication and shared marketplace sync helpers

### 5.3 Navigation Structure

The app contains route handling for:

- splash
- guest home
- farmer home
- consumer home
- auth
- role confirmation
- farmer profile
- consumer profile
- mandi
- recipes
- health
- buy
- saved
- farmer requests
- analytics
- about
- settings

### 5.4 Local Database Design

The Room database currently includes the following entities:

- PriceEntity
- RecipeEntity
- FavouriteEntity
- HealthBenefitEntity
- FpoEntity
- UserProfileEntity
- TransactionRecordEntity
- FarmerStockListingEntity
- ConsumerMilletRequestEntity
- FarmerRequestMatchEntity
- FeedbackEntity

Database name:

- `siri_dhanya_hub.db`

### 5.5 Repository Layer

Current repositories identified in the project:

- AppSeedRepository
- AuthRepository
- UserProfileRepository
- TransactionRepository
- MandiRepository
- RecipeRepository
- FavouriteRepository
- HealthRepository
- DirectBuyRepository
- MarketplaceRepository
- FirestoreMarketplaceRepository

## 6. Module-wise Implementation Details

### 6.1 Mandi Watch

Purpose:

- provide simulated market prices for millet varieties across multiple Karnataka cities

Implementation evidence:

- dedicated `MandiWatchScreen`
- seeded price records generated through `SeedDataProvider`
- city and millet handling supported in repository/data model

Current delivered behavior:

- price display
- trend direction
- market option selection support for related farmer workflows

### 6.2 Recipe Lab

Purpose:

- help consumers learn millet recipes in an accessible form

Implementation evidence:

- `RecipeLabScreen`
- `RecipeDetailScreen`
- `RecipeRepository`
- `RecipeEntity`
- favourites integration

Current delivered behavior:

- recipe listing
- recipe detail
- millet-based search/filtering support
- saved recipes flow

### 6.3 Health Benefits

Purpose:

- promote millet nutrition awareness and climate relevance

Implementation evidence:

- `HealthBenefitsScreen`
- detail screen logic
- `HealthRepository`
- `HealthBenefitEntity`

Current delivered behavior:

- nutrition card browsing
- climate note and health statement presentation

### 6.4 Direct Buy and Request Flow

Purpose:

- support digital interaction between producer-side and consumer-side demand

Implementation evidence:

- `DirectBuyScreen`
- `FarmerRequestsScreen`
- `MarketplaceRepository`
- stock listing, consumer request, and request-match entities

Current delivered behavior:

- consumer request draft and save flow
- farmer stock publishing flow
- active request management
- request matching support through local entities and repository logic

### 6.5 Onboarding, Auth, and Profile Setup

Purpose:

- create a role-specific first-time experience with farmer/consumer path separation

Implementation evidence:

- `AuthScreen`
- `RoleConfirmationScreen`
- `FarmerProfileFormScreen`
- `ConsumerProfileFormScreen`
- `AuthViewModel`
- `ProfileSetupViewModel`
- `UserProfileRepository`

Current delivered behavior:

- signup/login
- role selection
- role-aware profile setup
- returning-user route resolution

## 7. Seeded Data and Demo Readiness

The project includes demo seed data through `SeedDataProvider`.

Known seed signals from the codebase:

- millet types: Navane, Sajje, Baragu, Ragi
- cities: Bengaluru, Davangere, Mysuru, Hubli
- price seed pattern: 4 millets x 4 cities x 7 days = 112 records
- recipe seeds: 12 recipes
- health benefit seeds: 12 records
- FPO seeds: 5 records

These seeded records support demoability and help satisfy project completeness expectations even without live production feeds.

## 8. Testing and Validation

### 8.1 Functional Validation Checklist

- guest navigation works
- signup/login flow works
- role-based onboarding works
- farmer and consumer profile forms validate inputs
- recipe, health, and mandi screens open without navigation errors
- favourites persist through Room
- consumer request and farmer stock flows can be demonstrated

### 8.2 Build and Run Notes

Known project files for build confidence:

- root `build.gradle.kts`
- app `build.gradle.kts`
- Gradle settings and properties
- AndroidManifest
- Hilt, Room, Firebase, Compose dependencies

Known current submission risk:

- local Gradle wrapper completeness should be rechecked before final submission
- final report should state build prerequisites clearly

### 8.3 Validation Gaps to Close Before Submission

- confirm clean build on a machine with working Gradle wrapper / Android Studio setup
- capture screenshots for each implemented feature
- verify README setup steps match the current repo exactly

## 9. Project Outcomes

### 9.1 Delivered Outcomes

- multi-screen Android application with role-based flows
- MVVM-based architecture
- Room-backed local persistence
- Firebase authentication integration
- simulated market, recipe, health, and request/listing feature coverage
- formal documentation sources already created during internship

### 9.2 Alignment with Project 74 Brief

The current implementation aligns strongly with the Project 74 brief in the following areas:

- millet domain focus
- dual user perspective: farmer and consumer
- market-price visibility module
- recipe-awareness module
- health-awareness module
- direct-buy / request workflow

### 9.3 Evaluation-Readiness Strengths

- multiple folders and modular package structure
- clear Android project configuration
- multiple repositories, ViewModels, screens, entities
- meaningful custom domain logic
- seeded data for demoability

## 10. Challenges and Resolutions

### 10.1 Tooling and Build Constraints

- Android/Gradle compatibility management
- environment-specific wrapper/build setup issues

### 10.2 UI and Compose Challenges

- interactive form behavior in onboarding/profile setup
- dropdown and state-handling compatibility

### 10.3 Auth and Profile Routing Challenges

- role-first onboarding for fresh users
- resume flow for returning users
- profile completion and role locking

### 10.4 Data and Architecture Challenges

- keeping composables free of direct data-layer calls
- maintaining clear repository-driven state flow
- supporting demo data and user workflows in parallel

## 11. Future Enhancements

The following should be documented as future scope unless already implemented and demonstrable:

- GenAI-based recipe recommendation
- health assistant / millet chatbot
- market trend prediction or recommendation logic
- real production mandi API integration
- expanded language/localization support
- richer analytics and transaction insight

## 12. Conclusion

Siri-Dhanya Hub is a domain-specific Android application built around a meaningful agriculture and nutrition problem. The project demonstrates practical use of Kotlin, Jetpack Compose, MVVM, Room, Firebase, Hilt, and modern Android app structure. It also reflects custom implementation effort through module separation, seeded demo data, role-based flows, and repository-backed business logic.

For MindMatrix submission, the strongest next step is to pair this report with a polished README, validated build/run instructions, updated screenshots, and a formal PRD/requirements document that maps the implemented system back to Project 74 requirements.

## Appendix A: Repository Readiness Checklist

- [ ] Public GitHub repository URL added
- [ ] README updated with full setup and run instructions
- [ ] Screenshots added to README/report
- [ ] Generated/unnecessary folders excluded from repo
- [ ] Build checked on clean Android Studio setup
- [ ] Commit history reviewed for meaningful progress
- [ ] Demo evidence captured

## Appendix B: Screenshots to Collect

- splash screen
- guest home
- role selection
- signup screen
- farmer profile form
- consumer profile form
- farmer home
- consumer home
- mandi watch
- recipe list
- recipe detail
- health benefits
- direct buy request screen
- favourites
- farmer requests
- settings / about

## Appendix C: References

- MindMatrix Project 74 brief
- MindMatrix automated evaluation criteria document
- Existing Siri-Dhanya SRD
- Internship presentation material
- Official Android Developer documentation

