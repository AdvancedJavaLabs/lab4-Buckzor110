import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id ("com.gradleup.shadow") version "9.0.0-beta4"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}


tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "lab4.buckzor110.App"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation("org.apache.hadoop:hadoop-common:3.4.1")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:3.4.1")
    implementation("org.apache.hadoop:hadoop-client:3.4.1")


    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.1-jre")
}

application {
    // Define the main class for the application.
    mainClass.set("lab4.buckzor110.App")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
