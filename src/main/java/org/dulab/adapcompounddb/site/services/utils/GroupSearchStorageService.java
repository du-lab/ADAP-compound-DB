package org.dulab.adapcompounddb.site.services.utils;

import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroupSearchStorageService {

    private static final Map<String, List<SearchResultDTO>> searchResults = new ConcurrentHashMap<>();
    private static final Map<String, Integer> searchProgress = new ConcurrentHashMap<>();

    public void storeResults(String jobId, List<SearchResultDTO> results) {
        searchResults.put(jobId, results);
    }

    public List<SearchResultDTO> getResults(String jobId) {
        return searchResults.get(jobId);
    }

    public void updateProgress(String jobId, int progress) {
        searchProgress.put(jobId, progress);
    }

    public int getProgress(String jobId) {
        return searchProgress.get(jobId);
    }

}
