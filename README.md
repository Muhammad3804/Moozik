# 🎸 Moozik — Music E-Commerce & Learning Platform

A full-stack Android application where users can browse and purchase musical instruments, manage their cart, book music lessons, and track orders — all in one place.

## ✨ Features

### 🔐 Authentication
- Email/Password registration and login
- Google Sign-In
- Email verification before access
- Persistent sessions across app restarts
- Secure logout

### 🛍️ Store
- Dynamic product catalog fetched from REST API
- RecyclerView with instrument cards (name, price, image, rating)
- Add to Cart functionality
- Asset-based image loading via Glide

### 🛒 Cart
- Per-user cart — every user sees only their own cart
- Full CRUD operations (add, update quantity, remove, clear)
- Search cart items by name
- Sort by price (low to high / high to low)
- Real-time cart sync via Firebase Firestore across devices
- Local SQLite persistence for offline support
- Subtotal calculation

### 💳 Checkout
- Firestore-backed delivery address management
- Auto-fills saved address for returning users
- Payment method selection (Credit/Debit Card or Cash on Delivery)
- Local push notification on order confirmation

### 👤 Profile
- Personalized user name and stats per account
- Logout functionality

### 🎵 Lessons
- Music lessons browsing interface

---

## 🏗️ Architecture

- **Single Activity Architecture** — MainActivity as host container
- **Fragment-based navigation** — Store, Cart, Lessons, Profile, Checkout
- **Custom back-stack management** via FragmentManager
- **Repository pattern** — CartRepository (SQLite), FirestoreCartRepository (Firestore)
- **Async operations** — all DB and network calls on background threads via Kotlin Coroutines

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | XML, Jetpack Compose |
| Architecture | Single Activity, Fragments |
| Authentication | Firebase Auth (Email + Google) |
| Cloud Database | Firebase Firestore |
| Local Database | SQLite (SQLiteOpenHelper) |
| Networking | Retrofit, Gson |
| Image Loading | Glide |
| Async | Kotlin Coroutines |
| Backend Data | MockAPI |
| Notifications | Android Local Notifications |
| Version Control | Git, GitHub |

---

## 🗄️ Database Schema

### SQLite
```
products
├── id (PK, AUTOINCREMENT)
├── title
├── price
├── description
├── category
└── image_url

cart_items
├── id (PK, AUTOINCREMENT)
├── product_id (FK → products.id)
└── quantity
```

### Firestore
```
users/
  {userId}/
    ├── name
    ├── email
    ├── userId
    ├── fullName
    ├── phone
    ├── street
    ├── city
    ├── province
    └── postalCode

cart_items/
  {cartItemId}/
    ├── userId
    ├── productId
    ├── productName
    ├── price
    └── quantity
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio
- Android device or emulator (API 24+)
- Firebase project with Authentication and Firestore enabled

### Setup
1. Clone the repository
```bash
git clone https://github.com/Muhammad3804/Moozik.git
```
2. Open in Android Studio
3. Add your `google-services.json` to the `app/` folder
4. Sync Gradle and run the app

---

## 📁 Project Structure

```
app/
├── activities/
│   ├── MainActivity.kt
│   ├── LoginActivity.kt
│   └── SignupActivity.kt
├── fragments/
│   ├── StoreFragment.kt
│   ├── CartFragment.kt
│   ├── LessonsFragment.kt
│   ├── ProfileFragment.kt
│   └── CheckoutFragment.kt
├── adapters/
│   └── ProductAdapter.kt
├── models/
│   ├── Product.kt
│   └── CartItem.kt
├── data/
│   ├── MoozikDbHelper.kt
│   ├── CartRepository.kt
│   ├── FirestoreCartRepository.kt
│   └── ApiProductDto.kt
├── network/
│   ├── RetrofitClient.kt
│   └── ApiService.kt
└── util/
    └── AssetImageLoader.kt
```



## 👨‍💻 Developer

**Muhammad Bin Musaddiq**  
[LinkedIn](https://linkedin.com/in/mbinmusaddiq) | [GitHub](https://github.com/Muhammad3804)
