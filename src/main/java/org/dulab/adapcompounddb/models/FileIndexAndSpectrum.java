package org.dulab.adapcompounddb.models;

import org.dulab.adapcompounddb.models.entities.Spectrum;

public class FileIndexAndSpectrum {
    private int fileIndex;
    private Spectrum spectrum;

    public FileIndexAndSpectrum() {
        fileIndex = 0;
        spectrum = new Spectrum();
    }

    public FileIndexAndSpectrum(int fileIndex, Spectrum spectrum) {
        this.fileIndex = fileIndex;
        this.spectrum = spectrum;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

}
