import org.gradle.kotlin.dsl.invoke
import kotlin.plus

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.datamanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.datamanager"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/ASL-2.0.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/kotlin-jupyter-libraries/libraries.json",
                "META-INF/LGPL-3.0.txt",
                "META-INF/INDEX.LIST",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
            pickFirsts += listOf(
                "META-INF/xmlenc-config.xml",
                "META-INF/xml.xsd",
                "META-INF/services/javax.xml.stream.XMLInputFactory",
                "draftv3/schema",
                "arrow-git.properties",
                "draftv4/schema",
            )
        }
    }


    dependencies {
        val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
        implementation(composeBom)

        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
        implementation("androidx.activity:activity-compose:1.8.2")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
        implementation("androidx.navigation:navigation-compose:2.7.7")
        implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

        // Network & JSON
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("com.squareup.moshi:moshi:1.15.0")
        implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

        // JSON Schema
        implementation("com.github.java-json-tools:json-schema-validator:2.2.14") {
            exclude(group = "commons-logging", module = "commons-logging")
        }
        implementation("com.github.java-json-tools:json-schema-core:1.2.14") {
            exclude(group = "commons-logging", module = "commons-logging")
        }

        // XML APIs for Apache POI
        implementation("xerces:xercesImpl:2.12.2")
        implementation("org.apache.xmlbeans:xmlbeans:5.1.1")

        // Apache Commons
        implementation("commons-codec:commons-codec:1.15")
        implementation("org.apache.commons:commons-collections4:4.4")
        implementation("commons-io:commons-io:2.11.0")
        implementation("org.apache.commons:commons-compress:1.24.0")

        // Apache POI
        implementation("org.apache.poi:poi:5.2.2") {
            exclude(group = "commons-codec", module = "commons-codec")
            exclude(group = "org.apache.commons", module = "commons-collections4")
            exclude(group = "commons-io", module = "commons-io")
        }
        implementation("org.apache.poi:poi-ooxml:5.2.2") {
            exclude(group = "org.apache.commons", module = "commons-compress")
            exclude(group = "commons-codec", module = "commons-codec")
            exclude(group = "org.apache.commons", module = "commons-collections4")
            exclude(group = "commons-io", module = "commons-io")
        }

        // DataFrame
        implementation("org.jetbrains.kotlinx:dataframe:0.12.1") {
            exclude(group = "commons-logging", module = "commons-logging")
        }

        // Arrow
        implementation("org.apache.arrow:arrow-vector:11.0.0")
        implementation("org.apache.arrow:arrow-memory-core:11.0.0")
        implementation("org.apache.arrow:arrow-memory-unsafe:11.0.0")

        // Testing
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-tooling")
        debugImplementation("androidx.compose.ui:ui-test-manifest")

        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.activity:activity-compose:1.8.2")
        implementation("androidx.compose.ui:ui:1.5.4")
        implementation("androidx.compose.material:material:1.5.4")
        implementation("androidx.navigation:navigation-compose:2.7.6")
        implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
        debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
        implementation("org.apache.commons:commons-math3:3.6.1")
        implementation("androidx.compose.material:material-icons-extended:1.5.4")
        implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
        implementation("com.google.firebase:firebase-core:21.1.1")
        implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
        implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
        // Room components
        implementation("androidx.room:room-runtime:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")
        kapt("androidx.room:room-compiler:2.6.1")
    }
}
dependencies {
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.runtime.livedata)
}
