package org.dulab.site.services;

import org.dulab.site.models.Spectrum;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileReaderService {
    List<Spectrum> read(InputStream inputStream) throws IOException;
}
