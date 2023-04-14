FROM tomcat:8.5		
ADD target/adap-compound-db.war /usr/local/tomcat/webapps/
ADD server/context.xml /usr/local/tomcat/conf/context.xml
EXPOSE 8080
CMD ["catalina.sh", "run"]
