plugins {
    java
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}
allprojects {
    group = "top.mrxiaom"
    version = "1.0.0"

    apply(plugin = "java")
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.rosewooddev.io/repository/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    val targetJavaVersion = 11
    extensions.configure<JavaPluginExtension> {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(targetJavaVersion)
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.4") // NMS
    compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:24.0.0")
    shadow(project(":bungeecord"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to project.version))
            include("plugin.yml")
        }
    }
}
