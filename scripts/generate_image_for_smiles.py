from rdkit import Chem
from rdkit.Chem.PandasTools import ChangeMoleculeRendering
from rdkit.Chem.Draw import rdMolDraw2D
import argparse


def generateImage(smiles, inchi):
    if smiles is not None:
        mol = Chem.MolFromSmiles(smiles)
    elif inchi is not None:
        mol = Chem.inchi.MolFromInchi(inchi)
    else:
        return

    drawer = rdMolDraw2D.MolDraw2DSVG(400, 300)
    drawer.DrawMolecule(Chem.Mol(mol.ToBinary()))
    drawer.FinishDrawing()
    svg = drawer.GetDrawingText()
    # ChangeMoleculeRendering(renderer='PNG')
    print(svg)


if __name__ == '__main__':
    parser = argparse.ArgumentParser('Generates a PNG image with molecular structure')
    parser.add_argument("--smiles", help='SMILES of a molecule')
    parser.add_argument('--inchi', help='InChI of a molecule')
    args = parser.parse_args()
    generateImage(args.smiles, args.inchi)