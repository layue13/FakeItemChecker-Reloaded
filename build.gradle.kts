plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "com.layue13"
version = "2.0.0"

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
    
    // Database
    implementation("com.zaxxer:HikariCP:4.0.3")
    
    // Logging
    implementation("org.slf4j:slf4j-api:1.7.36")
    
    // Configuration
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
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

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<ProcessResources> {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = Charsets.UTF_8.name()
    filesMatching("*.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    minimize()
}
