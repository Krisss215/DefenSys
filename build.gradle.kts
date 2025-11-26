// build.gradle.kts – full working version with PDF reports, without icon

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("edu.sc.seis.launch4j") version "3.0.5"
}

group = "com.elderguardian"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // hardware monitoring (USB / HDMI / etc.)
    implementation("com.github.oshi:oshi-core:6.6.5")

    // PDF reports
    implementation("com.github.librepdf:openpdf:1.3.39")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    // your real main class (from screenshot)
    mainClass.set("com.guardian.ui.AgentMain")
}

tasks.test {
    useJUnitPlatform()
}

// build fat-jar with Shadow
tasks.withType<ShadowJar> {
    archiveBaseName.set("ElderGuardianAgent")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
}

// Launch4j: build .exe from fat-jar
launch4j {
    // main class
    mainClassName = "com.guardian.ui.AgentMain"

    // exe file name
    outfile = "ElderGuardianAgent.exe"

    // IMPORTANT: in Kotlin DSL use setJarTask(...)
    setJarTask(tasks.named<ShadowJar>("shadowJar").get())

    // no icon → no error
    jreMinVersion = "17"
}
