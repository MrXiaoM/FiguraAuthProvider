plugins {
    id ("com.github.gmazzo.buildconfig")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    compileOnly("org.jetbrains:annotations:24.0.0")
}

buildConfig {
    className("BuildConstants")
    packageName("top.mrxiaom.figura.velocity")
    useJavaOutput()
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

val targetJavaVersion = 17
tasks {
    withType<JavaCompile>().configureEach {
        options.release.set(targetJavaVersion)
    }
    jar {
        archiveBaseName.set("${rootProject.name}-${project.name}")
    }
}
