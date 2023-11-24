plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
}

group = "com.layue13"
version = "1.0.2"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "code-mc"
        url = uri("https://repo.codemc.io/repository/nms/")
    }
}

dependencies {
    compileOnly(fileTree("libs") {
        include("*.jar")
    })
    compileOnly("org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT")
}

val targetJavaVersion = 8
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() != javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<ProcessResources> {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = Charsets.UTF_8.name()
    filesMatching("*.yml") {
        expand(props)
    }
}
