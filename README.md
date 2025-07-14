# ArtistsApp

An Android application for searching and exploring musical artists using the Discogs API.

## 📱 Demo

### Demo Video

![Application Demo](screenshots/app_demo.gif)

### Demonstrated Features

- ✅ **Real-time search** of artists
- ✅ **Infinite pagination** with scroll
- ✅ **Complete artist details**
- ✅ **Discography exploration** with filters
- ✅ **Detailed information** of releases/albums
- ✅ **Automatic light and dark mode**
- ✅ **Loading states** and error handling
- ✅ **Smooth animations** with Compose

## 🏗️ Architecture

- **MVI (Model-View-Intent)** with Clean Architecture
- **Jetpack Compose** for UI
- **Hilt** for dependency injection
- **Retrofit** for network calls
- **Paging 3** for paginated data loading
- **Coroutines & Flow** for asynchronous programming

### Architecture Reasoning

The chosen architecture follows **Clean Architecture** principles with **MVI**:

- **Clean Architecture**: Clear separation of responsibilities in layers (presentation, domain,
  data)
- **MVI (Model-View-Intent)**: Unidirectional data flow with immutable states
    - **Intent**: User actions (sealed interfaces)
    - **Model**: Application states (sealed interfaces for events/states)
    - **View**: UI that reacts to state changes
- **Repository Pattern**: Data access abstraction, allowing to change data sources without affecting
  business logic
- **Use Cases**: Encapsulation of specific business logic, following the single responsibility
  principle
- **Dependency Injection**: Better testability and decoupling between components

This architecture provides:

- **Unidirectional flow**: Intent → ViewModel → State → UI
- **Immutable states**: Prevents concurrency bugs
- **Testability**: Each intent and state can be tested independently
- **Predictability**: Data flow is always the same
- **Debugging**: Easy tracking of application state

### Implemented MVI Pattern

```kotlin
// Intents - User actions
sealed interface ArtistDetailIntent {
    data class Load(val id: Long) : ArtistDetailIntent
    data class Retry(val id: Long) : ArtistDetailIntent
}

// Events/States - Application states
sealed interface ArtistDetailEvent {
    object Loading : ArtistDetailEvent
    data class Success(val detail: ArtistDetail) : ArtistDetailEvent
    data class Error(val message: String) : ArtistDetailEvent
}

// ViewModel - Processes intents and emits states
class ArtistDetailViewModel : ViewModel() {
    fun onIntent(intent: ArtistDetailIntent) { /* ... */ }
    val event: StateFlow<ArtistDetailEvent> = _event
}
```

## 🚀 Features

- Search artists by name
- Detailed artist view with complete information
- Discography exploration with pagination
- Release/album details
- Modern UI with Material 3 Design
- Loading states and error handling
- Complete unit tests
- Light and dark mode support
- Responsive design for different screen sizes
- Advanced search filters

## 🔧 Project Setup

### Prerequisites

- Android Studio Flamingo or higher
- JDK 11+
- Android SDK 24+
- Discogs API token (free)

### Installation

1. Clone the repository

```bash
git clone https://github.com/gibranhit/ArtistsApp.git
cd ArtistsApp
```

2. Configure Discogs API token:
    - Go to [Discogs Developer](https://www.discogs.com/developers/)
    - Create an account and generate a personal token
    - In Android Studio, go to `Build > Edit Build Types > Debug`
    - Add the token in `buildConfigField("String", "DISCOGS_USER_TOKEN", "\"your_token_here\"")`

3. Open the project in Android Studio

4. Sync the project with Gradle

5. Run the application

### Additional Configuration

For local development, you can configure:

```gradle
// In app/build.gradle
buildTypes {
    debug {
        buildConfigField("String", "DISCOGS_USER_TOKEN", "\"your_development_token\"")
        debuggable true
    }
    release {
        buildConfigField("String", "DISCOGS_USER_TOKEN", "\"your_production_token\"")
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

## 📊 Static Code Analysis with Detekt

This project includes **Detekt**, a static code analyzer for Kotlin that helps maintain code
quality.

### Available Commands

#### Run code analysis

```bash
./gradlew detekt
```

#### Generate reports in different formats

Reports are automatically generated in:

- `app/build/reports/detekt/detekt.html` - Detailed HTML report
- `app/build/reports/detekt/detekt.xml` - XML report
- `app/build/reports/detekt/detekt.txt` - Plain text report

#### Create baseline (ignore existing issues)

```bash
./gradlew detektBaseline
```

### Configured Rules

Detekt is configured with rules that check:

- **Complexity**: Very long methods, very large classes, cyclomatic complexity
- **Code style**: Naming conventions, code structure
- **Potential issues**: Null safety, unsafe casting, exceptions
- **Performance**: Unnecessary use of temporary objects
- **Coroutines**: Correct use of suspend functions

### Results Interpretation

Detekt reports include:

- **Severity levels**: Error, Warning, Info
- **Rule violations**: Specific problem description
- **Location**: Exact file and line of the issue
- **Suggestions**: Recommendations to solve the problem

**Interpretation example**:

```
ComplexMethod - src/main/.../ArtistViewModel.kt:45:1
  Function searchArtists has 12 statements, threshold is 10
  → Refactor the method by dividing it into smaller functions
```

### CI/CD Integration

To integrate Detekt in your CI/CD pipeline, add the following step:

```yaml
- name: Run static analysis
  run: ./gradlew detekt
```

### Custom Configuration

Detekt configuration is located in `config/detekt/detekt.yml`. You can customize rules according to
project needs.

## 🧪 Testing

### Run all tests

```bash
./gradlew test
```

### Run specific unit tests

```bash
./gradlew testDebugUnitTest
```

### Test coverage

The project includes unit tests for:

- ViewModels
- Use Cases
- Repository implementations
- Paging Sources

## 📱 Project Structure

```
app/
├── src/main/java/com/gibran/artistsapp/
│   ├── data/                     # Data layer
│   │   ├── api/                  # API services
│   │   ├── paging/               # Paging sources
│   │   ├── repository/           # Repository implementations
│   │   └── response/             # DTOs/Response models
│   ├── di/                       # Hilt modules
│   ├── domain/                   # Domain layer
│   │   ├── model/                # Domain models
│   │   ├── repository/           # Repository interfaces
│   │   └── usecase/              # Use cases
│   ├── presentation/             # Presentation layer
│   │   ├── ui/                   # Composables and screens
│   │   │   └── components/       # Reusable components
│   │   └── viewmodel/            # ViewModels
│   ├── navigation/               # App navigation
│   └── ui/theme/                 # Theme and styles
└── src/test/                     # Unit tests
```

## 🎨 Design System and UI/UX

The application uses a consistent design system with:

- **Material 3 Design**: Following Google's latest guidelines
- **Custom colors**: Palette optimized for light and dark mode
- **Consistent typography**: Clear text hierarchy
- **Systematic spacing**: 4dp/8dp grid system
- **Reusable components**: Standardized buttons, cards, inputs

### Responsive Design

The application is optimized for different screen sizes:

- **Phones**: Vertical layout with bottom navigation
- **Tablets**: Adaptive layout with better space usage
- **Landscape**: Automatic component adjustment
- **Densities**: Support for different DPI

### Accessibility

- **Content descriptions**: For screen readers
- **Contrast ratios**: WCAG guidelines compliance
- **Touch targets**: Minimum 48dp according to Material Guidelines
- **Keyboard navigation**: Full support

## 📚 Main Libraries

- **Jetpack Compose**: Modern declarative UI
- **Hilt**: Dependency injection
- **Retrofit + Moshi**: Networking and JSON serialization
- **Paging 3**: Efficient paginated loading
- **Coil**: Image loading
- **Navigation Compose**: Navigation
- **ViewModel**: State management
- **Coroutines**: Asynchronous programming
- **Detekt**: Static code analysis

## 🔒 API Configuration

The application uses the Discogs API. The API token is configured in `BuildConfig`:

```kotlin
buildConfigField("String", "DISCOGS_USER_TOKEN", "\"your_token_here\"")
```

## 🔍 Analysis and Development Process

### Requirements Analysis

1. **Required functionality**: Artist search, detailed view, pagination
2. **Technical constraints**: Native Android, external API, performance
3. **User experience**: Intuitive navigation, responsive design, loading states

### Development Process

1. **Architecture**: Definition of Clean Architecture with MVI
2. **Initial setup**: Configuration of Hilt, Retrofit, Compose
3. **Layer implementation**:
    - Data layer (API, repositories)
    - Domain layer (models, use cases)
   - Presentation layer (UI, ViewModels with MVI)
4. **Testing**: Unit tests for each component
5. **Static analysis**: Detekt configuration
6. **UI polish**: Material 3 implementation

### Technical Decisions

- **MVI**: Unidirectional flow and immutable states vs. traditional MVVM
- **Paging 3**: For efficient handling of large lists
- **Hilt**: Better Android integration than pure Dagger
- **Compose**: Modern and declarative UI vs. traditional XML
- **Sealed interfaces**: For type-safe Intent and Event/State definitions
- **StateFlow**: For reactive state emission
- **Moshi**: Better performance than Gson for JSON parsing
- **Coil**: Optimized for Compose vs. Glide/Picasso

## 🤝 Contributing

1. Fork the project
2. Create a branch for your feature (`git checkout -b feat/new-feature`)
3. Run Detekt to verify code quality (`./gradlew detekt`)
4. Run tests (`./gradlew test`)
5. Commit your changes (`git commit -am 'Add new feature'`)
6. Push to the branch (`git push origin feat/new-feature`)
7. Open a Pull Request
