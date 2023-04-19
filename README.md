# ADAP Compound Database

### Setup Dev/Local

### Requirements
- Java 9
- Maven
- MySQL 8.0

1. Clone GitLab repository of the project and run `mvn clean install`
```
https://cci-scm.uncc.edu/du-lab/adap-kdb.git
```

2. Install MySQL. Choose username and password for connecting to MySQL.
3. To populate the database, download a backup file from Google Drive and run (Import only table structure if data is not required)
```
python3 scripts/import_mysql.py --save GOOGLEDRIVEBACKUPFOLDER --user USERNAME --password PASSWORD
```
If above command throws error, consider turning off foriegn key checks using in sql console and turn it on after table creation
```
SET FOREIGN_KEY_CHECKS=0;
```
4. Create a file named application-local.properties under src -> main -> resources with the contents below and edit database spring.datasource.username and spring.datasource.password with MySql username and password
```
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/adapcompounddb?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true&blobSendChunkSize=805306368
spring.datasource.username=*username_here*
spring.datasource.password=*password_here*
```
5. Donot push the application-local.properties to git.
6. Open the code using IntelliJ and add a new configuration - type springboot to run the application. In the configuration select the main class as `org.dulab.adapcompounddb.Application` and active profiles as `local`. Alternatively you can find the Application.java class and run the class to create the configuration automatically, and edit the configuration to add active profiles as `local`.
7. Edit run configuration and add DISABLE_CAPTCHA=1 to disable google captcha checks

Note: Do not edit the application.properties file and for local development

### Setup Server

### Requirements
- Java 9
- Maven
- MySQL 8.0
- Tomcat 8.5
1. Clone GitLab repository of the project
```
https://cci-scm.uncc.edu/du-lab/adap-kdb.git
```
2. Install Apache Tomcat 8.5 server. Download the zip archive
   ([link](https://mirrors.ocf.berkeley.edu/apache/tomcat/tomcat-8/v8.5.61/bin/apache-tomcat-8.5.61.zip))
   , unzip it, and copy it to your project folder or somewhere else.
   Then add the content of the file
   **[repository]/server/context.xml** to the file **[tomcat]/conf/context.xml**

3. Install MySQL. Choose username and password for connecting to MySQL. Update fields `username` and
   `password` in file **[tomcat]/conf/context.xml**. Then, download MySQL JDBC driver and put it into
   **[tomcat]/lib** directory. To populate the database, download a backup file from Google Drive and run
```
mysql -u root -p < dump.mysql
```

4. (Optional) Install MySQL Workbench and check if you can connect to MySQL. Then, run the script **[repository]/databse/create.sql** in MySQL Workbench.

5. Install Maven and compile and deploy the project by running
```
mvn tomcat7:deploy
```
Alternatively, you can open the repository in IntelliJ IDEA and set it up to deploy the project.

6. To make the CAPTCHA work properly, set up the environment variable `CAPTCHA_SECRET_KEY`

### Setup for Amazon Web Services EC2 machine
See [link](INSTALL_LINUX.md)

### Setup email and password for gmail server
1. Create an app password for your email. See [link](https://support.google.com/accounts/answer/185833?hl=en#zippy=%2Cwhy-you-may-need-an-app-password)
2. Set email and password as your environmental variables. Use ADAP_EMAIL_LOGIN and ADAP_PASSWORD

### Package structure
* `org.dulab.adapcompounddb.config` configure the web application
* `org.dulab.adapcompounddb.models.entities` represent entities of tables in the MySQL database
* `org.dulab.adapcompounddb.site.controllers` handle user interaction (HTTP requests and responses)
* `org.dulab.adapcompounddb.site.services` perform the application logic for processing data
* `org.dulab.adapcompounddb.site.repositories` perform interaction with the MySQL database
* `org.dulab.adapcompounddb.validation` contains classes for data validation

