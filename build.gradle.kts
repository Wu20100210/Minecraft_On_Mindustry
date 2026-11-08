import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import java.util.concurrent.TimeUnit

version = "1.0"

buildscript{
    repositories{
        mavenCentral()
    }
}
//
// Mindustry version to depend on.
// Valid values:
// - latest: depend on the latest release of mindustry
// - be: depend on the very latest commit of mindustry
// - v<number>: depend on a specific version
val mindustryVersion = "v160"
val kotlinVersion = "2.3.20"
val sdkRoot: String? = System.getenv("ANDROID_HOME")
    ?: System.getenv("ANDROID_SDK_ROOT")
    ?: run {
        val localProps = File(rootDir, "local.properties")
        if (localProps.exists()) {
            Properties().apply { localProps.inputStream().use { load(it) } }.getProperty("sdk.dir")
        } else null
    }
val isWindows = System.getProperty("os.name").lowercase().contains("windows")
val modArtifactName = project.name

plugins{
    kotlin("jvm") version "2.3.20"
}

sourceSets.main{
    kotlin.srcDirs("src")
}

repositories{
    mavenCentral()

    ivy{
        url = uri("https://github.com/")
        patternLayout{
            artifact(
                when(mindustryVersion){
                    "latest" -> "/[organisation]/[module]/releases/[revision]/download/dependencies.jar"
                    "be" -> "/[organisation]/[module]/releases/download/master/[revision].jar"
                    else -> "/[organisation]/[module]/releases/download/[revision]/dependencies.jar"
                }
            )
        }
        metadataSources{ artifact() }

        content{
            if(mindustryVersion == "be"){
                includeVersion("Anuken", "MindustryBuilds", "latest")
            }else{
                includeVersion("Anuken", "Mindustry", mindustryVersion)
            }
        }
    }
}

dependencies{
    compileOnly(if(mindustryVersion == "be") "Anuken:MindustryBuilds:latest" else "Anuken:Mindustry:$mindustryVersion")
}

tasks.withType<KotlinCompile>().configureEach{
    compilerOptions{
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.withType<JavaCompile>().configureEach{
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.release.set(8)
}

val jarAndroid = tasks.register("jarAndroid"){
    dependsOn("jar")

    // 声明输出，让 Gradle 能做增量构建
    val androidJarFile = layout.buildDirectory.file("libs/${modArtifactName}Android.jar")
    outputs.file(androidJarFile)

    doLast{
        if (sdkRoot.isNullOrEmpty() || !File(sdkRoot).exists()){
            throw GradleException("No valid Android SDK found. Ensure that ANDROID_HOME is set to your Android SDK directory.")
        }

        val platformRoot = File("$sdkRoot/platforms/").listFiles()
            ?.sorted()
            ?.reversed()
            ?.find{ f -> File(f, "android.jar").exists() }
            ?: throw GradleException("No android.jar found. Ensure that you have an Android platform installed.")

        val classpathFiles = (configurations.compileClasspath.get().toList() +
                configurations.runtimeClasspath.get().toList() +
                listOf(File(platformRoot, "android.jar")))
            .distinct()

        val d8 = if (isWindows) "d8.bat" else "d8"

        // 直接构建参数列表，避免字符串 split 带来的空格/转义问题
        val args = mutableListOf(d8)
        classpathFiles.forEach {
            args += "--classpath"
            args += it.path
        }
        args += listOf(
            "--min-api", "21",
            "--output", "${modArtifactName}Android.jar",
            "${modArtifactName}Desktop.jar"
        )

        logger.lifecycle("执行 d8: ${args.joinToString(" ")}")

        val process = ProcessBuilder(args)
            .directory(layout.buildDirectory.dir("libs").get().asFile)
            .redirectErrorStream(true)
            .start()

        // 立即关闭子进程 stdin，防止 d8 意外等待输入而挂起
        process.outputStream.close()

        // 异步读取输出，避免管道缓冲区满导致死锁
        val output = StringBuilder()
        val reader = Thread {
            process.inputStream.bufferedReader().forEachLine { line ->
                output.appendLine(line)
                logger.info("[d8] $line")
            }
        }
        reader.start()

        // 带超时的等待
        val finished = process.waitFor(10, TimeUnit.MINUTES)
        reader.join(5000)

        if (!finished) {
            process.destroyForcibly()
            throw GradleException("d8 执行超时（10 分钟）。已捕获输出：\n$output")
        }
        if (process.exitValue() != 0) {
            throw GradleException("d8 执行失败，退出码 ${process.exitValue()}。输出：\n$output")
        }
        logger.lifecycle("d8 执行成功，已生成 Android jar")
    }
}

tasks.jar{
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("${modArtifactName}Desktop.jar")

    from(configurations.runtimeClasspath.map{ config -> config.map{ if (it.isDirectory) it else zipTree(it) } })

    from(rootDir){
        include("mod.hjson")
    }

    from("assets/"){
        include("**")
    }
}

val deploy = tasks.register("deploy", Jar::class){
    dependsOn(jarAndroid)
    dependsOn(tasks.jar)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("$modArtifactName.jar")

    from({
        listOf(
            zipTree("${layout.buildDirectory.get().asFile}/libs/${modArtifactName}Desktop.jar"),
            zipTree("${layout.buildDirectory.get().asFile}/libs/${modArtifactName}Android.jar")
        )
    })

    doLast{
        delete(
            "${layout.buildDirectory.get().asFile}/libs/${modArtifactName}Desktop.jar",
            "${layout.buildDirectory.get().asFile}/libs/${modArtifactName}Android.jar"
        )
    }
}