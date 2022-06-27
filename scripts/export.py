import pymysql
import sys
import csv

import os

import argparse
import pandas.io.sql as psql
import pandas as pd

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

	path = os.path.join(folder,'schema_main.sql')
	os.system(f'mysqldump -h {host} -u {user} -p{password} --no-data {database} > {path}')
	db = pymysql.connect(**db_opts)



	cur = db.cursor()
	cur.execute(f"USE {database}")
	cur.execute("SHOW TABLES")
	tables = cur.fetchall()
	for(table_name, ) in tables:
		sql = f'Select * from {database}.{table_name};'
		print("Writing " + table_name)
		
		cur.execute(sql)
		rows = cur.fetchall()
		
		decoded_rows = []
		column_names = [i[0] for i in cur.description]
		fp = open(os.path.join(mypath,table_name+'.csv'), 'w')
		myFile = csv.writer(fp, quoting = csv.QUOTE_MINIMAL)
		for row in rows:
			tmp = []
			for cell in row:
				if cell == '' or cell == 'N/A':
					cell = ' '
				try:
					tmp.append(cell.decode('utf-8'))
					
				except:
					tmp.append(cell)
					
			decoded_rows.append(tuple(tmp))	
	
	
		myFile.writerow(column_names)
	
	
		myFile.writerows(decoded_rows)

	
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