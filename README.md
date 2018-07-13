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
2. Install Apache Tomcat server (tested with version 8.5) and add the content of the file **[repository]/database/datasource.xml** to the file **[tomcat]/conf/context.xml**

3. Install MySQL, MySQL JDBC driver, and (optionally) MySQL Workbench (tested with version 5.0.11). Then, run the script **[repository]/databse/create.sql** in MySQL Workbench.

4. Install IntelliJ IDEA (or other IDE of your choice) and configure it to deploy our project on Tomcat 

### Setup for Amazon Web Services EC2 machine
See [link](INSTALL_LINUX.md)

### Package structure
* `org.dulab.adapcompounddb.config` configure the web application
* `org.dulab.adapcompounddb.models.entities` represent entities of tables in the MySQL database
* `org.dulab.adapcompounddb.site.controllers` handle user interaction (HTTP requests and responses)
* `org.dulab.adapcompounddb.site.services` perform the application logic for processing data
* `org.dulab.adapcompounddb.site.repositories` perform interaction with the MySQL database
* `org.dulab.adapcompounddb.validation` contains classes for data validation

