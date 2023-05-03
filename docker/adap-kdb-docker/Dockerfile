FROM openjdk:21-slim-buster
#required by rdkit
#RUN apk update && apk add --no-cache libstdc++6 && apk add --no-cache freetype
#ENV LD_LIBRARY_PATH=/usr/lib/gcc/x86_64-alpine-linux-musl/6.4.0/
ADD ../../target/adap-compound-db.war adap-compound-db.war
ENTRYPOINT ["java","-jar","/adap-compound-db.war", "--spring.profiles.active=docker"]
EXPOSE 8080





