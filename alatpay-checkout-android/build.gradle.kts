import java.io.ByteArrayOutputStream

plugins {
//    alias(libs.plugins.android.library)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.parcelize)
    id ("com.android.library")
    id ("kotlin-android")
    id ("maven-publish")
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")

    if (project.plugins.hasPlugin("com.android.library")) {
        // For Android libraries
        from(android.sourceSets["main"].java.srcDirs)
    } else {
        // For pure Kotlin libraries
        from(sourceSets["main"].java.srcDirs)
    }
}

artifacts {
    archives(tasks["androidSourcesJar"])
}

// Versioning
val libraryVersionName by extra { "1.0.0" } // Update this for each release
val libraryVersionCode by extra { 1 } // Update this for each release

group = "com.github.ALATPay" // Use ALATPay GitHub username here
version = libraryVersionName // Version should be updated with each release

//afterEvaluate {
//    tasks.withType<org.gradle.jvm.tasks.Jar>().configureEach {
//        archiveBaseName.set("alatpay-kotlin") // Replace with your library name
//    }
//}




// Git Tag Task (Cross-Platform)
tasks.register("publishToJitPack") {
    doLast {
        val tagName = "v$libraryVersionName"
        println("Creating Git tag: $tagName")

        // Set a default Git user identity (to avoid JitPack error)
        exec {
            commandLine("git", "config", "--global", "user.name", "Seun Ajibade")
        }
        exec {
            commandLine("git", "config", "--global", "user.email", "ajibadeseun@gmail.com")
        }

        // Check for uncommitted changes
        val status = ByteArrayOutputStream()
        exec {
            commandLine("git", "status", "--porcelain")
            standardOutput = status
            isIgnoreExitValue = true // Allow the command to fail without crashing the build
        }

        // Create the Git tag
        exec {
            commandLine("git", "tag", "-a", tagName, "-m", "Release $tagName")
        }

        // Push the Git tag (ensure 'alatpay-kotlin' is correct remote name)
        exec {
            commandLine("git", "push", "alatpay-kotlin", tagName)
        }

        println("Git tag created and pushed successfully: $tagName")
        println("JitPack will automatically build the library for this tag.")
    }
}


//apply(from = "local.gradle")
android {
    flavorDimensions += listOf("build")
    namespace = "com.alatpay_checkout_android"
    compileSdk = 35
//    defaultPublishConfig = "prodDebug" // Default to prodDebug if not specified
    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

//    productFlavors {
//        create("prod") {
//            dimension = "build"
//            matchingFallbacks += listOf("prod") // Match the "prod" flavor of the producer
//            buildConfigField ("String", "ENV", "\"prod\"")
//            buildConfigField ("String", "DEV_CHECKOUT_URL", "\"https://alatpay-client.azurewebsites.net/js/alatpay.js\"")
//            buildConfigField ("String", "STAGING_CHECKOUT_URL", "\"https://alatpay-client-sandbox.azurewebsites.net/js/alatpay.js\"")
//            buildConfigField ("String", "PROD_CHECKOUT_URL", "\"https://alatpay.ng/js/alatpay.js\"")
//            buildConfigField ("String", "SIGNING_KEY_ID", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SIGNING_PASSWORD", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SIGNING_KEY", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "OSSRH_USERNAME", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "OSSRH_PASSWORD", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SONATYPE_STAGING_PROFILE_ID", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "BASE_URL", "\"https://alatpay.ng\"")
//        }
//        create("staging") {
//            dimension = "build"
//            matchingFallbacks += listOf("staging") // Match the "staging" flavor of the producer
//            buildConfigField ("String", "ENV", "\"staging\"")
//            buildConfigField ("String", "DEV_CHECKOUT_URL", "\"https://alatpay-client.azurewebsites.net/js/alatpay.js\"")
//            buildConfigField ("String", "STAGING_CHECKOUT_URL", "\"https://alatpay-client-sandbox.azurewebsites.net/js/alatpay.js\"")
//            buildConfigField ("String", "PROD_CHECKOUT_URL", "\"https://alatpay.ng/js/alatpay.js\"")
//            buildConfigField ("String", "SIGNING_KEY_ID", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SIGNING_PASSWORD", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SIGNING_KEY", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "OSSRH_USERNAME", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "OSSRH_PASSWORD", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SONATYPE_STAGING_PROFILE_ID", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "BASE_URL", "\"https://alatpay-client-sandbox.azurewebsites.net\"")
//        }
//
//        create("dev") {
//            dimension = "build"
//            matchingFallbacks += listOf("dev") // Match the "dev" flavor of the producer
//            buildConfigField ("String", "ENV", "\"dev\"")
//            buildConfigField ("String", "DEV_CHECKOUT_URL", "\"https://alatpay-client.azurewebsites.net/js/alatpay.js\"")
//            buildConfigField ("String", "STAGING_CHECKOUT_URL", "\"https://alatpay-client-sandbox.azurewebsites.net/js/alatpay.js\"")
//            buildConfigField ("String", "PROD_CHECKOUT_URL", "\"https://alatpay.ng/js/alatpay.js\"")
//            buildConfigField ("String", "SIGNING_KEY_ID", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SIGNING_PASSWORD", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SIGNING_KEY", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "OSSRH_USERNAME", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "OSSRH_PASSWORD", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "SONATYPE_STAGING_PROFILE_ID", "\"XXXXXXXXXXXXXXX\"")
//            buildConfigField ("String", "BASE_URL", "\"https://alatpay-client.azurewebsites.net\"")
//        }
//    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // Two artifacts: the `aar` (or `jar`) and the sources
                if (project.plugins.hasPlugin("com.android.library")) {
                    from(components["release"])
                } else {
                    from(components["java"])
                }

//                artifact(tasks["androidSourcesJar"])

                groupId = "com.github.ALATPay"
                artifactId = "alatpay-kotlin"
                //version = libraryVersionName

                // Define POM metadata (optional, but recommended)
                pom {
                    name.set("ALATPay Android Kotlin SDK")
                    description.set("A simple and efficient way to integrate ALATPay into your Android applications using the Kotlin SDK. Easily accept payments and manage transactions.")
                    url.set("https://github.com/ALATPay/alatpay-kotlin.git")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("ajibadeseun")
                            name.set("Seun Ajibade")
                            email.set("ajibadeseun@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/ALATPay/alatpay-kotlin.git")
                        developerConnection.set("scm:git:ssh://github.com/ALATPay/alatpay-kotlin.git")
                        url.set("https://github.com/ALATPay/alatpay-kotlin.git")
                    }
                }
            }
        }
    }


//    tasks.named("assembleRelease") {
//        finalizedBy("publishToJitPack")
//    }
}

//publishing {
//    publications {
//        create<MavenPublication>("release") {
//            from(components["release"])
//
//            groupId = "com.github.ALATPay" // Replace with your GitHub username or organization
//            artifactId = "alatpay-kotlin" // Replace with your repository name
//            version = libraryVersionName // Replace with your version
//
//            // Define POM metadata (optional, but recommended)
//            pom {
//                name.set("ALATPay Android Kotlin SDK")
//                description.set("A simple and efficient way to integrate ALATPay into your Android applications using the Kotlin SDK. Easily accept payments and manage transactions.")
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