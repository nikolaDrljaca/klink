# builder stage
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
# copy all files from root to preserve structure
COPY . .
# Build only the klink-rest module
RUN mvn -pl klink-rest -am clean package -DskipTests

# Use slim JDK image to run built JAR
FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY --from=builder /app/klink-rest/target/klink-rest-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

