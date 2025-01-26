dependencies {
    compileOnly("net.md-5:bungeecord-api:1.20-R0.3-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:24.0.0")
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to project.version))
            include("bungee.yml")
        }
    }
    jar {
        archiveBaseName.set("${rootProject.name}-${project.name}")
        destinationDirectory.set(File(rootProject.projectDir, "out"))
    }
}
