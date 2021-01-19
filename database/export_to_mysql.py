"""Export a dump file to MySQL"""

import argparse
import re
import subprocess
import tempfile

from os.path import getsize
from typing import List, Optional


INSERT_PATTERN = re.compile(r'INSERT INTO (.*) VALUES (.*);')
use_statement = None


def _export_insert_lines(lines: List[str], arguments: List[str]):

    if lines is None or len(lines) == 0:
        return

    size_threshold = 100 * 1024 * 1024  # 100MB

    group = []
    group_size = 0
    for line in lines:
        group.append(line)
        group_size += len(line)
        if group_size > size_threshold:
            group = _optimize_insert_lines(group)
            _export_lines(group, arguments)
            group = []
            group_size = 0
    group = _optimize_insert_lines(group)
    _export_lines(group, arguments)


def _optimize_insert_lines(lines: List[str]):
    tables = {}
    for line in lines:
        # match = re.search(r'INSERT INTO (.*) VALUES (.*);', line)
        match = INSERT_PATTERN.search(line)
        if match is None:
            raise ValueError('Cannot understand the INSERT line: ' + line)
        values = tables.get(match.group(1))
        if values is None:
            values = []
        values.append(match.group(2))
        tables[match.group(1)] = values

    formatted_lines = ['INSERT INTO {:s} VALUES {:s};'.format(key, ','.join(tables[key])) for key in tables]

    print('Convert {:d} lines into {:d} lines'.format(len(lines), len(formatted_lines)))

    return formatted_lines


def _export_lines(original_lines: List[str], arguments: List[str]):

    if original_lines is None or len(original_lines) == 0:
        return

    lines = original_lines.copy()

    global use_statement
    if use_statement:
        lines.insert(0, use_statement)

    prefix = ["/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n",
            "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n",
            "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n",
            "/*!50503 SET NAMES utf8 */;\n",
            "/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n",
            "/*!40103 SET TIME_ZONE='+00:00' */;\n",
            "/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;\n",
            "/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n",
            "/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n",
            "/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n"]

    suffix = ["/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;\n",
            "/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;\n",
            "/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;\n",
            "/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;\n",
            "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;\n",
            "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;\n",
            "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;\n",
            "/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;\n"]

    # lines.insert(0, 'SET FOREIGN_KEY_CHECKS=0;\n')
    lines = prefix + lines + suffix
    # lines.append('SET FOREIGN_KEY_CHECKS=1;\n')

    partial_sql_file = tempfile.NamedTemporaryFile(mode='w', prefix='export-to-mysql.')
    partial_sql_file.writelines(lines)
    partial_sql_file.flush()

    print('Executing {:d} lines ({:.3f}MB) starting with:\n{:s}...'.format(
        len(original_lines), 
        getsize(partial_sql_file.name) / (1024 * 1024), 
        original_lines[0][:80]))
    
    command_line = ['mysql'] + arguments + ['<', partial_sql_file.name]
    # print(command_line)
    subprocess.run(' '.join(command_line), shell=True)

    # input()
    partial_sql_file.close()

    if use_statement is None:
        for line in lines:
            if line.startswith('USE'):
                use_statement = line


def export_to_mysql(filename: str, arguments: List[str]):
    
    global use_statement
    use_statement = None

    insert_lines = []
    other_lines = []
    for line in open(filename):
        # if line.startswith('DROP TABLE') and len(partial_lines) > 0:
        #     _export_lines(partial_lines, arguments)
        #     partial_lines = []
        
        if line.startswith('DROP TABLE'):
            _export_lines(other_lines, arguments)
            _export_insert_lines(insert_lines, arguments)
            other_lines = []
            insert_lines = []

        if line.startswith('INSERT'):
            insert_lines.append(line)
        else:
            other_lines.append(line)
    
    _export_lines(other_lines, arguments)
    _export_lines(insert_lines, arguments)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Export a dump file to MySQL')
    parser.add_argument('--dump-file', help='MySQL dump file', required=True)
    args, unknown_args = parser.parse_known_args()

    export_to_mysql(args.dump_file, unknown_args)