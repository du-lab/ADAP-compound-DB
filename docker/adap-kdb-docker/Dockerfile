#FROM eclipse-temurin:17-jre-alpine
#required by rdkit
#RUN apk update && apk add --no-cache libstdc++6
FROM openjdk:17-jdk-alpine
ADD ../../target/adap-compound-db.war adap-compound-db.war
ENTRYPOINT ["java","-jar","/adap-compound-db.war", "--spring.profiles.active=docker"]
EXPOSE 8080





