import java.util.Properties
import java.io.FileInputStream
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "com.example.b07group6"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.b07group6"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        var supabaseUrl = localProperties.getProperty("SUPABASE_URL", "")
        var supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY", "")
        var supabaseImageBucket = localProperties.getProperty("SUPABASE_IMAGE_BUCKET", "")
        buildConfigField("String", "SUPABASE_URL", "\"${supabaseUrl}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${supabaseAnonKey}\"")
        buildConfigField("String", "SUPABASE_IMAGE_BUCKET", "\"${supabaseImageBucket}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.fragment)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.23.0")
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))
    implementation("androidx.activity:activity:1.9.0") // Hopefully adds the edge-to-edge compatability ;(
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.bumptech.glide:glide:5.0.5")
}

// Task to create Javadocs, as Android Studio Javadoc creation seems to be sort of broken
// use './gradlew :app:generateJavadoc --no-configuration-cache' to run it. Using the run
// button in Android Studio doesn't seem to work either
tasks.register<Javadoc>("generateJavadoc") {
    description = "Generates Javadoc for the project"
    group = "documentation"
    source = fileTree("src/main/java")
    val compileTaskProvider = tasks.named<JavaCompile>("compileDebugJavaWithJavac")
    dependsOn(compileTaskProvider)
    val androidComponents = project.extensions.getByType<com.android.build.api.variant.ApplicationAndroidComponentsExtension>()
    val bootClasspathProvider = androidComponents.sdkComponents.bootClasspath
    doFirst {
        val compileTask = compileTaskProvider.get()
        val bootClasspath = bootClasspathProvider.get().map { it.asFile.absolutePath }.joinToString(File.pathSeparator)
        classpath = files(
            bootClasspath,
            compileTask.classpath,
            "${layout.buildDirectory.get()}/generated/source/buildConfig/debug",
            "${layout.buildDirectory.get()}/intermediates/javac/debug/compileDebugJavaWithJavac/classes"
        )
    }
    destinationDir = file("${layout.buildDirectory.get().asFile}/outputs/javadoc")
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        docEncoding = "UTF-8"
        addStringOption("Xdoclint:none", "-quiet")
        links("https://developer.android.com/reference/")
        links("https://docs.oracle.com/en/java/javase/11/docs/api/")
        addBooleanOption("linksource", true)
    }
    exclude("**/BuildConfig.java")
    exclude("**/R.java")
}
