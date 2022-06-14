import pymysql
import sys
import csv
import pandas as pd

from sqlalchemy import create_engine, types
from sqlalchemy_utils import database_exists, create_database
from os import listdir
from os.path import isfile, join

import time
import re

import mysql.connector
ws = re.compile("\s+")

mypath = './dumpfiles/'

start_time = time.time()
onlyfiles = [f.split('.')[0] for f in listdir(mypath) if isfile(join(mypath, f))]
onlyfiles.pop(1)

url = 'mysql://root:varunsuresh@localhost/adapcompounddb'

if not database_exists(url):
	create_database(url)

engine = create_engine(url) # enter your password and database names here


db_opts = {
'user': 'root',
'password': password,
'host':'localhost',
'database': 'adapcompounddb',

}

print(onlyfiles)


db = pymysql.connect(**db_opts)
cur = db.cursor()

cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 0;")
db.commit()
cur.execute('SET GLOBAL local_infile=1;')
db.commit()

 
queries = sql_file.split(';')

for f in onlyfiles:

	if f == 'File':
		continue

	print(f)
	
	read_start_time = time.time()

	df = pd.read_csv(mypath + f + '.csv',sep=',',  na_values = '0', low_memory = False)
	
	
	df.to_sql(name = f, con=engine,index=False,if_exists='replace', chunksize = 100000 , method='multi') #try changing chunksize to see change in performance
	print('Read finish time: %s' % (time.time() - read_start_time))
		

print('Reading File.csv')
csv_data = csv.reader(open('./dumpfiles/File.csv'))

csv.field_size_limit(sys.maxsize)
next(csv_data)

print('Writing File.csv')
for row in csv_data:
	cur.execute('INSERT INTO `File` (`Id`, `Name`, `FileType`, `Content`, `SubmissionId`) VALUES(%s, %s, %s, _binary %s, %s)', row)
	db.commit()


cur.execute("SET GLOBAL FOREIGN_KEY_CHECKS = 1;")
cur.close()
print("--- %s seconds ---" % (time.time() - start_time))