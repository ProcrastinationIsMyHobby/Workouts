## 🔧 Технологии и стек

- Kotlin
- MVVM (Model-View-ViewModel)
- Clean Architecture
- Coroutines / Flow
- Hilt (Dependency Injection)
- Jetpack Navigation
- Retrofit
- XML (в зависимости от UI-фреймворка)

---

## ⚙️ Системные зависимости

- **Android Gradle Plugin (AGP)**: `8.9.1`
- **Gradle**: `8.11.1`
- **Kotlin**: `1.9.x`
- Compile SDK: `35`
- Min SDK: `26`

---

## 🧱 Архитектура

Проект построен на основе **Clean Architecture** и использует **MVVM** как Presentation слой. 

---

## Решения принятые при разработке

В основном все стандартно, однако пришлось отказаться от использвания VideoView, тк она очень плохо вставала в UI. Создал TextureView, куда MediaPlayer уже грузит видосы.
Немного расширил класс самого плеера, накинул туда интерфейс DefaultLifecycleObserver, для того чтобы с ним удобнее было работать во фрагменте, так же реализовал сохранение его состояния при смене конфигурации
