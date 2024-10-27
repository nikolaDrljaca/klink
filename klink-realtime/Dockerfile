# # Stage 1: Cache Gradle dependencies
# FROM gradle:latest AS cache
# RUN mkdir -p /home/gradle/cache_home
# ENV GRADLE_USER_HOME /home/gradle/cache_home
# COPY build.gradle.* gradle.properties /home/gradle/app/
# WORKDIR /home/gradle/app
# RUN gradle clean build -i --stacktrace
#
# # Stage 2: Build Application
# FROM gradle:latest AS build
# COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
# COPY . /usr/src/app/
# WORKDIR /usr/src/app
# COPY --chown=gradle:gradle . /home/gradle/src
# WORKDIR /home/gradle/src
# # Build the fat JAR
# RUN gradle buildFatJar --no-daemon
#
# # Stage 3: Create the Runtime Image
# FROM amazoncorretto:22 AS runtime
# EXPOSE 8081
# RUN mkdir /app
# COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-docker-sample.jar
# ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]

### 
FROM gradle:8-jdk17-jammy as builder
WORKDIR /app
COPY . .
COPY /src/main/resources/api.yaml ./src/main/resources/api.yaml
RUN gradle clean build

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

