from rdkit import Chem
from rdkit.Chem.PandasTools import ChangeMoleculeRendering
import argparse


def generateImage(smiles):
    mol = Chem.MolFromSmiles(smiles)

    ChangeMoleculeRendering(renderer='PNG')
    print(mol)

parser = argparse.ArgumentParser()
parser.add_argument("smiles")
args = parser.parse_args()
generateImage(args.smiles)