import pymysql
import MySQLdb
import sys
import csv
import pandas as pd
from sqlalchemy import create_engine, types
from sqlalchemy_utils import database_exists, create_database
from os import listdir
from os.path import isfile, join
import os
import time
import traceback
import argparse

def read_file_table(cur, db, location):
	print('Reading File.csv')
	csv_data = csv.reader(open(f'{location}/File.csv'))

	csv.field_size_limit(sys.maxsize)
	next(csv_data)

	print('Writing File.csv')
	for row in csv_data:
		cur.execute('INSERT INTO `File` (`Id`, `Name`, `FileType`, `Content`, `SubmissionId`) VALUES(%s, %s, %s, _binary %s, %s)', row)
		db.commit()

def create_schema(location):
	fd = open(location, 'r')
	sqlFile = fd.read()
	fd.close()
	sqlCommands = sqlFile.split(';')

	return sqlCommands


def import_csv(username, password, host, database, store_location, schema_name):
	mypath = store_location

	start_time = time.time()
	onlyfiles = [f.split('.')[0] for f in listdir(mypath) if isfile(join(mypath, f)) and f.split('.')[1] == 'csv']
	

	url = f'mysql://{username}:{password}@{host}/{database}'

	if not database_exists(url):
		create_database(url)

	engine = create_engine(url) # enter your password and database names here


	db_opts = {
	'user': username,
	'password': password,
	'host': host,
	'database': database,

	}

	print(onlyfiles)


	db = pymysql.connect(**db_opts)
	cur = db.cursor()

	cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 0;")
	db.commit()
	cur.execute('SET GLOBAL local_infile=1;')
	db.commit()
	cur.execute(f"use {database};")
	schema_location = join(mypath, schema_name)
	sqlCommands = create_schema(schema_location)

	for command in sqlCommands :
		try:
			cur.execute(command)
			db.commit()
		except Exception as e:
			print('Command skipped: ', e, file=sys.stderr)
			exit()

	for f in onlyfiles:
		# breakpoint()

		if f == 'File':
			continue

		print(f)
		
		read_start_time = time.time()

		df = pd.read_csv(os.path.join(mypath, f'{f}.csv'), sep=',',  low_memory = False)
		
		try:
			df.to_sql(name = f, con=engine,index=False,if_exists='append', chunksize = 1000000 , method='multi') #try changing chunksize to see change in performance
		except Exception as e:

			print('Could not write ', f, file=sys.stderr)
			print(str(e))
# 			traceback.print_exc()
			exit()
		print('Read finish time: %s' % (time.time() - read_start_time))
		
	#read_file_table(cur,db,store_location)	

	


	cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 1;")
	cur.close()
	print("--- %s seconds ---" % (time.time() - start_time))

if __name__ == '__main__' :
	parser = argparse.ArgumentParser()
	parser.add_argument('--save', help='Save location', required = True)
	parser.add_argument('--user', help='Username' ,required = True)
	parser.add_argument('--password', help='Password' ,required = True)
	parser.add_argument('--host', help='Host', required = True)
	parser.add_argument('--db', help='Database', required = True)
	parser.add_argument('--schema', help='Create table script file name', required = True)
	args = parser.parse_args()
	import_csv(args.user, args.password, args.host, args.db, args.save, args.schema)