package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.List;

public interface MathService {

    double diversityIndex(List<Spectrum> spectra, SubmissionCategoryType type);
}
