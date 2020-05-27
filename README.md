# ADAP Compound Database

### Requirements
- Java 8 EE
- Maven
- Tomcat 8.5
- MySQL 8.0

### Setup
1. Clone GitHub repository of the project
```
git clone https://github.com/du-lab/ADAP-compound-DB.git
```
2. Install Apache Tomcat server (tested with version 8.5) and add the content of the file 
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

### Setup for Amazon Web Services EC2 machine
See [link](INSTALL_LINUX.md)

### Package structure
* `org.dulab.adapcompounddb.config` configure the web application
* `org.dulab.adapcompounddb.models.entities` represent entities of tables in the MySQL database
* `org.dulab.adapcompounddb.site.controllers` handle user interaction (HTTP requests and responses)
* `org.dulab.adapcompounddb.site.services` perform the application logic for processing data
* `org.dulab.adapcompounddb.site.repositories` perform interaction with the MySQL database
* `org.dulab.adapcompounddb.validation` contains classes for data validation

