plugins {
    id("java-library")
    kotlin("jvm")

    alias(libs.plugins.run.paper)
    alias(libs.plugins.lombok)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.chunky.common)

    implementation(libs.acf.paper)

    constraints {
        compileOnly(libs.commons.lang3) {
            because("Fixes CVE-2025-48924")
        }

        compileOnly(libs.plexus.utils) {
            because("Fixes CVE-2025-67030")
        }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

lombok {
    version = libs.versions.lombok.get()
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
    }

    shadowJar {
        archiveClassifier.set("")

        relocate("co.aikar.commands", "com.hardlands.libs.acf.commands")
        relocate("co.aikar.locales", "com.hardlands.libs.acf.locales")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version)

        inputs.properties(props)

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}