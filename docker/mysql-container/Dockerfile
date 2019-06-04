# Use an official MySQL runtime as a parent image
FROM ubuntu:18.04

# The maintainer name and email
MAINTAINER Aleksandr Smirnov <asmirno1@uncc.edu>

ENV DEBIAN_FRONTEND noninteractive

# Update ubuntu packages
RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get install -y apt-utils

# Install Vim
RUN apt-get install -y vim

# # Install Wget
# RUN apt-get install -y curl

# Install MySQL 8.0
RUN apt-get install -y mysql-server-5.7
# RUN mysql_secure_installation --use-default
# RUN apt-get install -y lsb-release
# RUN curl -OL https://dev.mysql.com/get/mysql-apt-config_0.8.10-1_all.deb
# RUN dpkg -i mysql-apt-config_0.8.10-1_all.deb
# RUN apt-get install -y 

# Copy the database schema to the /data directory
ADD files/entrypoint.sh files/init_db.sh files/adapcompounddb_schema.sql /tmp/

# init_db.sh will create the default database from adapcompounddb_schema.sql, 
# then stop mysqld, and finally copy the /var/lib/mysql directory to default_mysql_db.tar.gz
# RUN sh /tmp/init_db.sh

CMD ["/tmp/entrypoint.sh"]
