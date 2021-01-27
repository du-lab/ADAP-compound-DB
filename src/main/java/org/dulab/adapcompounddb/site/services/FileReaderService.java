package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Validated
public interface FileReaderService {

    @NotNull(message = "FileReader is required to return a list of mass spectra.")
    List<Spectrum> read(InputStream inputStream, ChromatographyType type) throws IOException;
}
