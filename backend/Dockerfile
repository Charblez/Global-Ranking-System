# --------------------------------------------------
# Build
# --------------------------------------------------
# Set the source image
FROM eclipse-temurin:17-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy over gradle build tools
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Give gradle execution permissions
RUN chmod +x gradlew

# Download dependencies before copying source code
RUN ./gradlew dependencies --no-daemon

# Copy over source files
COPY src src

# Build jar and run without testing
RUN ./gradlew bootJar -x test --no-daemon

# --------------------------------------------------
# Runtime
# --------------------------------------------------
FROM eclipse-temurin:17-alpine

# Create user instead of running as root
RUN addgroup -S statit_group && adduser -S statit_user -G statit_group
USER statit_user

# Set working directory
WORKDIR /app

# Copy the built jar over
COPY --from=builder /app/build/libs/*.jar statit.jar

# Expose to port 8080
EXPOSE 8080

# Set the entrypoint and run commands
ENTRYPOINT ["java", "-jar", "statit.jar"]
