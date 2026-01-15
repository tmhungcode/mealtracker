# syntax=docker/dockerfile:1
# Multi-stage build for optimized image size and security

# Stage 1: Build stage (not used here since Maven builds outside Docker)
# Keeping this stage for potential future use
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="Hung Tran <tmhung.code@gmail.com>"
LABEL org.opencontainers.image.title="Meal Tracker API"
LABEL org.opencontainers.image.description="REST API for tracking meals and calorie consumption"
LABEL org.opencontainers.image.version="0.0.1-SNAPSHOT"

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy JAR file
ARG JAR_FILE
COPY --chown=spring:spring target/${JAR_FILE} app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 9000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9000/api/actuator/health || exit 1

# JVM optimizations and configuration
ENV JAVA_OPTS="-XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom \
  -Duser.timezone=UTC"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILE:-dev}"]
