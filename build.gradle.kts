plugins {
    id("java-library")
    kotlin("jvm")

    id("com.diffplug.spotless") version "8.9.0"

    alias(libs.plugins.run.paper)
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

    compileOnly("com.google.code.gson:gson:2.14.0")

    compileOnly(project(":annotation-processor"))
    annotationProcessor(project(":annotation-processor"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

spotless {
    encoding("UTF-8")

    java {
        // Import hygiene
        removeUnusedImports()
        importOrder("", "\\#")
        forbidWildcardImports()
        forbidModuleImports()

        // Annotation normalization
        formatAnnotations()

        // Java formatting
        princeOfSpace("2.2.0")
            .indentStyle("SPACES")
            .indentSize(4)
            .lineLength(120)
            .wrapStyle("BALANCED")
            .closingParenOnNewLine(true)
            .trailingCommas(false)
            .javaLanguageLevel(25)

        // Source hygiene
        trimTrailingWhitespace()
        endWithNewline()

        // Escape hatch: // spotless:off / // spotless:on
        toggleOffOn()
    }
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("co.aikar.commands", "org.heather.hardlands.libs.acf.commands")
        relocate("co.aikar.locales", "org.heather.hardlands.libs.acf.locales")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms4G", "-Xmx4G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}