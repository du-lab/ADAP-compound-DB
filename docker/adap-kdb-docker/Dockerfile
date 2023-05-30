#FROM adoptopenjdk/openjdk11:jre-11.0.19_7-ubuntu
FROM tomcat:8.5.89-jre17
#For s3
ENV ACCESS_ID="AKIA57CZCFX53PQDCA6K"
ENV ACCESS_KEY="Z8YWRt6b4iGe73Lx90NgMvHaDhfZm+ABpNICNazK"

#For captcha
ENV CAPTCHA_SECRET_KEY="6LdY3V8hAAAAABWB0g3AcKLTJAVz-i_8FJQ4EUhZ"

#For email
ENV ADAP_EMAIL_LOGIN="adap.helpdesk@gmail.com"
ENV ADAP_EMAIL_PASSWORD="djlqocwutonnykwi"

ADD ../../target/adap-compound-db.war /usr/local/tomcat/webapps/
ADD server/context.xml /usr/local/tomcat/conf/context.xml

#ENTRYPOINT ["java","-jar","/adap-compound-db.jar", "--spring.profiles.active=docker"]
CMD ["catalina.sh", "run"]
EXPOSE 8080





