package org.dulab.services;

import org.dulab.models.Spectrum;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileReaderService {
    List<Spectrum> read(InputStream inputStream) throws IOException;
}
