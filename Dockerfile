# syntax=docker/dockerfile:1

########################################
# 1) BUILD STAGE (Maven + JDK 17)
########################################
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1. baixar dependências
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# 2. copiar código e empacotar
COPY src src
RUN mvn -q -DskipTests package

########################################
# 2) RUNTIME STAGE (JRE 17 enxuto)
########################################
FROM eclipse-temurin:17-jre
WORKDIR /app

ENV TZ=America/Sao_Paulo \
    SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

RUN useradd -ms /bin/bash appuser

# copia o jar gerado no stage anterior
COPY --from=build /workspace/target/*.jar /app/app.jar

RUN mkdir -p /app/data /app/uploads && chown -R appuser:appuser /app

EXPOSE 8080
USER appuser

# importante: usar a porta do Render
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]
