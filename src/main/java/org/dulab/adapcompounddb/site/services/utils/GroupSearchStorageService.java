package org.dulab.adapcompounddb.site.services.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class GroupSearchStorageService {

    private static final Map<String, Map<String,Object>> searchResults = new ConcurrentHashMap<>();
    private static final Map<String, Double> searchProgress = new ConcurrentHashMap<>();
    private static final Map<String, Future<?>> activeSearchJobs = new ConcurrentHashMap<>();
    public void storeResults(String jobId, List<SearchResultDTO> results) {
        Map<String, List<Map<String, Object>>> resultJson = new HashMap<>();
        resultJson.put("matches", new ArrayList<>());
        resultJson.put("compounds", new ArrayList<>());

        Set<Long> uniqueSpectrumIds = new HashSet<>();
        for (SearchResultDTO result : results) {
            if(result.getSpectrumId() == 0) continue;
            Map<String, Object> matchesJson = new HashMap<>();
            matchesJson.put("spectrumId", result.getSpectrumId());
            //to prevent duplicates compounds
            boolean unique = uniqueSpectrumIds.add(result.getSpectrumId());
            if(unique) {
                Map<String, Object> compound = new HashMap<>();
                if (result.getSpectrumId() != 0) compound.put("spectrumId", result.getSpectrumId());
                if(result.getName() != null) compound.put("name", result.getName());
                if (result.getMass() > 0.0) compound.put("mass", result.getMass());
                if (result.getQueryPrecursorMz() > 0.0) compound.put("precursorMz", result.getQueryPrecursorMz());
                if (result.getPrecursorType() != null) compound.put("precursorType", result.getPrecursorType());
                if (result.getFormula() != null) compound.put("formula", result.getFormula());
                if (result.getCasId() != null) compound.put("casId", result.getCasId());
                if (result.getPubChemId() != null) compound.put("pubChemId", result.getPubChemId());
                if (result.getInChIKey() != null) compound.put("inchikey", result.getInChIKey());
                if (result.getHmdbId() != null) compound.put("hmdbId", result.getHmdbId());

                resultJson.get("compounds").add(compound);
            }
            if (result.getQuerySpectrumName() != null) matchesJson.put("querySpectrumName", result.getQuerySpectrumName());
            if(result.getScore() > 0.0) matchesJson.put("score", result.getScore());
            if(result.getRetTimeError() < Double.MAX_VALUE) matchesJson.put("retTimeError", result.getRetTimeError());
            if(result.getPrecursorErrorPPM() < Double.MAX_VALUE) matchesJson.put("precursorErrorPPM", result.getPrecursorErrorPPM());
            if (result.getMassErrorPPM() < Double.MAX_VALUE) matchesJson.put("massError", result.getMassErrorPPM());
            if (result.getOntologyLevel() != null) matchesJson.put("ontologyLevel", result.getOntologyLevel());
            resultJson.get("matches").add(matchesJson);


        }
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("search-results", resultJson);
        searchResults.put(jobId, wrapper);
    }
    public void storeSearchJob(String jobId, Future<?> searchTask) {
        activeSearchJobs.put(jobId, searchTask);
    }

    public boolean cancelSearchJob(String jobId) {
        Future<?> task = activeSearchJobs.get(jobId);
        if (task != null) {
            // cancel task and remove task and its results from memory
            boolean cancelled = task.cancel(true);
            if(cancelled){
                clear(jobId);
                return true;
            }
        }
        return false;
    }
    public boolean isCanceled(String jobId) {
        Future<?> job = activeSearchJobs.get(jobId);
        return (job != null && job.isCancelled()) ;
    }
    public void removeSearchJob(String jobId) {
        activeSearchJobs.remove(jobId);
    }
    public Map<String,Object> getResults(String jobId) {
        return searchResults.get(jobId);
    }

    public void updateProgress(String jobId, double progress) {
        searchProgress.put(jobId, progress);
    }

    public Double getProgress(String jobId) {
        return searchProgress.get(jobId);
    }

    public void addSpectraToResults(String jobId, Object obj){
        Map<String, Object> result = searchResults.get(jobId);
        if(result!= null)
            result.put("spectra", obj);
    }

    public void clear(String jobId) {
       searchResults.remove(jobId);
       searchProgress.remove(jobId);
       activeSearchJobs.remove(jobId);
    }
}
