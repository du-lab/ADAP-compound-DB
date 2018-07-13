# How to install ADAP-Compound-DB on Amazon EC2 machine with Ubuntu

In the following, it is assumed that you have created and launched your Amazon EC2 instance with Ubuntu, and you have connected to it using SSH. For a tutorial on how to do all that, see [this](https://medium.com/@GalarnykMichael/aws-ec2-part-1-creating-ec2-instance-9d7f8368f78a) and [this](https://medium.com/@GalarnykMichael/aws-ec2-part-2-ssh-into-ec2-instance-c7879d47b6b2).

## Step 1. Install Java Development Kit
Here we install OpenJDK, an open-source alternative to Oracle JDK, because it's easier and open source.
```
$ sudo apt-get update
$ sudo apt-get install default-jdk
```

<!-- ## Step 2. Install MySQL database

By default, Ubuntu 16.04 install MySQL version 5.7. However, our web application requires MySQL version 8.0 to work properly. In order to install MySQL 8.0, first download and execute the repository package
```shell
$ cd /tmp
$ wget -c https://dev.mysql.com/get/mysql-apt-config_0.8.10-1_all.deb
$ sudo dpkg -i mysql-apt-config_0.8.10-1_all.deb
```
You will be prompted to choose MySQL version and other components. Then, run the following command:
```shell
$ sudo apt-get install mysql-server
```
When asked to enter a password for the root user, write it down somewhere. You will use it when establishing connection to the database server.

__Remark 1__: If you need to start, stop, restart, or check status of MySQL server, you can use the following commands:
```shell
$ systemctl start mysql
$ systemctl stop mysql
$ systemctl restart mysql
$ systemctl status mysql
``` -->

## Step 2. Install Apache Tomcat Server

### Create a new user
For security purposes. Tomcat should be run as unprivileged user. We will create a new user and group that will run the Tomcat service
```shell
$ sudo addgroup tomcat
$ sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
```

### Install Tomcat
The best way to install Tomcat 8 is to download the latest binary release then configure it manually.
```shell
$ cd /tmp
$ curl -O http://mirror.reverse.net/pub/apache/tomcat/tomcat-8/v8.5.32/bin/apache-tomcat-8.5.32.tar.gz
$ sudo mkdir /opt/tomcat
$ sudo tar xzvf apache-tomcat-8.5.32.tar.gz -C /opt/tomcat --strip-components=1
```

### Update permissions
The `tomcat` user that we set up needs to have access to the Tomcat installation.
```shell
$ cd /opt/tomcat
$ sudo chgrp -R tomcat /opt/tomcat
$ sudo chmod -R g+r conf
$ sudo chmod g+x conf
$ sudo chown -R tomcat webapps/ work/ temp/ logs/
```

### Create a `systemd` service file
We want to be able to run Tomcat as a service, so we will set up `systemd` service file. Create a new file called `tomcat.service` in the `/etc/systemd/system` directory by typing
```shell
$ sudo nano /etc/systemd/system/tomcat.service
```
and paste the following contents into the file.
```
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

Environment=JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/jre
Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/tomcat
Environment=CATALINA_BASE=/opt/tomcat
Environment='CATALINA_OPTS=-Xms128M -Xmx512M -server -XX:+UseParallelGC'
Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

User=tomcat
Group=tomcat
UMask=0007
RestartSec=10
Restart=always

[Install]
WantedBy=multi-user.target
```
Next, reload the `systemd` daemon and start the `tomcat` service.
```shell
$ sudo systemctl daemon-reload
$ sudo systemctl start tomcat
$ sudo systemctl status tomcat
```

### Test Tomcat server
Now that the Tomcat service is started, we can test to make sure the default page is available. First, log into your Amazon Web Services account, go to _EC2 Management Console_, and find __Public DNS (IPv4)__ (i.e. `ec2-18-222-205-143.us-east-2.compute.amazonaws.com`) or __IPv4 Public IP__ (i.e. `18.222.205.143`) of the instance you are running. In your browser, type in one of the following lines
```
http://ec2-18-222-205-143.us-east-2.compute.amazonaws.com:8080
http://18.222.205.143:8080
```
You will see the default Tomcat page with title "__It works!__".

If you don't see that page, make sure that the instance accepts inbound requests on port 8080. In order to do that, open page _Security Groups_ in _EC2 Management Console_, select that group used by your instance, and add the following rules on the __Inbound__ tab
```
Custom TCP Rule    TCP    8080    0.0.0.0/0
Custom TCP Rule    TCP    8080    ::/0
```

If you were able to successfully access Tomcat, now is a good time to enable the service file so that Tomcat automatically starts at boot
```shell
$ sudo systemctl enable tomcat
```

## Step 3. Configure Tomcat to listen to port 80
When testing Tomcat, we had to add the port number 8080 at the end of the public DNS/IP to reach Tomcat server. That's because Tomcat is listening to the port 8080. However, the default port number used by all browsers is 80. Here, we will configure Tomcat to listen to port 80.

First, edit the file `/opt/tomcat/conf/server.xml` and replace line
```xml
<Connector port="8080" protocol="HTTP/1.1"
```
with the line
```xml
<Connector port="80" protocol="HTTP/1.1"
```

Then, install and configure `authbind` to use port 80.
```shell
$ sudo apt-get install authbind
$ sudo touch /etc/authbind/byport/80
$ sudo chmod 500 /etc/authbind/byport/80
$ sudo chown tomcat /etc/authbind/byport/80
```

Finally, configure Tomcat to use `authbind` when it starts up. To do that, we will edit the file `/opt/tomcat/bin/startup.sh`. At the bottom of the file we need to change this
```shell
exec "$PRGDIR"/"@EXECUTABLE" start "$@"
```
to this
```shell
exec authbind --deep "$PRGDIR"/"@EXECUTABLE" start "$@"
```
and restart Tomcat
```shell
$ sudo systemctl restart tomcat
```

## Step 4. Add Tomcat Users and a Data Source
We will create two Tomcat user that have access to `manager-script` and `manager-gui`. The user `admin` will be used by Maven to deploy our web application. The user `tomcat` can be used by you to manually deploy/undeploy web applications. Note that the Manager is only accessible from a browser running on the same machine as Tomcat, so we don't need to worry about secure passwords.

Add the following lines to the file `/etc/tomcat-users.xml`
```xml
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<user username="admin" password="admin" roles="manager-script"/>
<user username="tomcat" password="tomcat" roles="manager-gui"/>
```

Restart the Tomcat service to apply the changes.
```shell
$ sudo systemctl restart tomcat
```

If you want to manually manage deployed applications, you can install a console web browser and go to the corresponding web page
```shell
$ sudo apt-get install elinks
$ elinks http://localhost:8080/manager
```

Next, we will create a data source that can access our Amazon RDS database. Open the Tomcat configuration file `/opt/tomcat/conf/context.xml` and add the following lines
```xml
<Resource name="jdbc/AdapCompoundDbDataSource"
        type="javax.sql.DataSource"
        maxTotal="20" maxIdle="5" maxWaitMillis="10000"
        username="[USERNAME]" password="[PASSWORD]"
        driverClassName="com.mysql.cj.jdbc.Driver"
        url="jdbc:mysql://[RDS_URL]:3306/adapcompounddb?autoReconnect=true&amp;useSSL=false"/>
```
Substitute `USERNAME` and `RDS_URL` with the correct values that you can find on _RDS - AWS Console_ in the __Details__ pane of your RDS-instance (field Username and Endpoint respectively). Substitute `PASSWORD` with the master password you set up when you created the RDS instance.

Finally, we will download MySQL JDBC Driver and add it to `/usr/share/tomcat8/lib/`:
```shell
$ cd /tmp
$ wget -i https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.11.tar.gz
$ tar xzvf mysql-connector-java-8.0.11.tar.gz
$ sudo cp mysql-connector-java-8.0.11/mysql-connector-java-8.0.11.jar /opt/tomcat/lib/
$ sudo chown root:tomcat /opt/tomcat/lib/mysql-connector-java-8.0.11.jar
```

Restart the Tomcat service to apply the changes.
```shell
$ sudo systemctl restart tomcat
```

## Step 5. Download and Deploy ADAP Spectral Library

First, download the source code of ADAP Spectral Library
```shell
$ git clone https://github.com/du-lab/ADAP-compound-DB.git
```

Then, deploy ADAP Spectral Library by running the command
```shell
$ cd ~/ADAP-compound-DB
$ mvn tomcat7:deploy
```

## References
- [How To Install Apache Tomcat 8 on Ubuntu 16.04](https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04)
- [How to Deploy a WAR File to Tomcat](http://www.baeldung.com/tomcat-deploy-war)
