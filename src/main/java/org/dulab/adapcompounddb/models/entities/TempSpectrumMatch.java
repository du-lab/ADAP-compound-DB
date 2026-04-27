package org.dulab.adapcompounddb.models.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(indexes = {
        @Index(name = "idx_temp_session", columnList = "sessionId"),
        @Index(name = "idx_temp_session_file_spectrum", columnList = "sessionId, fileIndex, spectrumIndex")
})
public class TempSpectrumMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 128)
    private String sessionId;

    @Column(nullable = false)
    private int fileIndex;

    @Column(nullable = false)
    private int spectrumIndex;

    @Column(length = 512)
    private String querySpectrumName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MatchSpectrumId", referencedColumnName = "Id")
    private Spectrum matchSpectrum;

    private Double score;
    private Double isotopicSimilarity;
    private Double precursorError;
    private Double precursorErrorPPM;
    private Double massError;
    private Double massErrorPPM;
    private Double retTimeError;
    private Double retIndexError;

    @Column(length = 45)
    private String ontologyLevel;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] queryPeakMzs;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] libraryPeakMzs;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getFileIndex() { return fileIndex; }
    public void setFileIndex(int fileIndex) { this.fileIndex = fileIndex; }

    public int getSpectrumIndex() { return spectrumIndex; }
    public void setSpectrumIndex(int spectrumIndex) { this.spectrumIndex = spectrumIndex; }

    public String getQuerySpectrumName() { return querySpectrumName; }
    public void setQuerySpectrumName(String querySpectrumName) { this.querySpectrumName = querySpectrumName; }

    public Spectrum getMatchSpectrum() { return matchSpectrum; }
    public void setMatchSpectrum(Spectrum matchSpectrum) { this.matchSpectrum = matchSpectrum; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getIsotopicSimilarity() { return isotopicSimilarity; }
    public void setIsotopicSimilarity(Double isotopicSimilarity) { this.isotopicSimilarity = isotopicSimilarity; }

    public Double getPrecursorError() { return precursorError; }
    public void setPrecursorError(Double precursorError) { this.precursorError = precursorError; }

    public Double getPrecursorErrorPPM() { return precursorErrorPPM; }
    public void setPrecursorErrorPPM(Double precursorErrorPPM) { this.precursorErrorPPM = precursorErrorPPM; }

    public Double getMassError() { return massError; }
    public void setMassError(Double massError) { this.massError = massError; }

    public Double getMassErrorPPM() { return massErrorPPM; }
    public void setMassErrorPPM(Double massErrorPPM) { this.massErrorPPM = massErrorPPM; }

    public Double getRetTimeError() { return retTimeError; }
    public void setRetTimeError(Double retTimeError) { this.retTimeError = retTimeError; }

    public Double getRetIndexError() { return retIndexError; }
    public void setRetIndexError(Double retIndexError) { this.retIndexError = retIndexError; }

    public String getOntologyLevel() { return ontologyLevel; }
    public void setOntologyLevel(String ontologyLevel) { this.ontologyLevel = ontologyLevel; }

    public byte[] getQueryPeakMzs() { return queryPeakMzs; }
    public void setQueryPeakMzs(byte[] queryPeakMzs) { this.queryPeakMzs = queryPeakMzs; }

    public byte[] getLibraryPeakMzs() { return libraryPeakMzs; }
    public void setLibraryPeakMzs(byte[] libraryPeakMzs) { this.libraryPeakMzs = libraryPeakMzs; }
}
