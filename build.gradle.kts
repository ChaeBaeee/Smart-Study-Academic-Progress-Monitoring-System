import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    id("org.jetbrains.compose") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.smartstudy"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Compose for Desktop
    implementation(compose.desktop.currentOs)
    
    // Material Icons Extended
    implementation(compose.materialIconsExtended)
    
    // JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

compose.desktop {
    application {
        mainClass = "com.smartstudy.MainKt"
        
        // Disable ProGuard for release builds
        buildTypes.release.proguard {
            isEnabled = false
        }
        
        nativeDistributions {
            // Specify target formats for Windows executable
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            
            packageName = "Smart Study System"
            packageVersion = "1.0.0"
            description = "Academic Progress Monitoring System"
            copyright = "© 2025 Smart Study System"
            vendor = "Smart Study"
            
            windows {
                menuGroup = "Smart Study System"
                upgradeUuid = "18159995-d967-4cd2-8885-77BFA97CFA9F"
                // Add app icon if you have one
                // iconFile.set(project.file("src/main/resources/icon.ico"))
            }
            
            macOS {
                // Add app icon if you have one
                // iconFile.set(project.file("src/main/resources/icon.icns"))
            }
            
            linux {
                // Add app icon if you have one
                // iconFile.set(project.file("src/main/resources/icon.png"))
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// ProGuard is disabled in the compose.desktop block above

// Disable the default jar task to avoid confusion
tasks.jar {
    enabled = false
}

// ShadowJar configuration for creating executable fat JAR with all dependencies
tasks.shadowJar {
    archiveBaseName.set("SmartStudySystem")
    archiveClassifier.set("")
    archiveVersion.set("")
    
    manifest {
        attributes(
            "Main-Class" to "com.smartstudy.MainKt"
        )
    }
    
    // Merge service files to avoid conflicts
    mergeServiceFiles()
    
    // Exclude signature files to avoid conflicts
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    
    // Shadow plugin automatically includes all runtime dependencies by default
    // This includes Kotlin stdlib, Compose Desktop, and all other dependencies
}
