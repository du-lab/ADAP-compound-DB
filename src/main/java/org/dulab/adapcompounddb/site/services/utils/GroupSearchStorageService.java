package org.dulab.adapcompounddb.site.services.utils;

import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroupSearchStorageService {

    private static final Map<String, Map<String,Object>> searchResults = new ConcurrentHashMap<>();
    private static final Map<String, Integer> searchProgress = new ConcurrentHashMap<>();

    public void storeResults(String jobId, List<SearchResultDTO> results) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("search-results", results);
        searchResults.put(jobId, wrapper);
    }

    public Map<String,Object> getResults(String jobId) {
        return searchResults.get(jobId);
    }

    public void updateProgress(String jobId, int progress) {
        searchProgress.put(jobId, progress);
    }

    public int getProgress(String jobId) {
        return searchProgress.get(jobId);
    }

    public void addSpectraToResults(String jobId, List<SpectrumDTO> spectra){
        Map<String, Object> wrapper = searchResults.get(jobId);
        if(wrapper!= null)
            wrapper.put("spectra", spectra);
    }

}
