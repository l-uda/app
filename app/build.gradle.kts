plugins {
    id(Plugins.androidApplication)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinExtensions)

    id("name.remal.check-dependency-updates") version "1.0.193"
}

android {

    compileSdkVersion(Configs.compileSdkVersion)
    defaultConfig {
        applicationId = Configs.applicationId
        minSdkVersion(Configs.minSdkVersion)
        targetSdkVersion(Configs.targetSdkVersion)
        versionCode = Configs.versionCode
        versionName = Configs.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile(ProGuards.proguardTxt), ProGuards.androidDefault)        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {


    implementation(project(":core"))

    implementation("com.intentfilter:android-permissions:2.0.54")
    implementation("androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")

    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")

    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}")
    implementation("io.reactivex.rxjava2:rxjava:2.2.10")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")


}