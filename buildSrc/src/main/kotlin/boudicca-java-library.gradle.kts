import org.gradle.jvm.toolchain.JavaLanguageVersion

/**
 * plugin for applying the correct java version
 */
plugins {
    id("boudicca-base")
    `java-library`
}

val jvmVersion: Int by rootProject.ext

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmVersion))
    }
}