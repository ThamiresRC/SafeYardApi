# syntax=docker/dockerfile:1

########################################
# 1) BUILD STAGE (Maven + JDK 17)
########################################
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copia arquivos mínimos para baixar dependências
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copia o código e empacota
COPY src src
RUN ./mvnw -q -DskipTests package

########################################
# 2) RUNTIME STAGE (JRE 17 enxuto)
########################################
FROM eclipse-temurin:17-jre
WORKDIR /app

# Variáveis úteis (ajuste JAVA_OPTS se quiser limitar memória no Azure)
ENV TZ=America/Sao_Paulo \
    SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS=""

# Usuário não-root
RUN useradd -ms /bin/bash appuser

# Copia o jar gerado
COPY --from=build /workspace/target/*.jar /app/app.jar

# Pastas para H2 (dev) e uploads
RUN mkdir -p /app/data /app/uploads && chown -R appuser:appuser /app

EXPOSE 8080
USER appuser

# Permite passar JAVA_OPTS/SPRING_PROFILES_ACTIVE por -e no docker run
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
