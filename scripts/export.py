import pymysql
import sys
import csv

import os

db_opts = {
	'user': 'root',
	'password': password,
	'host':'localhost',
	'database': 'adapcompounddb'
}

mypath = 'dumpfiles'

if not os.path.isdir(mypath):
	os.makedirs(mypath)

db = pymysql.connect(**db_opts)



cur = db.cursor()
cur.execute("USE adapcompounddb")
cur.execute("SHOW TABLES")
tables = cur.fetchall()
for(table_name, ) in tables:
	sql = 'Select * from adapcompounddb.{};'.format(table_name)
	print("Writing " + table_name)
	cur.execute(sql)
	rows = cur.fetchall()

	column_names = [i[0] for i in cur.description]
	fp = open(mypath+ '/' + table_name+'.csv', 'w')
	myFile = csv.writer(fp)
	myFile.writerow(column_names)
	myFile.writerows(rows)
	fp.close()