rootProject.name = "zedith"

include(":mods")
file("mods")
    .listFiles()
    ?.filter { it.isDirectory && File(it, "build.gradle.kts").exists() }
    ?.forEach { dir ->
        include(":mods:${dir.name}")
        project(":mods:${dir.name}").projectDir = dir
    }