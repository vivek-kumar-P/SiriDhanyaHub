# Siri-Dhanya Hub Execution Plan

## Build strategy
Build the app in thin, testable slices. Each slice must compile, run, and be demoable before the next feature is added.

## Part 1: App foundation
Goal: replace the starter screen with real app structure.

Deliverables:
- App navigation graph
- Splash screen
- Login/Register placeholder flow
- Home dashboard
- Bottom navigation shell
- Placeholder destinations for all major modules

Definition of done:
- App launches into splash
- User can enter the app shell
- User can move across all main screens without crashes

## Part 2: Data layer foundation
Goal: create the storage model before feature logic.

Deliverables:
- Room entities
- DAO interfaces
- Database class
- Repository interfaces/implementations
- Seed-data providers

Definition of done:
- App builds with Room configured
- Seed routines can populate empty tables

## Part 3: Mandi Watch
Goal: deliver the first fully working feature.

Deliverables:
- Price list screen
- City filter
- Trend indicators
- 7-day trend detail screen
- Seeded price data

Definition of done:
- 4 millet types render
- 4 cities filter correctly
- Trend detail opens per millet/city

## Part 4: Recipe Lab
Goal: deliver the strongest user-facing content feature.

Deliverables:
- Recipe list
- Search by millet type
- Recipe detail
- Kannada content rendering
- Seeded recipe data

Definition of done:
- Search filters in real time
- Recipe detail shows required content fields

## Part 5: Favourites
Goal: prove local persistence and interaction quality.

Deliverables:
- Save/remove favourite action
- Favourites screen
- Empty state

Definition of done:
- Favourites persist after app restart

## Part 6: Health Benefits
Goal: complete educational content section.

Deliverables:
- Health list
- Health detail
- Seeded health data

Definition of done:
- All 4 millet cards and detail content render correctly

## Part 7: Direct Buy
Goal: complete agriculture commerce flow.

Deliverables:
- FPO list
- FPO detail
- Simulated request-to-buy dialog
- Seeded FPO data

Definition of done:
- User can browse FPOs and trigger simulated buy flow

## Part 8: Authentication and Firebase
Goal: connect the app to production-style auth flow.

Deliverables:
- Firebase project setup
- `google-services.json`
- Register/login/logout
- Session handling

Definition of done:
- Protected app flow works with Firebase Auth

## Part 9: Polish and testing
Goal: make the app demo-ready.

Deliverables:
- Loading, error, and empty states
- UI consistency pass
- Kannada readability pass
- Basic test coverage
- Demo checklist

Definition of done:
- End-to-end app flow is stable for presentation

## Immediate next coding order
1. Build app navigation shell
2. Add feature placeholders
3. Add Room schema
4. Implement Mandi Watch
5. Implement Recipe Lab
6. Implement Favourites
7. Implement Health Benefits
8. Implement Direct Buy
9. Add Firebase Auth
