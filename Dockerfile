# Use a specific version of Ubuntu as the builder stage
FROM ubuntu:latest AS build

# Install necessary dependencies for building your application
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

# Copy the application source code into the container
COPY . .

# Install Maven and build the application
RUN apt-get install maven -y
RUN mvn clean install

# Switch to the final image stage
FROM openjdk:17-jdk-slim

# Expose port 8080 (if your application needs this port)
EXPOSE 8080

# Copy the built JAR from the previous stage
COPY --from=build /target/todolist-0.0.1-SNAPSHOT.jar app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
