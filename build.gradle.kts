plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("com.guardian.ui.AgentMain")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "com.guardian.ui.AgentMain"
        )
    }
}
