# syntax=docker/dockerfile:1

FROM maven:3.9.16-eclipse-temurin-17-noble AS build

WORKDIR /workspace

# Resolve dependencies before copying source so this layer remains cacheable.
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn --batch-mode --no-transfer-progress clean package -DskipTests


FROM eclipse-temurin:17.0.19_10-jre-noble AS runtime

WORKDIR /app

COPY --from=build --chown=10001:10001 \
    /workspace/target/agape_hill_backend-*.jar /app/app.jar

# Let the JVM size its heap from the container memory limit. Render can
# override JAVA_TOOL_OPTIONS without rebuilding the image.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

# Render routes traffic to the PORT environment variable. Spring reads it from
# application.properties; 10000 documents the production container port.
EXPOSE 10000

# A numeric non-root user avoids adding operating-system packages or accounts.
USER 10001:10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
