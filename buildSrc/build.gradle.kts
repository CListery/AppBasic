plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(Dependencies.GradlePlugin.android)
    implementation(Dependencies.GradlePlugin.kotlin)
    implementation(Dependencies.GradlePlugin.androidKotlin)
    implementation(Dependencies.GradlePlugin.jfrog)
    implementation(Dependencies.GradlePlugin.dokka)
}
