import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar

plugins {
    base
}

allprojects {
    group = "dev.zedith"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

val hytaleServerJar = rootProject.file("libs/HytaleServer.jar")

// Apply only to actual mod projects (everything under :mods:*)
configure(subprojects.filter { it.path.startsWith(":mods:") }) {
    apply(plugin = "java")

    extensions.configure<JavaPluginExtension>("java") {
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        add("testImplementation", platform("org.junit:junit-bom:5.10.0"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")

        if (hytaleServerJar.exists()) {
            add("compileOnly", files(hytaleServerJar))
            add("testCompileOnly", files(hytaleServerJar))
            add("testRuntimeOnly", files(hytaleServerJar))
        }
    }

    tasks.withType<Test>().configureEach {
        if (!hytaleServerJar.exists()) {
            doFirst {
                logger.lifecycle("libs/HytaleServer.jar not found — skipping @Tag(\"hytale\") tests.")
            }
        }

        useJUnitPlatform {
            if (!hytaleServerJar.exists()) {
                excludeTags("hytale")
            }
        }

        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true

            debug {
                events = setOf(
                    TestLogEvent.STARTED,
                    TestLogEvent.FAILED,
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.STANDARD_OUT
                )
                exceptionFormat = TestExceptionFormat.FULL
            }
            info.events = debug.events
            info.exceptionFormat = debug.exceptionFormat
        }

        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent == null) {
                    val output =
                        "Results: ${result.resultType} " +
                                "(${result.testCount} tests, " +
                                "${result.successfulTestCount} passed, " +
                                "${result.failedTestCount} failed, " +
                                "${result.skippedTestCount} skipped)"

                    val startItem = "|  "
                    val endItem = "  |"
                    val lineLength = startItem.length + output.length + endItem.length
                    val line = "-".repeat(lineLength)

                    println("\n$line\n$startItem$output$endItem\n$line")
                }
            }

            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        })
    }

    tasks.withType<Jar>().configureEach {
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


        dependsOn(tasks.named("jar"))
        from(tasks.named<Jar>("jar").map { it.archiveFile })

        into(modsDir.map { file(it) })
    }
}
