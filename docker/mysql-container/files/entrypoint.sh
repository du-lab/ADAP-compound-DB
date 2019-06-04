#!/bin/bash

# Initialize MySQL database
# Add this file to the container via Dockerfile.
# Specify a VOLUMNE ["/var/lib/mysql"] in Dockerfile or `-v /var/lib/mysql` in the `docker run` command.
# Once built, do `docker run image_name /path/to/docker-mysql-initialize.sh`

mkdir -p /var/run/mysqld
chown mysql:mysql /var/run/mysqld

mysqld_safe --skip-grant-tables &



# set -e
# set -x
# 
# # Start the MySQL daemon in the background.
# # /bin/mysqld --initialize & mysql_pid=$!
# /bin/mysqld --initialize
# 
# until mysqladmin ping > /dev/null 2>&1; do
# 	echo -n "."; sleep 0.2
# done
