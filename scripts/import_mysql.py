import os
from os import listdir
from os.path import isfile, join
import argparse

import pymysql
from sqlalchemy_utils import database_exists, create_database

def import_into_sql(username, password, host, database, store_location, with_data):

	url = f'mysql://{username}:{password}@{host}/{database}'

	if not database_exists(url):
		create_database(url)

	db_opts = {
	'user': username,
	'password': password,
	'host': host,
	'database': database,

	}


	db = pymysql.connect(**db_opts)
	cur = db.cursor()

	cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 0;")

	onlysql = [f.split('.')[0] for f in listdir(store_location) if isfile(join(store_location, f)) and f.split('.')[1] == 'sql']
	for f in onlysql:
		file_name = f + ".sql"
		query = f'mysql -u {username} -p{password} {database} < {join(store_location,file_name)}'
		print('Executing ', query)
		os.system(query)

    if with_data:
        onlyfiles = [f.split('.')[0] for f in listdir(store_location) if isfile(join(store_location, f)) and f.split('.')[1] == 'txt']
        for f in onlyfiles:
            file_name = f + ".txt"
            query = f'mysqlimport -u {username} -p{password} -d {database} --local {join(store_location,file_name)}'
            print('Executing ', query)
            os.system(query)

	cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 1;")
	cur.close()

if __name__ == '__main__' :
	parser = argparse.ArgumentParser()
	parser.add_argument('--save', help='Save location', required = True)
	parser.add_argument('--user', help='Username' ,required = True)
	parser.add_argument('--password', help='Password' ,required = True)
	parser.add_argument('--with-data', help='If not specified, only the DB structure will be imported', action='store_true')
	args = parser.parse_args()
	import_into_sql(args.user, args.password, 'localhost', 'adapcompounddb', args.save, parser.with_data)