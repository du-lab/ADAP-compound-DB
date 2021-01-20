package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.List;
import java.util.stream.Collectors;

public class MultiSpectrumQueryBuilder {

    private List<Spectrum> querySpectra;

    MultiSpectrumQueryBuilder(List<Spectrum> querySpectra){
        this.querySpectra = querySpectra;
    }

    public List<Spectrum> getQuerySpectra() {
        return querySpectra;
    }

    public void setQuerySpectra(List<Spectrum> querySpectra) {
        this.querySpectra = querySpectra;
    }

    public String build() {
//        StringBuilder librarySelectionBuilder = new StringBuilder();

        String query;
        if (querySpectra == null)
            query = "SELECT 0 as Id,DISTINCT SpectrumId, 0 AS Score FROM Peak\n";
        else{
            query = "select Id, SpectrumId, sum(Product) as Score from(\n";

            for (Spectrum s: querySpectra) {
                query += s.getPeaks()
                        .stream()
                        .map(p -> String.format("\tselect " + s.getId() + " as Id, SpectrumId, SQRT(Intensity * 1.0) "
                                + "as Product from Peak "
                                + "where Peak.Mz - p.Mz < 0.01 or p.Mz - Peak.Mz < 0.01")
                        ).collect(Collectors.joining("\tUNION ALL\n"));
            }

            query += ") AS Result group by Id, SpectrumId having Score > 0.5\n";
        }

        return query;
    }
}
