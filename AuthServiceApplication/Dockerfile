FROM eclipse-temurin:17-jammy
# Set the working directory inside the container
WORKDIR /app

# Copy the gradle build files
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .
COPY gradle ./gradle
COPY entrypoint.sh /entrypoint.sh

# Make the script executable
RUN chmod +x /entrypoint.sh

# Copy the source code
COPY src ./src

# Build the application using Gradle
RUN ./gradlew build

# Expose the port for your application
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["/entrypoint.sh"]
