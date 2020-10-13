package org.dulab.adapcompounddb.site.controllers.utils;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.FileType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.dulab.adapcompounddb.site.services.FileReaderService;
import org.dulab.adapcompounddb.site.services.MspFileReaderService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipartFileUtils {

    private static final Map<FileType, FileReaderService> fileReaderServiceMap = new HashMap<>();

    static {
        fileReaderServiceMap.put(FileType.MSP, new MspFileReaderService());
    }


    /**
     * Reads MSP files and adds them to submission
     * @param submission submission
     * @param multipartFiles files
     * @param fileType file type
     * @param chromatographyType chromatography type
     */
    public static void readMultipartFile(Submission submission, List<MultipartFile> multipartFiles,
                                               FileType fileType, ChromatographyType chromatographyType) {

        final FileReaderService service = fileReaderServiceMap.get(fileType);
        if (service == null)
            throw new IllegalStateException(
                    "Cannot find an implementation of FileReaderService for a file of type " + fileType);

        final List<File> files = new ArrayList<>(multipartFiles.size());

        // create two integer totalNumberOfFiles and totalNumberOfIntFiles to calculate MassSpectrometryType,
        // totalNumberOfFiles is the number of files a study contained,
        // totalNumberOfIntFiles is the number of files that contain Intergered M/Z value.
        int totalNumberOfFiles = multipartFiles.size();
        int totalNumberOfIntFiles = 0;

        for (final MultipartFile multipartFile : multipartFiles) {
            final File file = new File();
            file.setName(multipartFile.getOriginalFilename());
            file.setFileType(fileType);
            file.setSubmission(submission);
            try {
                file.setContent(multipartFile.getBytes());
                file.setSpectra(service.read(multipartFile.getInputStream(), chromatographyType));
                file.getSpectra().forEach(s -> s.setFile(file));

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

        submission.setFiles(files);
    }
}
