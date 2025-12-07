# -------------------------
# Build Stage 
# -------------------------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# gradlew 및 소스 복사
COPY . .

# 권한 부여
RUN chmod +x gradlew

# 테스트는 제외하고 빌드
RUN ./gradlew clean build -x test --no-daemon

# -------------------------
# Run Stage
# -------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 결과만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${SERVER_PORT:-80}"]