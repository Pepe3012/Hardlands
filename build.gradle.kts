plugins {
    id("java-library")
    alias(libs.plugins.run.paper)
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")

    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("org.popcraft:chunky-common:1.3.38")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        })
    }

    assemble {
        dependsOn(jar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version)

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

kotlin {
    jvmToolchain(25)
}