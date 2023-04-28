FROM eclipse-temurin:17-jre-alpine
ADD ../../target/adap-compound-db.war adap-compound-db.war
ENTRYPOINT ["java","-jar","/adap-compound-db.war", "--spring.profiles.active=docker"]
EXPOSE 8080





