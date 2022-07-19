import os
import argparse
from datetime import datetime

def export(user, password, database, folder):
	files = [f for f in os.listdir(folder)]
	for f in files:
		os.system(f'sudo rm {os.path.join(folder,f)}')
	os.system(f'sudo mysqldump -u {user} -p{password} --tab {folder} {database}')
	file_name = 'backup_' + datetime.today().strftime('%Y-%m-%d_%H-%M-%S')
	os.system(f'sudo zip -r {file_name}.zip {folder}')

if __name__ == '__main__' :
	parser = argparse.ArgumentParser()
	parser.add_argument('--save', help='Save location', required = True)
	parser.add_argument('--user', help='Username', required = True)
	parser.add_argument('--password', help='Password', required = True)
	#parser.add_argument('--host', help='Host')
	#parser.add_argument('--db', help='Database')
	#parser.add_argument('--schema', help='Schema location')
	args = parser.parse_args()
	export(args.user, args.password, 'adapcompounddb', args.save)