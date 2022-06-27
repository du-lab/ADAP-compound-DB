import pymysql
import sys
import csv

import os

import argparse
import pandas.io.sql as psql
import pandas as pd
from sqlalchemy import create_engine
import numpy as np

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
	url = f'mysql://{user}:{password}@{host}/{database}'
	cnx = create_engine(url).connect()


	cur = db.cursor()
	cur.execute(f"USE {database}")
	cur.execute("SHOW TABLES")
	tables = cur.fetchall()
	for(table_name, ) in tables:
		#sql = f'Select * from {database}.{table_name};'
		print("Writing " + table_name)
		
		df = pd.read_sql_table(table_name, cnx)
		if table_name == 'userprincipal':
			print(df)
		for col, dtype in df.dtypes.items():
			if dtype == np.object:
				df[col] = df[col].str.decode('utf-8').fillna(df[col]) 
			
		df.to_csv(os.path.join(folder,f"{table_name}.csv"), index = False, encoding='utf-8')

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