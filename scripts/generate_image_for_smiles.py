from rdkit import Chem
from rdkit.Chem.PandasTools import ChangeMoleculeRendering
import argparse


def generateImage(smiles, inchi):
    if smiles is not None:
        mol = Chem.MolFromSmiles(smiles)
    elif inchi is not None:
        mol = Chem.inchi.MolFromInchi(inchi)
    else:
        return

    ChangeMoleculeRendering(renderer='PNG')
    print(mol)


if __name__ == '__main__':
    parser = argparse.ArgumentParser('Generates a PNG image with molecular structure')
    parser.add_argument("--smiles", help='SMILES of a molecule')
    parser.add_argument('--inchi', help='InChI of a molecule')
    args = parser.parse_args()
    generateImage(args.smiles, args.inchi)