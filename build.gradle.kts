plugins {
    java
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.gmazzo.buildconfig") version "3.1.0" apply false
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
        maven("https://jitpack.io/")
        maven("https://repo.papermc.io/repository/maven-public/")
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

configurations.create("shadowLink")
dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-buffer:4.1.97.Final")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("fr.xephi:authme:5.6.0-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:24.0.0")
    "shadowLink"(project(":bungeecord"))
    "shadowLink"(project(":velocity"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.getByName("shadowLink"))
    }
    build {
        dependsOn(shadowJar)
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to project.version))
            include("plugin.yml")
        }
    }
}
