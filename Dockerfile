FROM openjdk:8-jdk-alpine

WORKDIR /home
COPY target/dist/*.jar app.jar
COPY target/dist/*.properties .
COPY start.sh start.sh

EXPOSE 80

ENTRYPOINT ["start.sh"]