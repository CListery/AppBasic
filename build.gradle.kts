import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath(libs.jetbrainsKotlinGradle)
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
