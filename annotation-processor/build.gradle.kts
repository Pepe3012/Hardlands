plugins {
    id("java")
}

version = project.version
group = project.group

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}