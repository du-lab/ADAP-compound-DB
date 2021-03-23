package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreScreenQueryBuilder {

    private Spectrum querySpectrum;
    private double mzTolerance;

    PreScreenQueryBuilder(Spectrum querySpectrum, double mzTolerance) {
        this.querySpectrum = querySpectrum;
        this.mzTolerance = mzTolerance;
    }

    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    public String buildQueryBlock(int numberOfTopMz, Double queryMz) {

        String queryBlock;
        queryBlock = "select * from Spectrum where (";
        queryBlock += IntStream.range(1, numberOfTopMz + 1)
                .mapToObj(i -> String.format("(TopMz" + i + "\t> %f and TopMz" + i + "\t< %f)",
                        queryMz - mzTolerance,
                        queryMz + mzTolerance))
                .collect(Collectors.joining(" or\n"));
        queryBlock += ")\n";

        return queryBlock;
    }

    public String build() {
        String query;
        //TODO It should be `select Count(*) as Common, Id from...`. Your code would work but the column names would be confusing.
        query = "select Count(*), Id as Common from (\n";

        if (querySpectrum.getTopMz1() != null) {
            query = query + buildQueryBlock(8, querySpectrum.getTopMz1());
        }
        if (querySpectrum.getTopMz2() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(9, querySpectrum.getTopMz2());
        }
        if (querySpectrum.getTopMz3() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(10, querySpectrum.getTopMz3());
        }
        if (querySpectrum.getTopMz4() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(11, querySpectrum.getTopMz4());
        }
        if (querySpectrum.getTopMz5() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(12, querySpectrum.getTopMz5());
        }
        if (querySpectrum.getTopMz6() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(13, querySpectrum.getTopMz6());
        }
        if (querySpectrum.getTopMz7() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(14, querySpectrum.getTopMz7());
        }
        if (querySpectrum.getTopMz8() != null) {
            query += "union all\n";
            query = query + buildQueryBlock(15, querySpectrum.getTopMz8());
        }
        query += ") as TempTable\n";
        query += "group by Id order by Common desc";
        return query;
    }
}
