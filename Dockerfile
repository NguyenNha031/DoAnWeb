# ---- Build Stage ----
FROM gradle:8.2.1-jdk17 AS build

COPY . /home/gradle/project
WORKDIR /home/gradle/project

# Gán quyền thực thi cho gradlew
RUN chmod +x ./gradlew

# Dùng wrapper nếu có
RUN ./gradlew clean build --no-daemon --no-build-cache

# ✅ In ra danh sách file trong build/libs để kiểm tra
RUN ls -lh /home/gradle/project/build/libs

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk
EXPOSE 8888
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
