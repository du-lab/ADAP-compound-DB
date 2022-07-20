from rdkit import Chem
from rdkit.Chem import rdDepictor
from rdkit.Chem.Draw import rdMolDraw2D
import argparse


def generateImage(smiles, inchi):
    if smiles is not None:
        mol = Chem.MolFromSmiles(smiles)
    elif inchi is not None:
        mol = Chem.inchi.MolFromInchi(inchi)
    else:
        return

    mc = Chem.Mol(mol.ToBinary())
    if not mc.GetNumConformers():
        rdDepictor.Compute2DCoords(mc)
    drawer = rdMolDraw2D.MolDraw2DSVG(400, 300)
    drawer.DrawMolecule(mc)
    drawer.FinishDrawing()
    svg = drawer.GetDrawingText()
    print(svg)


if __name__ == '__main__':
    parser = argparse.ArgumentParser('Generates a PNG image with molecular structure')
    parser.add_argument("--smiles", help='SMILES of a molecule')
    parser.add_argument('--inchi', help='InChI of a molecule')
    args = parser.parse_args()
    generateImage(args.smiles, args.inchi)