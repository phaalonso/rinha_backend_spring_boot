FROM eclipse-temurin:19-jdk-alpine
VOLUME /tmp
COPY run.sh .
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "run.sh"]
