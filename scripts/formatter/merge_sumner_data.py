import argparse
import pandas as pd
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
    return compound_id.strip()


def get_meta_rows(data: pd.DataFrame, id: str) -> List[pd.Series]:
    data = data[data['Compound ID'] == id]
    if (len(data) == 0):
        raise ValueError('Cannot locate row with ID = {:s}'.format(id))

    rows = [row for _, row in data.iterrows()]
    return rows


def get_modified_spectrum_lines(spectrum_lines: List[str], row: pd.Series) -> List[str]:
    ret_time = row['Retention time (min)']
    ret_time_line = 'RT: {:s}\n'.format(ret_time)

    spectrum_lines = spectrum_lines.copy()
    spectrum_lines.insert(1, ret_time_line)
    return spectrum_lines


def merge_sumner_data(path_to_msp: str, path_to_csv: str):

    meta_data = pd.read_csv(path_to_csv, header=0)
    meta_data = meta_data.astype(str)

    filename, extension = splitext(path_to_msp)
    path_to_output = filename + '.merged' + extension
    with open(path_to_output, 'w') as output:

        count_spectra = 0
        count_matches = 0
        spectrum_lines = []
        compound_id = None
        for line in open(path_to_msp):
            spectrum_lines.append(line)
            line = line.strip()
            if len(line) == 0:
                # End of a record is reached. Time to write this record
                if len(spectrum_lines) > 1 and compound_id is not None:
                    rows = get_meta_rows(meta_data, compound_id)
                    for row in rows:
                        lines = get_modified_spectrum_lines(spectrum_lines, row)
                        output.writelines(lines)
                spectrum_lines = []
                compound_id = None
            
            elif ':' in line:
                key, value = line.split(':', maxsplit=1)
                if key.strip() == 'Name':
                    compound_id = get_id_from_name(value.strip())
                    # lines = get_meta_lines(meta_data, compound_id)
                    # output.writelines(lines)


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description="Merges Sumner's data .csv file into the corresponding .msp file")
    parser.add_argument('--msp-file', help='MSP file', required=True)
    parser.add_argument('--csv-file', help='CSV file', required=True)

    args = parser.parse_args()
    merge_sumner_data(args.msp_file, args.csv_file)
