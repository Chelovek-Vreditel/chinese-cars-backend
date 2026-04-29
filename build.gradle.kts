plugins {
	java
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.github.ChelovekVreditel"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // DB migrations system
    implementation("org.flywaydb:flyway-core:12.1.0")
    implementation("org.flywaydb:flyway-database-postgresql:12.1.0")
    // HTML parser
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("com.microsoft.playwright:playwright:1.58.0")
    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
    // Modules for tests
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.testcontainers:postgresql:1.21.4")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    // Environmental variables from .env file
    implementation(platform("me.paulschwarz:spring-dotenv-bom:5.1.0"))
    developmentOnly("me.paulschwarz:springboot4-dotenv")
    // XML parsing
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.5")
    runtimeOnly("com.sun.xml.bind:jaxb-impl:4.0.7")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
