plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("kapt") version "2.0.21"
    id("org.springframework.boot") version "3.5.9"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.kamae.family"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
    }
}

dependencies {
    //Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //Spring-security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-config")
    //Spring-cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    //Aspect
    implementation("org.springframework.boot:spring-boot-starter-aop")
    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    //Telegram
    implementation("org.telegram:telegrambots:6.9.7.1")
    //Arrow
    implementation("io.arrow-kt:arrow-core:2.0.0")
    //Store
    implementation("org.postgresql:postgresql:42.7.3")
    runtimeOnly("com.h2database:h2")
    implementation("org.liquibase:liquibase-core:5.0.1")
    implementation("org.zalando:logbook-spring-boot-starter:3.7.2")
    implementation("org.zalando:logbook-openfeign:3.7.2")

    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.14.7")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()

    jvmArgs = listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED"
    )
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "io.kamae.family.bot.FamilyBotApplication"
        )
    }
    archiveBaseName.set("family-bot")
    archiveClassifier.set("")
    archiveVersion.set("${project.version}-${System.getenv("GITHUB_RUN_NUMBER") ?: "local"}")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}


kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

