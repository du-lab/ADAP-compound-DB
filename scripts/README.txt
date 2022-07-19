Steps to run mysqldump to backup data

1) Create a folder in /tmp/
2) In /etc/my.cnf under [mysqld] section add the line - secure_file_priv = "/private/tmp/<folder_name>/"
3) Restart Mysql Server
4) Make mysql the owner of the folder by using the command `sudo chown -R _mysql /tmp/<folder>`
5) In Terminal run: sudo mysqldump -u <user> -p<password> --tab  /private/tmp/<folder> <database> 