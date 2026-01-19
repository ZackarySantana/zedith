import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.os.OperatingSystem
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

val libsDir = rootProject.layout.projectDirectory.dir("libs")
val credentialsFile = rootProject.layout.buildDirectory.file("hytale/.hytale-downloader-credentials.json")
val downloadDir = rootProject.layout.buildDirectory.dir("hytale/download")
val hytaleZip = downloadDir.map { it.file("hytale.zip") }

/**
 * Based on and adapted from FastStats dev-kit:
 * https://github.com/faststats-dev/dev-kits/tree/1881337f212cb16b9832162e4e6cf2018a82beb8/hytale
 *
 * Original authors retain credit for the downloader logic.
 */
val downloadServer = rootProject.tasks.register("download-server") {
    doLast {
        if (hytaleServerJar.exists()) {
            println("HytaleServer.jar already exists, skipping download")
            return@doLast
        }

        val downloaderZip = downloadDir.map { it.file("hytale-downloader.zip") }

        libsDir.asFile.mkdirs()
        downloadDir.get().asFile.mkdirs()

        val os = OperatingSystem.current()
        val downloaderExecutable = when {
            os.isLinux -> downloadDir.map { it.file("hytale-downloader-linux-amd64") }
            os.isWindows -> downloadDir.map { it.file("hytale-downloader-windows-amd64.exe") }
            else -> throw GradleException("Unsupported operating system: ${os.name}")
        }

        if (!downloaderExecutable.get().asFile.exists()) {
            if (!downloaderZip.get().asFile.exists()) {
                ant.invokeMethod(
                    "get",
                    mapOf(
                        "src" to "https://downloader.hytale.com/hytale-downloader.zip",
                        "dest" to downloaderZip.get().asFile.absolutePath
                    )
                )
            } else {
                println("hytale-downloader.zip already exists, skipping download")
            }

            copy {
                from(zipTree(downloaderZip))
                include(downloaderExecutable.get().asFile.name)
                into(downloadDir)
            }
        } else {
            println("Hytale downloader binary already exists, skipping download and extraction")
        }

        downloaderZip.get().asFile.delete()

        downloaderExecutable.get().asFile.setExecutable(true)

        val credentials = System.getenv("HYTALE_DOWNLOADER_CREDENTIALS")
        if (!credentials.isNullOrBlank()) {
            val credFile = credentialsFile.get().asFile
            credFile.parentFile.mkdirs()
            if (!credFile.exists()) {
                credFile.writeText(credentials)
                println("Wrote downloader credentials to ${credFile.absolutePath}")
            }
        }

        if (!hytaleZip.get().asFile.exists()) {
            val processBuilder = ProcessBuilder(
                downloaderExecutable.get().asFile.absolutePath,
                "-download-path",
                "hytale",
                "-credentials-path",
                credentialsFile.get().asFile.absolutePath
            )
            processBuilder.directory(downloadDir.get().asFile)
            processBuilder.redirectErrorStream(true)

            val process = processBuilder.start()
            process.inputStream.bufferedReader().useLines { lines -> lines.forEach(::println) }

            val exitCode = process.waitFor()
            if (exitCode != 0) throw GradleException("Hytale downloader failed with exit code: $exitCode")
        } else {
            println("hytale.zip already exists, skipping download")
        }

        if (!hytaleZip.get().asFile.exists()) {
            throw GradleException("hytale.zip not found at ${hytaleZip.get().asFile.absolutePath}")
        }

        // Extract only Server/HytaleServer.jar
        copy {
            from(zipTree(hytaleZip))
            include("Server/HytaleServer.jar")
            into(downloadDir)
        }

        val extracted = downloadDir.get().file("Server/HytaleServer.jar").asFile
        if (!extracted.exists()) {
            throw GradleException("HytaleServer.jar was not found in Server/ subdirectory")
        }

        extracted.copyTo(hytaleServerJar, overwrite = true)
        downloadDir.get().dir("Server").asFile.deleteRecursively()
        hytaleZip.get().asFile.delete()

        if (!hytaleServerJar.exists()) {
            throw GradleException("HytaleServer.jar extraction failed")
        }

        println("Extracted ${hytaleServerJar.absolutePath}")
    }
}

rootProject.tasks.register("update-server") {
    dependsOn(tasks.named("download-server"))

    doFirst {
        hytaleServerJar.delete()
        hytaleZip.get().asFile.delete()
    }
}

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
        add("compileOnly", files(hytaleServerJar))
        add("testCompileOnly", files(hytaleServerJar))
        add("testRuntimeOnly", files(hytaleServerJar))
    }

    tasks.withType<JavaCompile>().configureEach {
        dependsOn(downloadServer)
    }

    tasks.withType<Test>().configureEach {
        dependsOn(downloadServer)

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
