// build.gradle.kts – DefenSys build script (PDF reports, JDK 21, ShadowJar + Launch4j)

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("edu.sc.seis.launch4j") version "3.0.5"
}

group = "com.elderguardian"      // можно оставить старую группу, это не видно пользователю
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

// используем локально установленную JDK 21
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    // главный класс UI
    mainClass.set("com.guardian.ui.AgentMain")
}

tasks.test {
    useJUnitPlatform()
}

// ShadowJar: собираем fat-jar с именем DefenSys.jar
tasks.withType<ShadowJar> {
    archiveBaseName.set("DefenSys")  // <-- имя jar-файла
    archiveClassifier.set("")        // без "-all"
    archiveVersion.set("")           // без версии
    mergeServiceFiles()
}

// Launch4j: собираем DefenSys.exe из fat-jar
launch4j {
    // главный класс
    mainClassName = "com.guardian.ui.AgentMain"

    // имя exe-файла
    outfile = "DefenSys.exe"        // <-- имя .exe

    // для Kotlin DSL:
    setJarTask(tasks.named<ShadowJar>("shadowJar").get())

    // целимся в JRE 21
    jreMinVersion = "21"
}
