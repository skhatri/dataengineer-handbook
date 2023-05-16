import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val sparkVersion = "3.4.0"
val scalaVersion = "2.12"

val spark by configurations.creating
val uber by configurations.creating
val hadoop by configurations.creating

plugins {
    id("java")
    id("scala")
    id("idea")
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories{
  mavenCentral()
}

dependencies {
    implementation("org.scala-lang:scala-compiler:2.12.17")
    implementation("org.scala-lang:scala-library:2.12.17")

    listOf("spark-core", "spark-sql", "spark-kubernetes").forEach { name ->
        spark("org.apache.spark:${name}_${scalaVersion}:$sparkVersion")
    }


    uber("com.datastax.spark:spark-cassandra-connector_2.12:3.3.0")
    uber("com.datastax.oss:java-driver-core-shaded:4.15.0")
    uber("com.datastax.oss:java-driver-core:4.15.0")
    uber("com.github.jnr:jnr-posix:3.1.16")

    uber("com.fasterxml.jackson.module:jackson-module-scala_2.12:2.14.2") {
      exclude(module="scala-library")
    }

    implementation("io.netty:netty-codec:4.1.91.Final")
    hadoop("org.apache.hadoop:hadoop-aws:3.3.4")
    hadoop("org.apache.hadoop:hadoop-common:3.3.4")
    hadoop("com.amazonaws:aws-java-sdk-s3:1.12.262")

    implementation(spark)
    implementation(uber)
    implementation(hadoop)
}

tasks.withType<ShadowJar> {
    isZip64 = true
    configurations = listOf(uber)
    dependencies {
        exclude(listOf("**/pom.properties", "**/pom.xml"))
    }
}

tasks.build {
    finalizedBy("shadowJar")
}


task("runApp", JavaExec::class) {
    main = "demo.ParquetReadWrite"
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf(
        "-Xms512m", "-Xmx512m",
        "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED"
    )
}

