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

        query = "select Id, SpectrumId, power(sum(Product), 2) as Score from(\n";
        int index = 0;
        for (Spectrum s: querySpectra) {
           int n = index;
           if (index == 0){

           }else{
               query = query + "\tUNION all\n";
           }
            query += s.getPeaks()
                    .stream()
                    .map(p -> String.format("\tselect %d as Id, SpectrumId, SQRT(Intensity * %f) "
                            + "as Product from Peak join Spectrum on Spectrum.Id = Peak.SpectrumId "
                            + "where Peak.Mz < %f and %f < Peak.Mz "
                            + "and Spectrum.Consensus is False and Spectrum.Reference is False "
                            + "and Spectrum.ChromatographyType = 'GAS'",
                            n,
                            p.getIntensity(),
                            p.getMz() + 0.01,
                            p.getMz() - 0.01)
                    ).collect(Collectors.joining("\tUNION ALL\n"));
            index++;
        }
        query += ") AS Result group by Id, SpectrumId having Score > 0.5 order by Score desc\n";

        return query;
    }
}
