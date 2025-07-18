plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.gibran.artistsapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gibran.artistsapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildFeatures.buildConfig = true
        buildConfigField("String", "DISCOGS_BASE_URL", "\"https://api.discogs.com/\"")
        buildConfigField(
            "String",
            "DISCOGS_USER_TOKEN",
            "\"iFmjmIWKjuxSoKCSizEEGIlUqOrnxGBOCXzQIMMZ\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Architecture Components
    implementation(libs.viewmodel.compose)
    implementation(libs.navigation.compose)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    // Image Loading
    implementation(libs.coil.compose)

    // Coroutines
    implementation(libs.coroutines.android)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)


    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.paging.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

detekt {
    // Usar configuración personalizada
    config.setFrom("$rootDir/config/detekt/detekt.yml")

    // Construir sobre la configuración por defecto
    buildUponDefaultConfig = true

    // No activar todas las reglas automáticamente
    allRules = false

    // Autocorrección automática cuando sea posible
    autoCorrect = true

    // Ejecutar en paralelo para mejor rendimiento
    parallel = true

    // Fallar el build si hay issues
    ignoreFailures = false

    // Configurar reportes
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(true)
        md.required.set(false)
    }
}