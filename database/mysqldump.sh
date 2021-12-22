#!/usr/bin/env bash
mysqldump --user=dulab --host=18.221.34.82 --protocol=tcp --port=3306 --skip-extended-insert --default-character-set=utf8 --column-statistics=0 --set-gtid-purged=OFF -p --add-drop-database --databases "adapcompounddb" > dump.mysql


# To upload the backup, run
# python3 export_to_mysql.py --user MYSQL_USERNAME --password MYSQL_PASSWORD --dump-file DUMP_FILE
# You may need to temporally increase the maximum allowed packet size of MySQL with
# SET GLOBAL max_allowed_packet=1073741824
