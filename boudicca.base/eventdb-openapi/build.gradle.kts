import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.openapi.generator") version "7.2.0"
    `java-library`
    `maven-publish`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(rootProject.ext["jvmVersion"] as Int))
    }
}

val openapi by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val jacksonVersion = "2.16.0"
val jakartaAnnotationVersion = "1.3.5"

dependencies {
    openapi(files("src/main/resources/api-docs.json"))

    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakartaAnnotationVersion")
    api(project(":boudicca.base:semantic-conventions"))
}

tasks.withType<GenerateTask> {
    inputs.files(openapi)
    inputSpec.set(openapi.files.first().path)
}

tasks.register<GenerateTask>("generateJavaClient") {
    inputs.files(openapi)
    inputSpec.set(openapi.files.first().path)
    outputDir.set(layout.buildDirectory.dir("/generated/java").get().toString())
    generatorName.set("java")
    library.set("native")
    generateApiTests.set(false)
    generateModelTests.set(false)
    additionalProperties.put("supportUrlQuery", "false")
    invokerPackage.set("base.boudicca.openapi")
    apiPackage.set("base.boudicca.eventdb.api")
    modelPackage.set("base.boudicca.eventdb.model")
}

tasks.register<GenerateTask>("generateTypescriptClient") {
    inputs.files(openapi)
    inputSpec.set(openapi.files.first().path)
    outputDir.set(layout.buildDirectory.dir("/generated/typescript").get().toString())
    generatorName.set("typescript-axios")
    configOptions.putAll(
        mapOf(
            "npmName" to "@boudicca/eventdb-api-client",
            "npmVersion" to "${project.version}",
            "supportsES6" to "true",
        )
    )
}

sourceSets {
    main {
        java {
            srcDir(file(layout.buildDirectory.dir("/generated/java").get().toString()))
        }
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named<GenerateTask>("generateJavaClient"))
}

publishing {
    publications {
        create<MavenPublication>("eventdb-client") {
            from(components["java"])
        }
    }
}