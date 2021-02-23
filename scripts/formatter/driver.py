import argparse
import pandas as pd
import subprocess


def driver(path: str):
    print(path)
    df = pd.read_csv(path, header=0)
    for _, row in df.iterrows():
        command_line = ['python3', 'merge_sumner_data.py', '--msp-file', row['msp-file'], '--csv-file', row['csv-file'],
                        '--sdf-file', row['sdf-file']]
        print(command_line)
        subprocess.run(command_line, check=True)
        print()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Executes merge_sumner_data.py')
    parser.add_argument('file', help='CSV file with paths to MSP, CSV, and SDF files')
    
    args = parser.parse_args()
    driver(args.file)