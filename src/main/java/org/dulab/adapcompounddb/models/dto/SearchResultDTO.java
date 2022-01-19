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
    private Long querySpectrumId;
    private Integer querySpectrumIndex;
    private Integer queryFileIndex;
    private String querySpectrumName;
    private String querySpectrumShortName;
    private String queryExternalId;
    private boolean queryWithPeaks;
    private double[] queryPrecursorMzs;
    private String[] queryPrecursorTypes;
    private Double queryMass;
    private Double queryRetTime;

    // Match
    private MatchType matchType;
    private Integer matchIndex;
    private long spectrumId;
    private Long clusterId;
    private String externalId;
    private String name;
    private String precursorType;
    private Integer size;
    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;
    private Double minPValue;
    private Double diseasePValue;
    private Double speciesPValue;
    private Double sampleSourcePValue;
    private String ontologyLevel;
    private Integer ontologyPriority;
    private String chromatographyTypeLabel;
    private String chromatographyTypePath;
    private String json;
    private Double mass;
    private Double retTime;
    private String formula;
    private String casId;
    private String hmdbId;
    private String pubChemId;
    private String inChIKey;
    private String submissionName;
    private Boolean inHouse;
    private Long submissionId;

    // Other
    private int position;
    private Double score;
    private Double isotopicSimilarity;
    private Double precursorError;
    private Double precursorErrorPPM;
    private Double massError;
    private Double massErrorPPM;
    private Double retTimeError;
    private Double retIndexError;
    private boolean marked;


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
            this.queryMass = querySpectrum.getMass();
            this.queryRetTime = querySpectrum.getRetentionTime();
            this.chromatographyTypeLabel = querySpectrum.getChromatographyType().getLabel();
            this.chromatographyTypePath = querySpectrum.getChromatographyType().getIconPath();
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
            this.mass = matchSpectrum.getMass();
            this.retTime = matchSpectrum.getRetentionTime();
            this.formula = matchSpectrum.getFormula();
            this.inHouse = matchSpectrum.isInHouseReference();
            this.inChIKey = matchSpectrum.getInChiKey();

            Map<IdentifierType, String> identifiers = matchSpectrum.getIdentifiers();
            if (identifiers != null) {
                this.casId = identifiers.get(IdentifierType.CAS);
                this.hmdbId = identifiers.get(IdentifierType.HMDB);
                this.pubChemId = identifiers.get(IdentifierType.PUBCHEM);
            }

            this.score = spectrumMatch.getScore();
            this.isotopicSimilarity = spectrumMatch.getIsotopicSimilarity();
            this.precursorError = spectrumMatch.getPrecursorError();
            this.precursorErrorPPM = spectrumMatch.getPrecursorErrorPPM();
            this.precursorType = spectrumMatch.getPrecursorType();
            this.massError = spectrumMatch.getMassError();
            this.massErrorPPM = spectrumMatch.getMassErrorPPM();
            this.retTimeError = spectrumMatch.getRetTimeError();
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

    public Integer getMatchIndex() {
        return matchIndex;
    }

    public void setMatchIndex(Integer matchIndex) {
        this.matchIndex = matchIndex;
    }

    public long getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(final long spectrumId) {
        this.spectrumId = spectrumId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public String getPrecursorType() {
        return precursorType;
    }

    public void setPrecursorType(String precursorType) {
        this.precursorType = precursorType;
    }

    public Double getScore() {
        return score;
    }

    public Long getNISTScore() {
        return (score != null) ? Math.round(score * 1000) : null;
    }

    public void setScore(final Double score) {
        this.score = score;
    }

    public Double getIsotopicSimilarity() {
        return isotopicSimilarity;
    }

    public void setIsotopicSimilarity(Double isotopicSimilarity) {
        this.isotopicSimilarity = isotopicSimilarity;
    }

    public Double getAveSignificance() {
        return aveSignificance;
    }

    public void setAveSignificance(final Double aveSignificance) {
        this.aveSignificance = aveSignificance;
    }

    public Double getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(final Double minSignificance) {
        this.minSignificance = minSignificance;
    }

    public Double getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(final Double maxSignificance) {
        this.maxSignificance = maxSignificance;
    }

    public Double getMinPValue()
    {
        return minPValue;
    }

    public void setMinPValue(Double minPValue)
    {
        this.minPValue = minPValue;
    }

    public Double getDiseasePValue()
    {
        return diseasePValue;
    }

    public void setDiseasePValue(Double diseasePValue)
    {
        this.diseasePValue = diseasePValue;
    }

    public Double getSpeciesPValue()
    {
        return speciesPValue;
    }

    public void setSpeciesPValue(Double speciesPValue)
    {
        this.speciesPValue = speciesPValue;
    }

    public Double getSampleSourcePValue()
    {
        return sampleSourcePValue;
    }

    public void setSampleSourcePValue(Double sampleSourcePValue)
    {
        this.sampleSourcePValue = sampleSourcePValue;
    }

    public String getOntologyLevel() {
        return ontologyLevel;
    }

    public void setOntologyLevel(String ontologyLevel) {
        this.ontologyLevel = ontologyLevel;
    }

    public Integer getOntologyPriority() {
        return ontologyPriority;
    }

    public void setOntologyPriority(Integer ontologyPriority) {
        this.ontologyPriority = ontologyPriority;
    }

    public void setOntologyLevel(@Nullable OntologyLevel ontologyLevel) {
        if (ontologyLevel != null) {
            this.setOntologyLevel(ontologyLevel.getLabel());
            this.setOntologyPriority(ontologyLevel.getPriority());
        } else {
            this.setOntologyLevel((String) null);
            this.setOntologyPriority(null);
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

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Boolean getInHouse() {
        return inHouse;
    }

    public void setInHouse(Boolean inHouse) {
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

    public Long getQuerySpectrumId() {
        return querySpectrumId;
    }

    public void setQuerySpectrumId(long querySpectrumId) {
        this.querySpectrumId = querySpectrumId;
    }

    public Integer getQuerySpectrumIndex() {
        return querySpectrumIndex;
    }

    public void setQuerySpectrumIndex(Integer querySpectrumIndex) {
        this.querySpectrumIndex = querySpectrumIndex;
    }

    public Integer getQueryFileIndex() {
        return queryFileIndex;
    }

    public void setQueryFileIndex(Integer queryFileIndex) {
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

    public Double getQueryPrecursorMz() {
        if (queryPrecursorMzs != null && queryPrecursorMzs.length > 0)
            return queryPrecursorMzs[0];
        return null;
    }

    public void setQueryPrecursorMz(Double precursorMz) {
        this.queryPrecursorMzs = precursorMz != null ? new double[]{precursorMz} : null;
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

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public Double getRetTime() {
        return retTime;
    }

    public void setRetTime(Double retTime) {
        this.retTime = retTime;
    }

    public Double getPrecursorError() {
        return precursorError;
    }

    public void setPrecursorError(Double precursorError) {
        this.precursorError = precursorError;
    }

    public Double getPrecursorErrorPPM() {
        return precursorErrorPPM;
    }

    public void setPrecursorErrorPPM(Double precursorErrorPPM) {
        this.precursorErrorPPM = precursorErrorPPM;
    }

    public Double getMassError() {
        return massError;
    }

    public void setMassError(Double massError) {
        this.massError = massError;
    }

    public Double getMassErrorPPM() {
        return massErrorPPM;
    }

    public void setMassErrorPPM(Double massErrorPPM) {
        this.massErrorPPM = massErrorPPM;
    }

    public Double getRetTimeError() {
        return retTimeError;
    }

    public void setRetTimeError(Double retTimeError) {
        this.retTimeError = retTimeError;
    }

    public Double getRetIndexError() {
        return retIndexError;
    }

    public void setRetIndexError(Double retIndexError) {
        this.retIndexError = retIndexError;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getHRef() {
        return String.format("%s/%d/",
                matchType.name().toLowerCase(),
                (matchType == MatchType.CLUSTER) ? this.getClusterId() : this.getSpectrumId());
    }

    public String getQueryHRef() {
        if (querySpectrumId != null && querySpectrumId > 0)
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
