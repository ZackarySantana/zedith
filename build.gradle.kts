plugins {
    java
}

allprojects {
    group = "dev.zedith"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Apply only to actual mod projects (everything under :mods:*)
configure(subprojects.filter { it.path.startsWith(":mods:") }) {
    apply(plugin = "java")

    java {
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        compileOnly(files("${rootProject.projectDir}/libs/HytaleServer.jar"))
        testImplementation(files("${rootProject.projectDir}/libs/HytaleServer.jar"))
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    val manifestFile = layout.projectDirectory.file("src/main/resources/manifest.json")

    val verifyManifest = tasks.register("verifyManifest") {
        group = "verification"
        description = "Verifies that src/main/resources/manifest.json exists for ${project.path}."

        inputs.file(manifestFile)

        doLast {
            val f = manifestFile.asFile
            if (!f.exists()) {
                throw GradleException("Missing required manifest.json for ${project.path}: ${f.path}")
            }
        }
    }

    tasks.named("processResources") {
        dependsOn(verifyManifest)
    }

    val modsDir = providers.environmentVariable("HYTALE_MODS_DIR")

    tasks.register<Copy>("installMod") {
        group = "distribution"
        description = "Copies the built mod jar to HYTALE_MODS_DIR"

        onlyIf { modsDir.orNull?.isNotBlank() == true }

        doFirst {
            if (modsDir.orNull.isNullOrBlank()) {
                logger.lifecycle("HYTALE_MODS_DIR is not set; skipping installMod.")
            } else {
                logger.lifecycle("Installing ${project.name} to: ${modsDir.get()}")
            }
        }

        dependsOn(tasks.jar)

        from(tasks.jar.map { it.archiveFile })

        into(modsDir.map { file(it) })
    }
}
