FROM eclipse-temurin:17-jre-alpine
#ADD target/adap-compound-db.war /usr/local/tomcat/webapps/
#ADD server/context.xml /usr/local/tomcat/conf/context.xml
EXPOSE 8080
#CMD ["catalina.sh", "run"]


ADD target/adap-compound-db.jar adap-compound-db.jar
ENTRYPOINT ["java","-jar","/adap-compound-db.jar"]




