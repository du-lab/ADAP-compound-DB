package org.dulab.adapcompounddb.models;

public class FileIndexAndSpectrumIndexBestMatchPair {
    private int fileIndex;
    private SpectrumIndexAndBestMatchPair spectrumIndexAndBestMatchPair;

    public FileIndexAndSpectrumIndexBestMatchPair() {
        fileIndex = 0;
        spectrumIndexAndBestMatchPair = new SpectrumIndexAndBestMatchPair();
    }

    public FileIndexAndSpectrumIndexBestMatchPair(int fileIndex, SpectrumIndexAndBestMatchPair spectrumIndexAndBestMatchPair) {
        this.fileIndex = fileIndex;
        this.spectrumIndexAndBestMatchPair = spectrumIndexAndBestMatchPair;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public SpectrumIndexAndBestMatchPair getSpectrumIndexAndBestMatchPair() {
        return spectrumIndexAndBestMatchPair;
    }

    public void setSpectrumIndexAndBestMatchPair(SpectrumIndexAndBestMatchPair spectrumIndexAndBestMatchPair) {
        this.spectrumIndexAndBestMatchPair = spectrumIndexAndBestMatchPair;
    }

}
