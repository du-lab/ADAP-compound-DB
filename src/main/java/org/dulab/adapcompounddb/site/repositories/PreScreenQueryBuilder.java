package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreScreenQueryBuilder {

    private final boolean searchConsensusSpectra;
    private final boolean searchReferenceSpectra;
    private final boolean searchClusterableSpectra;

    private ChromatographyType chromatographyType;
    private Spectrum querySpectrum;
    private Double mzTolerance = null;
    private Double precursorMz = null;
    private Double precursorTolerance = null;
    private Double retTime = null;
    private Double retTimeTolerance = null;
    private Double mass = null;
    private double[] masses = null;
    private Double massTolerance = null;
    private Double massTolerancePPM = null;


    public PreScreenQueryBuilder(
            boolean searchConsensusSpectra, boolean searchReferenceSpectra, boolean searchClusterableSpectra) {

        this.searchConsensusSpectra = searchConsensusSpectra;
        this.searchReferenceSpectra = searchReferenceSpectra;
        this.searchClusterableSpectra = searchClusterableSpectra;
    }

    public PreScreenQueryBuilder withChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
        return this;
    }

    public PreScreenQueryBuilder withPrecursor(Double mz, Double tolerance) {
        this.precursorMz = mz;
        this.precursorTolerance = tolerance;
        return this;
    }

    public PreScreenQueryBuilder withRetTime(Double retTime, Double tolerance) {
        this.retTime = retTime;
        this.retTimeTolerance = tolerance;
        return this;
    }

    public PreScreenQueryBuilder withMass(Double mass, Double tolerance) {
        this.mass = mass;
        this.massTolerance = tolerance;
        return this;
    }

    public PreScreenQueryBuilder withMassPPM(Double mass, Double ppm) {
        this.mass = mass;
        this.massTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withMasses(double[] masses, Double tolerance) {
        this.masses = masses;
        this.massTolerance = tolerance;
        return this;
    }

    public PreScreenQueryBuilder withMassesPPM(double[] masses, Double ppm) {
        this.masses = masses;
        this.massTolerancePPM = ppm;
        return this;
    }

    public PreScreenQueryBuilder withQuerySpectrum(Spectrum querySpectrum, Double mzTolerance) {
        this.querySpectrum = querySpectrum;
        this.mzTolerance = mzTolerance;
        return this;
    }


    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    public String buildQueryBlock(int numberOfTopMz, Double queryMz) {

        String queryBlock = String.format(
                "SELECT * FROM Spectrum WHERE Consensus IS %b AND Reference IS %b AND Clusterable IS %b",
                searchConsensusSpectra, searchReferenceSpectra, searchClusterableSpectra);

        if (chromatographyType != null)
            queryBlock += String.format(" AND Spectrum.ChromatographyType = '%s'", chromatographyType);

        if (precursorMz != null && precursorTolerance != null)
            queryBlock += String.format(" AND Spectrum.Precursor > %f AND Spectrum.Precursor < %f",
                    precursorMz - precursorTolerance, precursorMz + precursorTolerance);

        if (mass != null && massTolerance != null)
            queryBlock += String.format(" AND Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f",
                    mass - massTolerance, mass + massTolerance);

        if (mass != null && massTolerancePPM != null) {
            double x = 1e-6 * massTolerancePPM;
            queryBlock += String.format(" AND Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f",
                    mass / (1 + x), mass / (1 - x));
        }

//        if (mass == null && masses != null && massTolerance != null)
//            spectrumSelector += String.format(" AND (%s)", Arrays.stream(masses)
//                    .mapToObj(mass -> String.format(
//                            "(Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f)",
//                            mass - massTolerance, mass + massTolerance))
//                    .collect(Collectors.joining(" OR ")));
//
//        if (mass == null && masses != null && massTolerancePPM != null) {
//            double x = 1e-6 * massTolerancePPM;
//            spectrumSelector += String.format(" AND (%s)", Arrays.stream(masses)
//                    .mapToObj(mass -> String.format(
//                            "(Spectrum.MolecularWeight > %f AND Spectrum.MolecularWeight < %f)",
//                            mass / (1 + x), mass / (1 - x)))
//                    .collect(Collectors.joining(" OR ")));
//        }

        if (retTime != null && retTimeTolerance != null)
            queryBlock += String.format(" AND Spectrum.RetentionTime > %f AND Spectrum.RetentionTime < %f",
                    retTime - retTimeTolerance, retTime + retTimeTolerance);

        if (querySpectrum != null && mzTolerance != null) {
            queryBlock += IntStream.range(1, numberOfTopMz + 1)
                    .mapToObj(i -> String.format("(TopMz" + i + "\t> %f and TopMz" + i + "\t< %f)",
                            queryMz - mzTolerance,
                            queryMz + mzTolerance))
                    .collect(Collectors.joining(" or\n"));
        }

        queryBlock += ")\n";

        return queryBlock;
    }

    public String build() {
        String query;
        query = "SELECT COUNT(*) as Common, Id FROM (\n";

        if (querySpectrum != null && mzTolerance != null) {
            if (querySpectrum.getTopMz1() != null) {
                query += buildQueryBlock(8, querySpectrum.getTopMz1());
            }
            if (querySpectrum.getTopMz2() != null) {
                query += "union all\n";
                query += buildQueryBlock(9, querySpectrum.getTopMz2());
            }
            if (querySpectrum.getTopMz3() != null) {
                query += "union all\n";
                query += buildQueryBlock(10, querySpectrum.getTopMz3());
            }
            if (querySpectrum.getTopMz4() != null) {
                query += "union all\n";
                query += buildQueryBlock(11, querySpectrum.getTopMz4());
            }
            if (querySpectrum.getTopMz5() != null) {
                query += "union all\n";
                query += buildQueryBlock(12, querySpectrum.getTopMz5());
            }
            if (querySpectrum.getTopMz6() != null) {
                query += "union all\n";
                query += buildQueryBlock(13, querySpectrum.getTopMz6());
            }
            if (querySpectrum.getTopMz7() != null) {
                query += "union all\n";
                query += buildQueryBlock(14, querySpectrum.getTopMz7());
            }
            if (querySpectrum.getTopMz8() != null) {
                query += "union all\n";
                query += buildQueryBlock(15, querySpectrum.getTopMz8());
            }

        } else {
            query += buildQueryBlock(0, null);
        }
        query += ") AS TempTable\n";
        query += "GROUP BY Id ORDER BY Common DESC";
        return query;
    }
}
