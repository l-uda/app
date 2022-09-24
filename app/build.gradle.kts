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
            applicationIdSuffix = ".release"
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(getDefaultProguardFile(ProGuards.proguardTxt), ProGuards.androidDefault)
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    flavorDimensions("version")
    productFlavors {
        create("stage") {
            // Assigns this product flavor to the "version" flavor dimension. If you are using only one dimension, this property is optional, and the plugin automatically assigns all the module's flavors to that dimension.
            dimension = "version"
            applicationIdSuffix = ".stage"
            versionNameSuffix = "-stage"
//            buildConfigField("String", "server_url", "\"https://www.sagosoft.it/_API_/cpim/luda/www/luda_20210111_1500/api/app/\"")
//            buildConfigField("String", "server_url", "\"https://www.sagosoft.it/_API_/cpim/luda/www/luda_20220202_2200/api/app/\"")
//            buildConfigField("String", "server_url", "\"http://127.0.0.1:80/api/app/\"")
           buildConfigField("String", "server_url", "\"https://luda.nixo.xyz/api/app/\"")
            //buildConfigField("String", "server_url", "\"http://192.168.1.250:8080/api/app/\"")
        }
        create("production") {
            dimension = "version"
            applicationIdSuffix = ".production"
            versionNameSuffix = "-production"
           // buildConfigField("String", "server_url", "\"http://192.168.1.250:80/luda01/api/app/\"")
           //buildConfigField("String", "server_url", "\"http://192.168.1.47:5000/api/app/\"")
            // buildConfigField("String", "server_url", "\"http://10.42.0.164:5000/api/app/\"")
            buildConfigField("String", "server_url", "\"https://luda.nixo.xyz/api/app/\"")
           //buildConfigField("String", "server_url", "\"http://192.168.1.250:8080/api/app/\"")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
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
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    debugImplementation("androidx.fragment:fragment-testing:${Versions.fragment}")


}