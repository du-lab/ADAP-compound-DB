#FROM tomcat:8.5	
#ADD target/adap-compound-db.war /usr/local/tomcat/webapps/
#ADD server/context.xml /usr/local/tomcat/conf/context.xml

FROM ubuntu:18.04
FROM openjdk:19	
ADD target/adap-compound-db.jar adap-compound-db.jar
EXPOSE 8080
#CMD ["catalina.sh", "run"]
ENTRYPOINT ["java","-jar","/adap-compound-db.jar"]




