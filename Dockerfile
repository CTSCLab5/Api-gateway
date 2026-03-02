FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]