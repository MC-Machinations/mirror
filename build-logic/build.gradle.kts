plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.licenser)
}

tasks {
    compileKotlin {
        kotlinOptions.languageVersion = "17"
    }
}
