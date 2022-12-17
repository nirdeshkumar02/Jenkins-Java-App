FROM openjdk:8-jre-alpine

EXPOSE 8080

COPY ./target/jenkins-java-app-*.jar /usr/app/
WORKDIR /usr/app

CMD java -jar jenkins-java-app-*.jar
