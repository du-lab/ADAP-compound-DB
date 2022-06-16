import pymysql
import sys
import csv
import pandas as pd

from sqlalchemy import create_engine, types
from sqlalchemy_utils import database_exists, create_database
from os import listdir
from os.path import isfile, join

import time

import argparse

def read_file_table(cur, db):
	print('Reading File.csv')
	csv_data = csv.reader(open('./dumpfiles/File.csv'))

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


def import_csv(username, password, host, database, store_location, schema_location):
	mypath = store_location

	start_time = time.time()
	onlyfiles = [f.split('.')[0] for f in listdir(mypath) if isfile(join(mypath, f))]
	onlyfiles.pop(1)

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

	sqlCommands = create_schema(schema_location)

	for command in sqlCommands :
		try:
			cur.execute(command)
			db.commit()
		except:
			print('Command skipped: ', command)

	for f in onlyfiles:

		if f == 'File':
			continue

		print(f)
		
		read_start_time = time.time()

		df = pd.read_csv(mypath + f + '.csv',sep=',',  na_values = '0', low_memory = False)
		
		try:
			df.to_sql(name = f, con=engine,index=False,if_exists='replace', chunksize = 1000000 , method='multi') #try changing chunksize to see change in performance
		except:
			print('Could not write ', f)
		print('Read finish time: %s' % (time.time() - read_start_time))
		
	read_file_table(cur,db)	

	


	cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 1;")
	cur.close()
	print("--- %s seconds ---" % (time.time() - start_time))

if __name__ == '__main__' :
	parser = argparse.ArgumentParser()
	parser.add_argument('--save', help='Save location')
	parser.add_argument('--user', help='Username')
	parser.add_argument('--password', help='Password')
	parser.add_argument('--host', help='Host')
	parser.add_argument('--db', help='Database')
	parser.add_argument('--schema', help='Schema location')
	args = parser.parse_args()
	import_csv(args.user, args.password, args.host, args.db, args.save, args.schema)