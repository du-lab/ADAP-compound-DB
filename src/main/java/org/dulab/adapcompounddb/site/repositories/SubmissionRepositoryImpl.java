package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.*;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
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
                .createQuery("select s from Spectrum s where s.file.id in (:fileIds)", Spectrum.class)
                .setParameter("fileIds", files.stream().map(File::getId).collect(Collectors.toList()))
                .getResultList();

        List<Peak> peaks = entityManager
                .createQuery("select p from Peak p where p.spectrum.id in (:spectrumIds)", Peak.class)
                .setParameter("spectrumIds", spectra.stream().map(Spectrum::getId).collect(Collectors.toList()))
                .getResultList();

        List<Isotope> isotopes = entityManager
                .createQuery("select i from Isotope i where i.spectrum.id in (:spectrumIds)", Isotope.class)
                .setParameter("spectrumIds", spectra.stream().map(Spectrum::getId).collect(Collectors.toList()))
                .getResultList();

        assignChildrenToParents(peaks, Peak::getSpectrum, spectra, Spectrum::setPeaks);
        assignChildrenToParents(isotopes, Isotope::getSpectrum, spectra, Spectrum::setIsotopes);
        assignChildrenToParents(spectra, Spectrum::getFile, files, File::setSpectra);
        assignChildrenToParents(files, File::getSubmission, Collections.singletonList(submission), Submission::setFiles);

        return submission;
    }

    private <C, P> void assignChildrenToParents(List<C> children, Function<C, P> parentGetter,
                                                List<P> parents, BiConsumer<P, List<C>> childrenSetter) {

        Map<P, List<C>> parentToChildrenMap = children.stream()
                .collect(Collectors.groupingBy(parentGetter));

        for (P parent : parents) {
            childrenSetter.accept(parent, parentToChildrenMap.get(parent));
        }
    }
}
