FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY . .
RUN mvn -B -P release clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Articles live on a volume so they survive container rebuilds. Run a single instance only.
ENV ARTICLES_DIR=/data/articles
ENV SERVER_ADDRESS=0.0.0.0
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=50 -XX:+ExitOnOutOfMemoryError"
VOLUME /data/articles

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
