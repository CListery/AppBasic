import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.jetbrainsKotlinGradle)
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
