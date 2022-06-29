package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubmissionRepositoryImpl implements SubmissionRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    public Submission getSubmissionWithFilesSpectraPeaks(long submissionId) {
        Submission submission = entityManager
                .createQuery("select s from Submission s where s.id = :submissionId", Submission.class)
                .setParameter("submissionId", submissionId)
                .getSingleResult();

        List<File> files = entityManager
                .createQuery("select f from File f where f.submission.id = :submissionId", File.class)
                .setParameter("submissionId", submissionId)
                .getResultList();

        List<Spectrum> spectra = entityManager
                .createQuery("select s from Spectrum s where s.file.submission.id = :submissionId", Spectrum.class)
                .setParameter("submissionId", submissionId)
                .getResultList();

        List<Peak> peaks = entityManager
                .createQuery("select p from Peak p where p.spectrum.file.submission.id = :submissionId", Peak.class)
                .setParameter("submissionId", submissionId)
                .getResultList();

        Map<Spectrum, List<Peak>> spectrumToPeakListMap = peaks.stream()
                .collect(Collectors.groupingBy(Peak::getSpectrum));
        spectrumToPeakListMap.forEach(Spectrum::setPeaks);

        Map<File, List<Spectrum>> fileToSpectrumListMap = spectrumToPeakListMap.keySet().stream()
                .collect(Collectors.groupingBy(Spectrum::getFile));
        fileToSpectrumListMap.forEach(File::setSpectra);

        Map<Submission, List<File>> submissionToFileListMap = fileToSpectrumListMap.keySet().stream()
                .collect(Collectors.groupingBy(File::getSubmission));
        submissionToFileListMap.forEach(Submission::setFiles);

        return submission;
    }
}
