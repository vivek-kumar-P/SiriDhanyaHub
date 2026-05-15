# Siri-Dhanya Hub

Siri-Dhanya Hub is a domain-specific Android application developed as part of the VTU Industry Readiness Programme (MindMatrix Project 74). It addresses critical gaps in the millet ecosystem by connecting farmers with market-price visibility and consumers with nutritional awareness and direct produce access.

## 📱 Screenshots
<p align="center">
  <img src="screenshots/home%20screen.jpg" width="30%" />
  <img src="screenshots/farmer%20screen.jpg" width="30%" />
  <img src="screenshots/about%20screen.jpg" width="30%" />
</p>

## 🌟 Project Overview

### Problem Statement
Karnataka's millet ecosystem faces significant fragmentation:
- **Market Access**: Farmers often lack real-time visibility into market prices across different cities.
- **Consumer Awareness**: Limited knowledge regarding millet recipes and their specific health benefits.
- **Value Chain**: A disconnected link between producer knowledge, consumer demand, and practical access.

### Vision & Mission
To create a unified digital experience that improves market rate visibility, encourages millet consumption through education, and supports climate-resilient crop awareness.

### Impact Goals
- **Nutrition Security**: Promoting healthier, millet-based diets.
- **Climate Action**: Encouraging lower-water, climate-resilient crops.
- **Farmer Prosperity**: Supporting better price awareness and direct market access.

## 🏗️ Architecture & Design
The application follows **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** pattern:

- **UI Layer**: Built entirely with **Jetpack Compose** for a modern, reactive interface.
- **State Management**: Uses **Hilt ViewModels** and **StateFlow** to maintain a robust and predictable UI state.
- **Data Layer**: 
    - **Repositories**: Orchestrate data between local and remote sources.
    - **Local (Room)**: Offline-first persistence for market prices, recipes, and profiles.
    - **Remote (Firebase)**: Real-time synchronization via **Firebase Realtime Database** and secure **Firebase Authentication**.

## 🚀 Core Modules

### 1. Mandi Watch
Displays simulated market prices for millet varieties (Navane, Sajje, Baragu, Ragi) across Karnataka cities (Bengaluru, Davangere, Mysuru, Hubli). Includes trend tracking and market option selection.

### 2. Recipe Lab
A comprehensive millet recipe guide featuring search, filtering, and a favorites system for offline reference.

### 3. Health Benefits
Nutritional awareness module detailing the health benefits and climate relevance of various millet types.

### 4. Direct Buy & Request Flow
Facilitates interaction where consumers raise buying requests and farmers publish available stock listings, supported by a local matching engine.

## 👥 User Roles & Flow
- **Guest**: Can browse basic educational content.
- **Farmer**: Manage profiles, publish stock, and respond to consumer demand via a matched inbox.
- **Consumer**: Raise buying requests, explore recipes, and manage favorite content.
- **Onboarding**: Role-locked onboarding ensures a tailored experience from registration to profile completion.

## 📂 Project Structure
```text
com.mindmatrix.siridhanyahub
├── data                # Repositories, DAOs, Entities, and Models
│   ├── local           # Room Database and local entities
│   ├── repository      # Data orchestration (Local + Remote sync)
│   └── auth/profile    # Auth logic and role-based models
├── di                  # Dependency Injection (Hilt Modules)
├── navigation          # Compose Navigation (Routes & NavHost)
├── ui                  # UI Layer (Screens, Components, Theme)
├── viewmodel           # State management for UI
└── MainActivity.kt     # App entry point
```

## 🛠️ Tech Stack
- **Kotlin** & **Jetpack Compose** (Material 3)
- **Hilt** (Dependency Injection)
- **Room** (Local Persistence)
- **Firebase** (Auth & Realtime Database)
- **Coroutines** & **Flow** (Async programming)
- **Coil** (Image loading)

## 📊 Demo Readiness
The app includes a `SeedDataProvider` that populates the local database with demo data:
- 112 price records across 4 cities.
- 12 millet recipes and 12 health benefit records.
- Sample FPO data and initial marketplace listings.
