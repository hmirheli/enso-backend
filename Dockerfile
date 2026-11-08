# ---- build stage -------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only the pom first so the dependency layer is cached between builds.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---- runtime stage -----------------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Do not run the application as root.
RUN useradd --system --create-home enso
COPY --from=build /workspace/target/*.jar app.jar
USER enso

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
