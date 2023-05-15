FROM adoptopenjdk/openjdk11:jre-11.0.19_7-ubuntu

#For s3
ENV ACCESS_ID="AKIA57CZCFX53PQDCA6K"
ENV ACCESS_KEY="Z8YWRt6b4iGe73Lx90NgMvHaDhfZm+ABpNICNazK"

#For captcha
ENV CAPTCHA_SECRET_KEY="6LdY3V8hAAAAABWB0g3AcKLTJAVz-i_8FJQ4EUhZ"

#For email
ENV ADAP_EMAIL_LOGIN="adap.helpdesk@gmail.com"
ENV ADAP_EMAIL_PASSWORD="djlqocwutonnykwi"

ADD ../../target/adap-compound-db.war adap-compound-db.war
ENTRYPOINT ["java","-jar","/adap-compound-db.war", "--spring.profiles.active=docker"]
EXPOSE 8080





