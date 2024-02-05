/**
 * plugin for applying the correct kotlin version
 */

plugins {
    id("boudicca-base")
    kotlin("jvm")
}

val jvmVersion: Int by rootProject.ext

kotlin {
    jvmToolchain(jvmVersion)
    compilerOptions {
        javaParameters = true
    }
}
