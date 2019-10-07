#!/usr/bin/env bash
mysqldump --user=dulab --host=adapcompounddbrds.cti4tencv7f9.us-east-2.rds.amazonaws.com --protocol=tcp --port=3306 --default-character-set=utf8 --set-gtid-purged=OFF -p --add-drop-database --databases "adapcompounddb" > dump.mysql


# To upload the backup, run
# mysql -u root -p < dump.mysql