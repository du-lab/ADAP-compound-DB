# FROM eclipse-temurin:17-jre-alpine
#RUN wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.73/src/apache-tomcat-9.0.73-src.tar.gz && \
#tar xzf apache-tomcat-9.0.73-src.tar.gz && \
#mv apache-tomcat-9.0.73-src tomcat && \
#rm -rf apache-tomcat-9.0.73-src.tar.gz

# ADD target/adap-compound-db.war /tomcat/webapps/
# ADD server/context.xml /tomcat/conf/context.xml
# EXPOSE 8080
# CMD ["/tomcat/bin/catalina.sh", "run"]


#ADD target/adap-compound-db.jar adap-compound-db.jar
#ENTRYPOINT ["java","-jar","/adap-compound-db.jar"]


FROM tomcat:8.5.88-jre17-temurin
ADD target/adap-compound-db.war /usr/local/tomcat/webapps/adap-compound-db.war
ADD server/context.xml /usr/local/tomcat/conf/context.xml
EXPOSE 8080
CMD ["catalina.sh", "run"]




