FROM eclipse-temurin:17-jre-alpine
#required by rdkit
RUN apt-get update && apt-get install -y libstdc++6
ADD ../../target/adap-compound-db.war adap-compound-db.war
ENTRYPOINT ["java","-jar","/adap-compound-db.war", "--spring.profiles.active=docker"]
EXPOSE 8080





