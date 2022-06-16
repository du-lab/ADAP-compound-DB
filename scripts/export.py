import pymysql
import sys
import csv

import os

import argparse

def export(user, password, host, database, folder):

	db_opts = {
	'user': user,
	'password': password,
	'host': host,
	'database': database
	}

	mypath = folder

	if not os.path.isdir(mypath):
		os.makedirs(mypath)


	os.system(f'mysqldump -h {host} -u {user} -p{password} --no-data {database} > {os.path.join(folder,'schema_main.sql')}')
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
		fp = open(os.path.join(mypath,table_name+'.csv'), 'w')
		myFile = csv.writer(fp)
		myFile.writerow(column_names)
		myFile.writerows(rows)
		fp.close()

if __name__ == '__main__' :
	parser = argparse.ArgumentParser()
	parser.add_argument('--save', help='Save location')
	parser.add_argument('--user', help='Username')
	parser.add_argument('--password', help='Password')
	parser.add_argument('--host', help='Host')
	parser.add_argument('--db', help='Database')
	#parser.add_argument('--schema', help='Schema location')
	args = parser.parse_args()
	export(args.user, args.password, args.host, args.db, args.save)