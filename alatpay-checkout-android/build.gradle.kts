import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.parcelize)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
//    `maven-publish`
}
// Versioning
val libraryVersionName by extra { "1.0.0" } // Update this for each release
val libraryVersionCode by extra { 1 } // Update this for each release

group = "com.github.ALATPay" // Use ALATPay GitHub username here
version = libraryVersionName // Version should be updated with each release

afterEvaluate {
    tasks.withType<org.gradle.jvm.tasks.Jar>().configureEach {
        archiveBaseName.set("alatpay-kotlin") // Replace with your library name
    }
}

//publishing {
//    publications {
//        create<MavenPublication>("release") {
//            from(components["java"])
//
//            // Define POM metadata (optional, but recommended)
//            pom {
//                name.set("ALATPay Android Kotlin SDK")
//                description.set("A simple and efficient way to integrate ALATPay into your Android applications using the Kotlin SDK. Easily accept payments and manage transactions")
//                url.set("https://github.com/ALATPay/alatpay-kotlin.git")
//                licenses {
//                    license {
//                        name.set("The Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("ajibadeseun")
//                        name.set("Seun Ajibade")
//                        email.set("ajibadeseun@gmail.com")
//                    }
//                }
//                scm {
//                    connection.set("scm:git:git://github.com/ALATPay/alatpay-kotlin.git")
//                    developerConnection.set("scm:git:ssh://github.com/ALATPay/alatpay-kotlin.git")
//                    url.set("https://github.com/ALATPay/alatpay-kotlin.git")
//                }
//            }
//        }
//    }
//}

// Git Tag Task (Cross-Platform)
tasks.register("publishToJitPack") {
    doLast {
        val tagName = "v$libraryVersionName"
        println("Creating Git tag: $tagName")

        // Check for uncommitted changes
        val status = ByteArrayOutputStream()
        exec {
            commandLine("git", "status", "--porcelain")
            standardOutput = status
            isIgnoreExitValue  = true // Allow the command to fail without crashing the build
        }
        if (status.toString().isNotEmpty()) {
            throw GradleException("You have uncommitted changes. Please commit or stash them before publishing.")
        }

        // Create the Git tag
        exec {
            commandLine("git", "tag", "-a", tagName, "-m", "Release $tagName")
        }

        // Push the Git tag
        exec {
            commandLine("git", "push", "origin", tagName)
        }

        println("Git tag created and pushed successfully: $tagName")
        println("JitPack will automatically build the library for this tag.")
    }
}

apply(from = "local.gradle")
android {
    namespace = "com.alatpay_checkout_android"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.gson)  // This will resolve the Gson dependency from the TOML file


    //serialize
    implementation(libs.kotlinx.serialization)

    //architectural components for view model and lifecycle stuffs
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.compose.material)
    //truth for fluent assertions
    testImplementation(libs.google.truth)
    androidTestImplementation(libs.google.truth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}