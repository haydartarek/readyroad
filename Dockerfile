# ============================================
# ReadyRoad Backend - Multi-stage Dockerfile
# ============================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 readyroad && \
    adduser -D -u 1001 -G readyroad readyroad

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Install su-exec for privilege drop in entrypoint
RUN apk add --no-cache su-exec

# Create required directories and set ownership
RUN mkdir -p /app/logs /app/public/images/quiz && \
    chown -R readyroad:readyroad /app

# Entrypoint: fix volume mount ownership at startup, then run the app
# Named Docker volumes are mounted as root after image build; this corrects
# permissions before the JVM starts so uploads always succeed.
RUN printf '#!/bin/sh\nchown readyroad:readyroad /app/public/images/quiz 2>/dev/null || true\nchmod 755 /app/public/images/quiz 2>/dev/null || true\nexec su-exec readyroad java ${JAVA_OPTS} -jar /app/app.jar "$@"\n' \
    > /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh

# Keep running as root so entrypoint.sh can chown the volume mount;
# su-exec in the script then drops to readyroad before starting Java.

# Expose port
EXPOSE 8890

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8890/actuator/health || exit 1

# Environment variables (with defaults)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms512m -Xmx1024m" \
    TZ=UTC

# Run application (via entrypoint script that fixes volume permissions first)
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
