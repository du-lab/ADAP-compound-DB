package org.dulab.adapcompounddb.models.dto;

import org.apache.commons.lang3.SerializationUtils;
import org.dulab.adapcompounddb.models.MatchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.MassSearchResult;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.models.enums.IdentifierType;
import org.dulab.adapcompounddb.models.ontology.OntologyLevel;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class SearchResultDTO implements Serializable, Comparable<SearchResultDTO> {

    private static final long serialVersionUID = 1L;

    public static final Map<Integer, Function<SearchResultDTO, Comparable>> COLUMN_TO_FIELD_MAP = new HashMap<>();

    static {
        COLUMN_TO_FIELD_MAP.put(1, SearchResultDTO::getQuerySpectrumName);
        COLUMN_TO_FIELD_MAP.put(2, SearchResultDTO::getName);
        COLUMN_TO_FIELD_MAP.put(3, SearchResultDTO::getSize);
        COLUMN_TO_FIELD_MAP.put(4, SearchResultDTO::getScore);
        COLUMN_TO_FIELD_MAP.put(5, SearchResultDTO::getAveSignificance);
        COLUMN_TO_FIELD_MAP.put(6, SearchResultDTO::getMinSignificance);
        COLUMN_TO_FIELD_MAP.put(7, SearchResultDTO::getSpeciesPValue);
        COLUMN_TO_FIELD_MAP.put(8, SearchResultDTO::getSampleSourcePValue);
        COLUMN_TO_FIELD_MAP.put(9, SearchResultDTO::getDiseasePValue);
        COLUMN_TO_FIELD_MAP.put(10, SearchResultDTO::getMinPValue);
        COLUMN_TO_FIELD_MAP.put(11, SearchResultDTO::getChromatographyTypeLabel);
    }

    // *************************
    // ***** Entity fields *****
    // *************************

    // Query
    private long querySpectrumId;
    private int querySpectrumIndex;
    private int queryFileIndex;
    private String querySpectrumName;
    private String querySpectrumShortName;
    private String queryExternalId;
    private boolean queryWithPeaks;
    private double[] queryPrecursorMzs;
    private String[] queryPrecursorTypes;
    private double queryMass;
    private double queryRetTime;
    private double[] queryPeakMzs;

    // Match
    private MatchType matchType;
    private int matchIndex;
    private long spectrumId;
    private long clusterId;
    private String externalId;
    private String name;
    private String precursorType;
    private int size;
    private double aveSignificance;
    private double minSignificance;
    private double maxSignificance;
    private double minPValue;
    private double diseasePValue;
    private double speciesPValue;
    private double sampleSourcePValue;
    private String ontologyLevel;
    private int ontologyPriority;
    private String chromatographyTypeLabel;
    private String chromatographyTypePath;
    private String json;
    private double mass;
    private double retTime;
    private String formula;
    private String casId;
    private String hmdbId;
    private String pubChemId;
    private String inChIKey;
    private String submissionName;
    private boolean inHouse;
    private long submissionId;
    private double[] libraryPeakMzs;

    // Other
    private int position;
    private double score;
    private double isotopicSimilarity;
    private double precursorError;
    private double precursorErrorPPM;
    private double massError;
    private double massErrorPPM;
    private double retTimeError;
    private double retIndexError;
    private boolean marked;
    private String errorMessage = null;


    public SearchResultDTO() {}

    public SearchResultDTO(Spectrum querySpectrum, SpectrumClusterView view) {
        this(querySpectrum);

        if (view != null) {
            boolean isReference = view.getClusterId() == null;
            this.matchType = (isReference) ? MatchType.SPECTRUM : MatchType.CLUSTER;
            this.spectrumId = view.getId();
            this.clusterId = view.getClusterId();
            this.name = (isReference ? "[Ref Spec] " : "[Con Spec] ") + view.getName();
            this.size = view.getSize();
            this.score = view.getScore();
            this.massError = view.getMassError();
            this.massErrorPPM = view.getMassErrorPPM();
            this.retTimeError = view.getRetTimeError();
            this.aveSignificance = view.getAverageSignificance();
            this.minSignificance = view.getMinimumSignificance();
            this.maxSignificance = view.getMaximumSignificance();
            this.diseasePValue = view.getDiseasePValue();
            this.speciesPValue = view.getSpeciesPValue();
            this.sampleSourcePValue = view.getSampleSourcePValue();
            this.minPValue = view.getMinPValue();
            this.chromatographyTypeLabel = view.getChromatographyType().getLabel();
            this.chromatographyTypePath = view.getChromatographyType().getIconPath();
        }
    }

    public SearchResultDTO(SpectrumClusterView view) {
        this(null, view);
    }

    public SearchResultDTO(Spectrum querySpectrum) {
        matchType = MatchType.SPECTRUM;
        if (querySpectrum != null) {
            this.querySpectrumId = querySpectrum.getId();
            this.querySpectrumName = querySpectrum.getName();
            this.querySpectrumShortName = querySpectrum.getShortName();
            this.queryExternalId = querySpectrum.getExternalId();
            this.queryWithPeaks = querySpectrum.getPeaks() != null;
            this.setQueryPrecursorMz(querySpectrum.getPrecursor());
            this.setQueryPrecursorType(querySpectrum.getPrecursorType());
            if (querySpectrum.getMass() != null)
                this.queryMass = querySpectrum.getMass();
            this.queryRetTime = querySpectrum.getRetentionTime();
            if(querySpectrum.getChromatographyType() != null) {
                this.chromatographyTypeLabel = querySpectrum.getChromatographyType().getLabel();
                this.chromatographyTypePath = querySpectrum.getChromatographyType().getIconPath();
            }
        }
    }

    public SearchResultDTO(SpectrumMatch spectrumMatch, Integer matchIndex) {
        this(spectrumMatch.getQuerySpectrum());

        Spectrum matchSpectrum = spectrumMatch.getMatchSpectrum();
        if (matchSpectrum != null) {
            this.matchType = matchSpectrum.isConsensus() ? MatchType.CLUSTER : MatchType.SPECTRUM;
            this.matchIndex = matchIndex;
            this.spectrumId = matchSpectrum.getId();
            this.name = matchSpectrum.getShortName();
            this.externalId = matchSpectrum.getExternalId();
            this.size = 1;
            if (matchSpectrum.getMass() != null)
                this.mass = matchSpectrum.getMass();
            if (matchSpectrum.getRetentionTime() != null)
                this.retTime = matchSpectrum.getRetentionTime();
            this.formula = matchSpectrum.getFormula();
            this.inHouse = matchSpectrum.isInHouseReference();
            this.inChIKey = matchSpectrum.getInChiKey();

            Map<IdentifierType, String> identifiers = matchSpectrum.getIdentifiersAsMap();
            if (identifiers != null) {
                this.casId = identifiers.get(IdentifierType.CAS);
                this.hmdbId = identifiers.get(IdentifierType.HMDB);
                this.pubChemId = identifiers.get(IdentifierType.PUBCHEM);
            }

            if (spectrumMatch.getScore() != null)
                this.score = spectrumMatch.getScore();
            if (spectrumMatch.getIsotopicSimilarity() != null)
                this.isotopicSimilarity = spectrumMatch.getIsotopicSimilarity();
            if (spectrumMatch.getPrecursorError() != null)
                this.precursorError = spectrumMatch.getPrecursorError();
            if (spectrumMatch.getPrecursorErrorPPM() != null)
                this.precursorErrorPPM = spectrumMatch.getPrecursorErrorPPM();
            this.precursorType = spectrumMatch.getPrecursorType();
            if (spectrumMatch.getMassError() != null)
                this.massError = spectrumMatch.getMassError();
            if (spectrumMatch.getMassErrorPPM() != null)
                this.massErrorPPM = spectrumMatch.getMassErrorPPM();
            if (spectrumMatch.getRetTimeError() != null)
                this.retTimeError = spectrumMatch.getRetTimeError();
            if (spectrumMatch.getRetIndexError() != null)
                this.retIndexError = spectrumMatch.getRetIndexError();

            SpectrumCluster cluster = matchSpectrum.getCluster();
            if (cluster != null) {
                this.clusterId = cluster.getId();
                this.size = cluster.getSize();
                this.aveSignificance = cluster.getAveSignificance();
                this.minSignificance = cluster.getMinSignificance();
                this.maxSignificance = cluster.getMaxSignificance();
                this.speciesPValue = cluster.getSpeciesPValue();
                this.sampleSourcePValue = cluster.getSampleSourcePValue();
                this.diseasePValue = cluster.getDiseasePValue();
                this.minPValue = cluster.getMinPValue();
            }

            File file = matchSpectrum.getFile();
            if (file != null) {
                Submission submission = file.getSubmission();
                if (submission != null) {
                    this.submissionName = submission.getName();
                    this.submissionId = submission.getId();
                }
            }
        }
    }

    public SearchResultDTO(Spectrum querySpectrum, MassSearchResult massSearchResult) {
        this(querySpectrum);

        matchType = MatchType.SPECTRUM;

        if (massSearchResult != null) {
            this.spectrumId = massSearchResult.getId();
            this.name = massSearchResult.getName();
            this.chromatographyTypeLabel = massSearchResult.getChromatographyType().getLabel();
            this.chromatographyTypePath = massSearchResult.getChromatographyType().getIconPath();
            this.mass = massSearchResult.getMolecularWeight();
            this.massError = massSearchResult.getError();
        }
    }

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    public int getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(int matchIndex) {
        this.matchIndex = matchIndex;
    }

    public long getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(final long spectrumId) {
        this.spectrumId = spectrumId;
    }

    public long getClusterId() {
        return clusterId;
    }

    public void setClusterId(long clusterId) {
        this.clusterId = clusterId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public String getPrecursorType() {
        return precursorType;
    }

    public void setPrecursorType(String precursorType) {
        this.precursorType = precursorType;
    }

    public double getScore() {
        return score;
    }

    public long getNISTScore() {
        return (score != 0) ? Math.round(score * 1000) : 0;
    }

    public void setScore(final double score) {
        this.score = score;
    }

    public double getIsotopicSimilarity() {
        return isotopicSimilarity;
    }

    public void setIsotopicSimilarity(double isotopicSimilarity) {
        this.isotopicSimilarity = isotopicSimilarity;
    }

    public double getAveSignificance() {
        return aveSignificance;
    }

    public void setAveSignificance(final double aveSignificance) {
        this.aveSignificance = aveSignificance;
    }

    public double getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(final double minSignificance) {
        this.minSignificance = minSignificance;
    }

    public double getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(final double maxSignificance) {
        this.maxSignificance = maxSignificance;
    }

    public double getMinPValue()
    {
        return minPValue;
    }

    public void setMinPValue(double minPValue)
    {
        this.minPValue = minPValue;
    }

    public double getDiseasePValue()
    {
        return diseasePValue;
    }

    public void setDiseasePValue(double diseasePValue)
    {
        this.diseasePValue = diseasePValue;
    }

    public double getSpeciesPValue()
    {
        return speciesPValue;
    }

    public void setSpeciesPValue(double speciesPValue)
    {
        this.speciesPValue = speciesPValue;
    }

    public double getSampleSourcePValue()
    {
        return sampleSourcePValue;
    }

    public void setSampleSourcePValue(double sampleSourcePValue)
    {
        this.sampleSourcePValue = sampleSourcePValue;
    }

    public String getOntologyLevel() {
        return ontologyLevel;
    }

    public void setOntologyLevel(String ontologyLevel) {
        this.ontologyLevel = ontologyLevel;
    }

    public int getOntologyPriority() {
        return ontologyPriority;
    }

    public void setOntologyPriority(int ontologyPriority) {
        this.ontologyPriority = ontologyPriority;
    }

    public void setOntologyLevel(@Nullable OntologyLevel ontologyLevel) {
        if (ontologyLevel != null) {
            this.setOntologyLevel(ontologyLevel.getLabel());
            this.setOntologyPriority(ontologyLevel.getPriority());
        } else {
            this.setOntologyLevel((String) null);
            this.setOntologyPriority(0);
        }
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getCasId() {
        return casId;
    }

    public void setCasId(String casId) {
        this.casId = casId;
    }

    public String getHmdbId() {
        return hmdbId;
    }

    public void setHmdbId(String hmdbId) {
        this.hmdbId = hmdbId;
    }

    public String getPubChemId() {
        return pubChemId;
    }

    public void setPubChemId(String pubChemId) {
        this.pubChemId = pubChemId;
    }

    public String getInChIKey() {
        return inChIKey;
    }

    public void setInChIKey(String inChIKey) {
        this.inChIKey = inChIKey;
    }

    public String getSubmissionName() {
        return submissionName;
    }

    public void setSubmissionName(String submissionName) {
        this.submissionName = submissionName;
    }

    public long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }

    public boolean getInHouse() {
        return inHouse;
    }

    public void setInHouse(boolean inHouse) {
        this.inHouse = inHouse;
    }

    public String getQuerySpectrumName() {
        return querySpectrumName;
    }

    public void setQuerySpectrumName(String querySpectrumName) {
        this.querySpectrumName = querySpectrumName;
    }

    public String getQuerySpectrumShortName() {
        return querySpectrumShortName;
    }

    public void setQuerySpectrumShortName(String querySpectrumShortName) {
        this.querySpectrumShortName = querySpectrumShortName;
    }

    public long getQuerySpectrumId() {
        return querySpectrumId;
    }

    public void setQuerySpectrumId(long querySpectrumId) {
        this.querySpectrumId = querySpectrumId;
    }

    public int getQuerySpectrumIndex() {
        return querySpectrumIndex;
    }

    public void setQuerySpectrumIndex(int querySpectrumIndex) {
        this.querySpectrumIndex = querySpectrumIndex;
    }

    public int getQueryFileIndex() {
        return queryFileIndex;
    }

    public void setQueryFileIndex(int queryFileIndex) {
        this.queryFileIndex = queryFileIndex;
    }

    public String getQueryExternalId() {
        return queryExternalId;
    }

    public void setQueryExternalId(String queryExternalId) {
        this.queryExternalId = queryExternalId;
    }

    public boolean isQueryWithPeaks() {
        return queryWithPeaks;
    }

    public void setQueryWithPeaks(boolean queryWithPeaks) {
        this.queryWithPeaks = queryWithPeaks;
    }

    public double getQueryPrecursorMz() {
        if (queryPrecursorMzs != null && queryPrecursorMzs.length > 0)
            return queryPrecursorMzs[0];
        return 0.0;
    }

    public void setQueryPrecursorMz(double precursorMz) {
        this.queryPrecursorMzs = precursorMz > 0.0 ? new double[]{precursorMz} : null;
    }

    public double[] getQueryPrecursorMzs() {
        return queryPrecursorMzs;
    }

    public void setQueryPrecursorMzs(double[] queryPrecursorMzs) {
        this.queryPrecursorMzs = queryPrecursorMzs;
    }

    public String getQueryPrecursorType() {
        if (queryPrecursorTypes != null && queryPrecursorTypes.length > 0)
            return queryPrecursorTypes[0];
        return null;
    }

    public void setQueryPrecursorType(String precursorType) {
        this.queryPrecursorTypes = precursorType != null ? new String[]{precursorType} : null;
    }

    public String[] getQueryPrecursorTypes() {
        return queryPrecursorTypes;
    }

    public void setQueryPrecursorTypes(String[] queryPrecursorTypes) {
        this.queryPrecursorTypes = queryPrecursorTypes;
    }

    public Double getQueryMass() {
        return queryMass;
    }

    public Double getQueryRetTime() {
        return queryRetTime;
    }

    public void setQueryRetTime(Double queryRetTime) {
        this.queryRetTime = queryRetTime;
    }

    public String getChromatographyTypeLabel() {
        return chromatographyTypeLabel;
    }

    public void setChromatographyTypeLabel(String chromatographyTypeLabel) {
        this.chromatographyTypeLabel = chromatographyTypeLabel;
    }

    public String getChromatographyTypePath() {
        return chromatographyTypePath;
    }

    public void setChromatographyTypePath(String chromatographyTypePath) {
        this.chromatographyTypePath = chromatographyTypePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRetTime() {
        return retTime;
    }

    public void setRetTime(double retTime) {
        this.retTime = retTime;
    }

    public double getPrecursorError() {
        return precursorError;
    }

    public void setPrecursorError(double precursorError) {
        this.precursorError = precursorError;
    }

    public double getPrecursorErrorPPM() {
        return precursorErrorPPM;
    }

    public void setPrecursorErrorPPM(double precursorErrorPPM) {
        this.precursorErrorPPM = precursorErrorPPM;
    }

    public double getMassError() {
        return massError;
    }

    public void setMassError(double massError) {
        this.massError = massError;
    }

    public double getMassErrorPPM() {
        return massErrorPPM;
    }

    public void setMassErrorPPM(double massErrorPPM) {
        this.massErrorPPM = massErrorPPM;
    }

    public double getRetTimeError() {
        return retTimeError;
    }

    public void setRetTimeError(double retTimeError) {
        this.retTimeError = retTimeError;
    }

    public double getRetIndexError() {
        return retIndexError;
    }

    public void setRetIndexError(double retIndexError) {
        this.retIndexError = retIndexError;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double[] getQueryPeakMzs() {
        return queryPeakMzs;
    }

    public void setQueryPeakMzs(double[] queryPeakMzs) {
        this.queryPeakMzs = queryPeakMzs;
    }

    public double[] getLibraryPeakMzs() {
        return libraryPeakMzs;
    }

    public void setLibraryPeakMzs(double[] libraryPeakMzs) {
        this.libraryPeakMzs = libraryPeakMzs;
    }

    public String getHRef() {
        return matchType!= null ? String.format("%s/%d/",
                matchType.name().toLowerCase(),
                (matchType == MatchType.CLUSTER) ? this.getClusterId() : this.getSpectrumId()): null;
    }

    public String getQueryHRef() {
        if (querySpectrumId != 0 && querySpectrumId > 0)
            return String.format("/spectrum/%d/", querySpectrumId);
        else
            return String.format("/file/%d/%d/", queryFileIndex, querySpectrumIndex);
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SearchResultDTO)) {
            return false;
        }
        return spectrumId == ((SearchResultDTO) other).spectrumId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(spectrumId);
    }

    @Override
    public String toString() {
        return "Search Result ID = " + getSpectrumId();
    }

    @Override
    public SearchResultDTO clone() {
        return SerializationUtils.clone(this);
    }

    /**
     * Returns 1 if this is better than the other, -1 if this is worse than the other, and 0 otherwise
     * @param other instance of SearchResultDTO
     * @return 1 if this is better than the other, -1 if this is worse than the other, and 0 otherwise
     */
    @Override
    public int compareTo(SearchResultDTO other) {
        int scoreComparison = compareDoubleOrNull(this.score, other.score, 1);
        int massComparison = compareDoubleOrNull(this.massError, other.massError, -1);
        int retTimeComparison = compareDoubleOrNull(this.retTimeError, other.retTimeError, -1);
        int ontologyComparison = compareDoubleOrNull(this.ontologyPriority, other.ontologyPriority, -1);

        if (scoreComparison == 0 && massComparison == 0 && retTimeComparison == 0 && ontologyComparison == 0)
            return 0;
        if (scoreComparison >= 0 && massComparison >= 0 && retTimeComparison >= 0 && ontologyComparison >= 0)
            return 1;
        else if (scoreComparison <= 0 && massComparison <= 0 && retTimeComparison <= 0 && ontologyComparison <= 0)
            return -1;
        else
            return 0;
    }

    /**
     * Returns 1 if x is "higher" then y, -1 if y is "higher" then x, and 0 otherwise
     * @param x number
     * @param y number
     * @return 1 if x is "higher" then y, -1 if y is "higher" then x, and 0 otherwise
     */
    private static int compareDoubleOrNull(Number x, Number y, int factor) {
        if (x == null && y == null)
            return 0;
        else if (x != null && y == null)
            return 1;
        else if (x == null && y != null)
            return -1;
        else
            return factor * Double.compare(x.doubleValue(), y.doubleValue());
    }
}
