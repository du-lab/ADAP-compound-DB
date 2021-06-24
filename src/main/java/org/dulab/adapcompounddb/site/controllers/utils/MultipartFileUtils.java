package org.dulab.adapcompounddb.site.controllers.utils;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.dulab.adapcompounddb.site.services.io.CsvFileReaderService;
import org.dulab.adapcompounddb.site.services.io.FileReaderService;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.site.services.io.MspFileReaderService;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MultipartFileUtils {

    private static final Map<FileType, FileReaderService> fileReaderServiceMap = new HashMap<>();

    static {
        fileReaderServiceMap.put(FileType.MSP, new MspFileReaderService());
        fileReaderServiceMap.put(FileType.CSV, new CsvFileReaderService());
    }


    /**
     * Reads MSP files and adds them to submission
     *
     * @param submission         submission
     * @param multipartFiles     files
     * @param chromatographyType chromatography type
     */
    public static void readMultipartFile(Submission submission, List<MultipartFile> multipartFiles,
                                         ChromatographyType chromatographyType,
                                         @Nullable Map<FileType, MetaDataMapping> metaDataMappings,
                                         boolean mergeFiles) {

//        final FileReaderService service = fileReaderServiceMap.get(fileType);
//        if (service == null)
//            throw new IllegalStateException(
//                    "Cannot find an implementation of FileReaderService for a file of type " + fileType);

        List<File> files = new ArrayList<>(multipartFiles.size());

        // create two integer totalNumberOfFiles and totalNumberOfIntFiles to calculate MassSpectrometryType,
        // totalNumberOfFiles is the number of files a study contained,
        // totalNumberOfIntFiles is the number of files that contain Intergered M/Z value.
        int totalNumberOfFiles = multipartFiles.size();
        int totalNumberOfIntFiles = 0;

        for (MultipartFile multipartFile : multipartFiles) {

            String filename = Objects.requireNonNull(multipartFile.getOriginalFilename());
            FileType fileType = getFileType(filename);
            FileReaderService fileReader = fileReaderServiceMap.get(fileType);
            if (fileReader == null)
                throw new IllegalStateException(
                        "Cannot find an implementation of FileReaderService for a file of type " + fileType);

            File file = new File();
            file.setName(filename);
            file.setFileType(fileType);
            file.setSubmission(submission);
            try {
                file.setContent(multipartFile.getBytes());
                file.setSpectra(fileReader.read(
                        multipartFile.getInputStream(),
                        metaDataMappings != null ? metaDataMappings.get(fileType) : null));
                file.getSpectra().forEach(spectrum -> {
                    spectrum.setFile(file);
                    spectrum.setChromatographyType(chromatographyType);
                });

                // check if the file exists spectrum has integral m/z value
                for (Spectrum s : file.getSpectra()) {
                    if (s.isIntegerMz()) {
                        totalNumberOfIntFiles++;
                        break;
                    }
                }
                files.add(file);

            } catch (final IOException e) {
                throw new IllegalStateException("Cannot read this file: " + e.getMessage(), e);
            }
        }

        // if totalNumberOfFiles == totalNumberOfIntFiles, it means every file of the study contains at least
        // one spectrum that isIntegerMz is true, then set massSpectrometryType is LOW_Resolution
        // else set massSpectrometryType is High_Resolution
        if (totalNumberOfFiles == totalNumberOfIntFiles) {
            submission.setMassSpectrometryType(MassSpectrometryType.LOW_RESOLUTION);
        } else {
            submission.setMassSpectrometryType(MassSpectrometryType.HIGH_RESOLUTION);
        }

        if (files.isEmpty())
            throw new IllegalStateException("Cannot read this file");

        if (mergeFiles)
            files = mergeFiles(files);

        submission.setFiles(files);
    }

    private static FileType getFileType(String filename) {
        String[] substrings = filename.split("\\.");
        if (substrings.length == 0)
            throw new IllegalArgumentException("Cannot determine the file type: " + filename);

        String extension = substrings[substrings.length - 1];
        return FileType.valueOf(extension.toUpperCase());
    }

    private static List<File> mergeFiles(List<File> files) {
        if (files == null || files.size() <= 1)
            return files;

        File mergedFile = files.get(0);
        List<Spectrum> mergedSpectra = mergedFile.getSpectra();
        if (!checkNames(mergedSpectra))
            throw new IllegalStateException("No ID found for the spectra in file " + mergedFile.getName());

        for (int i = 1; i < files.size(); ++i) {
            File file = files.get(i);
            List<Spectrum> spectra = file.getSpectra();
            if (!checkNames(spectra))
                throw new IllegalStateException("No ID found for the spectra in file " + mergedFile.getName());

            mergedSpectra = mergeSpectra(mergedSpectra, spectra);
            file.setSpectra(null);
        }

        mergedSpectra.forEach(s -> s.setFile(mergedFile));
        mergedFile.setSpectra(mergedSpectra);

        return files;
    }

    private static List<Spectrum> mergeSpectra(List<Spectrum> spectra1, List<Spectrum> spectra2) {

        Map<String, List<Spectrum>> nameToSpectraMap = new HashMap<>();

        // Add all spectra from `spectra1` to the map
        spectra1.forEach(s -> nameToSpectraMap
                .computeIfAbsent(s.getShortName(), k -> new ArrayList<>())
                .add(s));

        // Add all spectra from `spectra2` to the map
        spectra2.forEach(s -> nameToSpectraMap
                .computeIfAbsent(s.getShortName(), k -> new ArrayList<>())
                .add(s));

        // Merge spectra with the same external ID
        for (Map.Entry<String, List<Spectrum>> entry : nameToSpectraMap.entrySet()) {

            List<Spectrum> spectrumList = entry.getValue();
            if (spectrumList.isEmpty()) continue;

            Map<File, List<Spectrum>> fileToSpectraMap = new HashMap<>();
            spectrumList.forEach(s -> fileToSpectraMap
                    .computeIfAbsent(s.getFile(), k -> new ArrayList<>())
                    .add(s));

            // If all spectra in `spectrumList` are from the same file, don't merge anything
            if (fileToSpectraMap.size() <= 1) continue;

            // If `spectrumList` contains spectra from multiple files, we need to merge them and produce MxN spectra
            List<Spectrum> mergedSpectra = null;
            for (List<Spectrum> spectra : fileToSpectraMap.values()) {
                if (mergedSpectra == null) {
                    mergedSpectra = spectra;
                } else {
                    List<Spectrum> newMergedSpectra = new ArrayList<>(mergedSpectra.size() * spectra.size());
                    for (Spectrum mergedSpectrum : mergedSpectra) {
                        for (Spectrum spectrum : spectra) {
                            newMergedSpectra.add(Spectrum.merge(mergedSpectrum, spectrum));
                        }
                    }
                    mergedSpectra = newMergedSpectra;
                }
            }

            entry.setValue(mergedSpectra);
        }

        return nameToSpectraMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

//        List<Spectrum> unmergedSpectra = new ArrayList<>();
//        for (Spectrum spectrum2 : spectra2) {
//            boolean merged = false;
//            for (Spectrum spectrum1 : spectra1) {
//                if (spectrum1.getExternalId().equals(spectrum2.getExternalId())) {
//                    spectrum1.merge(spectrum2);
//                    merged = true;
//                }
//            }
//            if (!merged)
//                unmergedSpectra.add(spectrum2);
//        }
//        spectra1.addAll(unmergedSpectra);
    }

    private static boolean checkNames(List<Spectrum> spectra) {
        for (Spectrum spectrum : spectra)
            if (spectrum.getShortName() == null || spectrum.getShortName().isEmpty())
                return false;
        return true;
    }
}
