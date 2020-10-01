package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.File;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.Future;

public interface GroupSearchService {

    Future<Void> groupSearch(List<File> files, HttpSession session, String species, String source, String disease);

    /**
     * Calculates the fraction of processed query spectra
     *
     * @return an integer between 0 and 100
     */
    float getProgress();

    void setProgress(float progress);
}
