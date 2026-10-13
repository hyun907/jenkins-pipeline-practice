FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY target/jenkins-java-practice-1.0.0.jar /app/app.jar

USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
