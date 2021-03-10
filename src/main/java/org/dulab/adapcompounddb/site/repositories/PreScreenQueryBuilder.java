package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreScreenQueryBuilder {
    private Spectrum querySpectrum;

    PreScreenQueryBuilder(Spectrum querySpectrum){
        this.querySpectrum = querySpectrum;
    }

    public Spectrum getQuerySpectrum() {
        return querySpectrum;
    }

    public void setQuerySpectrum(Spectrum querySpectrum) {
        this.querySpectrum = querySpectrum;
    }

    public String buildQueryBlock(int numberOfTopMz, Spectrum s){
        String queryBlock;
        queryBlock = "select * from Spectrum where (";
        // create a map to map String variable to each topMz value of query sepctrum
        Map topMzValueMap = new HashMap();
        topMzValueMap.put("TopMz1", s.getTopMz1());
        topMzValueMap.put("TopMz2", s.getTopMz2());
        topMzValueMap.put("TopMz3", s.getTopMz3());
        topMzValueMap.put("TopMz4", s.getTopMz4());
        topMzValueMap.put("TopMz5", s.getTopMz5());
        topMzValueMap.put("TopMz6", s.getTopMz6());
        topMzValueMap.put("TopMz7", s.getTopMz7());
        topMzValueMap.put("TopMz8", s.getTopMz8());
        Set mapKeys = topMzValueMap.keySet();

         for(int i = 1; i <= numberOfTopMz; i++){
             String indexOfTopMz = "TopMz" + (numberOfTopMz - 8 + 1);
             String topMzNumber = "TopMz" + i;
             //check if Top m/z value is null
             queryBlock += String.format("(" + topMzNumber + "\t> %f and\t" + topMzNumber + "\t< %f)",
                     Double.parseDouble(topMzValueMap.get(indexOfTopMz).toString()) - 0.1,
                     Double.parseDouble(topMzValueMap.get(indexOfTopMz).toString()) + 0.1);
             //make sure the last line do not contain "or" keyword
             if(i != numberOfTopMz){
                 queryBlock += "\tor\n";
             }else{
                 queryBlock += ")\n";
             }
         }
        return queryBlock;
    }

    public String build(){
        String query;
        query = "select Id, Count(*) as Common from (\n";
        if(querySpectrum.getTopMz1()!=null & querySpectrum.getTopMz2()!=null & querySpectrum.getTopMz3()!=null
                & querySpectrum.getTopMz4()!=null & querySpectrum.getTopMz5()!=null & querySpectrum.getTopMz6()!=null
                & querySpectrum.getTopMz7()!=null & querySpectrum.getTopMz8()!=null){
            query = query + buildQueryBlock(8, querySpectrum);
        }
        if(querySpectrum.getTopMz9()!=null){
            query += "union all\n";
            query = query + buildQueryBlock(9, querySpectrum);
        }
        if(querySpectrum.getTopMz10()!=null) {
            query += "union all\n";
            query = query + buildQueryBlock(10, querySpectrum);
        }
        if(querySpectrum.getTopMz11()!=null) {
            query += "union all\n";
            query = query + buildQueryBlock(11, querySpectrum);
        }
        if(querySpectrum.getTopMz12()!=null) {
            query += "union all\n";
            query = query + buildQueryBlock(12, querySpectrum);
        }
        if(querySpectrum.getTopMz13()!=null) {
            query += "union all\n";
            query = query + buildQueryBlock(13, querySpectrum);
        }
        if(querySpectrum.getTopMz14()!=null) {
            query += "union all\n";
            query = query + buildQueryBlock(14, querySpectrum);
        }
        if(querySpectrum.getTopMz15()!=null) {
            query += "union all\n";
            query = query + buildQueryBlock(15, querySpectrum);
        }
//        if(querySpectrum.getTopMz16()!=null) {
//            query += "union all\n";
//            query = query + buildQueryBlock(16, querySpectrum);
//        }
        query += ") as TempTable\n";
        query += "group by Id order by Common desc";
    return query;
    }
}
