import argparse
import pandas as pd
import re
import sys

from os.path import splitext
from typing import List



def get_meta_lines(data: pd.DataFrame, id: str) -> List[str]:
    data = data[data['Compound ID'] == id]
    if (len(data) == 0):
        raise ValueError('Cannot locate row with ID = {:s}'.format(id))
    if (len(data) > 1):
        print('WARNING: {:d} lines found for ID = {:s}'.format(len(data), id), file=sys.stderr)

    ret_time = data.iloc[0]['Retention time (min)']

    # external_id_line = 'EXTERNAL_ID: {:s}\n'.format(id)
    ret_time_line = 'RT: {:s}\n'.format(ret_time)

    return [ret_time_line]


def get_id_from_name(name: str) -> str:
    compound_id, _ = name.strip().split(maxsplit=1)
    # match = re.search(r'^(.+)\(.+\)', name)
    # if not match:
    #     raise ValueError('Cannod locate ID of compound ' + name)
    
    # compound_id = match.group(1)
    return compound_id.strip()


def merge_sumner_data(path_to_msp: str, path_to_csv: str):

    meta_data = pd.read_csv(path_to_csv, header=0)
    meta_data = meta_data.astype(str)

    filename, extension = splitext(path_to_msp)
    path_to_output = filename + '.merged' + extension
    with open(path_to_output, 'w') as output:

        count_spectra = 0
        count_matches = 0
        for line in open(path_to_msp):
            output.write(line)
            if ':' in line:
                key, value = line.split(':', maxsplit=1)
                key = key.strip()

                if key == 'Name':
                    compound_id = get_id_from_name(value.strip())
                    lines = get_meta_lines(meta_data, compound_id)
                    output.writelines(lines)

                # elif key == 'ID' or key == 'PUBCHEM_COMPOUND_CID' or key == 'PUBCHEM_SUBSTANCE_ID' or key == 'HMDB_ID' or key == 'CAS' or key == 'Compound Id':
                #     compound_ids.append(value.strip())
                
                # elif key == 'Num Peaks':
                #     if len(compound_ids) == 1:
                #         lines = 
                #         output.writelines(lines)
                #         count_matches += 1
                #     else:
                #         raise ValueError('Cannod locate ID of compound ' + name)
            
            
    
        # print('{:d} of {:d} spectra matched to the csv file'.format(count_matches, count_spectra))


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="Merges Sumner's data .csv file into the corresponding .msp file")
    parser.add_argument('--msp-file', help='MSP file', required=True)
    parser.add_argument('--csv-file', help='CSV file', required=True)

    args = parser.parse_args()
    merge_sumner_data(args.msp_file, args.csv_file)
