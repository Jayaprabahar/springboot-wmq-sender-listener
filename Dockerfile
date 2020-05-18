FROM openjdk:11-alpine

ENV APP_NAME="springboot-wmq-sender-listener" \
    JAVA_OPTS="-Xms256m -Xmx512m -Djava.net.preferIPv4Stack=true"

COPY ./target/*.jar ./app.jar

COPY entrypoint.sh ./

EXPOSE 8080

ENTRYPOINT ["bash", "/home/java/entrypoint.sh"]