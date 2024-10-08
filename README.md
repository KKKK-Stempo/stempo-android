# Stempo<img src="https://github.com/user-attachments/assets/0a825ee4-3f9e-44db-94f7-31ae7e38d5f3" align=left width=100> [![GitHub License](https://img.shields.io/github/license/KKKK-stempo/stempo-android)](https://github.com/KKKK-stempo/stempo-android/blob/main/LICENSE)
> RAS 청각 자극을 통한 보행 패턴 개선 서비스

<br>

## 🏃‍➡️ Stempo
![설명](https://github.com/user-attachments/assets/978dd883-667e-4cf7-afa1-5ad5495ceb70)

<br>

## 📚 기술 스택
- **Clean Architecture**  지향
- **Architecture : MVVM**
- **Minimum SDK** 28 / **Target SDK** 34
- App
  - XML with ViewBinding &middot; DataBinding
- Wear OS
  - Compose UI
- **사용한 라이브러리**
    - Hilt, Retrofit, mpChart, Coil, Phonix 등
- **비동기 처리**
    - Coroutine Flow
- **버전 관리**
    - BuildSrc &middot; version Catalog

<br>

<br>

## 🗂️ Module & Package Convention
```
🗃️app

🗃️buildSrc

🗃️core-ui
 ┣ 📂base
 ┗ 📂extension

🗃️core-di

🗃️data
 ┣ 📂dto
 ┃ ┣ 📂response
 ┃ ┣ 📂request
 ┣ 📂datasource
 ┣ 📂datasourceImpl
 ┣ 📂local
 ┣ 📂repositoryImpl
 ┗ 📂service

🗃️domain
 ┣ 📂entity
 ┃ ┣ 📂response
 ┃ ┣ 📂request
 ┗ 📂repository

🗃️presentation
 ┗ 📂기능 별 패키징

🗃️watch
 ┗ 📂기능 별 패키징
```
<br>

## 👨‍💻 Contributors

[![contributors](https://contrib.rocks/image?repo=KKKK-Stempo/stempo-android)](https://github.com/KKKK-Stempo/stempo-android/contributors)

