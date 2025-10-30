FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

ENV TZ=America/Sao_Paulo \
    SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

RUN useradd -ms /bin/bash appuser


COPY --from=build /workspace/target/*.jar /app/app.jar

RUN mkdir -p /app/data /app/uploads && chown -R appuser:appuser /app

EXPOSE 8080
USER appuser

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]
