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
    implementation(buildSrcLibs.androidBuildToolGradle)
    implementation(buildSrcLibs.jetbrainsKotlinGradle)
    implementation(buildSrcLibs.jetbrainsKotlinAndroidGradle)
    implementation(buildSrcLibs.jfrogBuildInfoExtractorGradle)
    implementation(buildSrcLibs.dokkaGradle)
}
