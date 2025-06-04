# ---- Build Stage ----
FROM gradle:8.2.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# Dùng thư mục cache an toàn (nằm trong project folder)
ENV GRADLE_USER_HOME=/home/gradle/project/.gradle

USER gradle
RUN gradle clean build --no-daemon

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk
EXPOSE 8080
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
