package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.*;
import org.springframework.stereotype.Repository;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MultiFetchRepository {

    // Add Extended to speed up queries
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    public void resetEntityManager() {
        entityManager.clear();
    }

    public Submission getSubmissionWithFilesSpectraPeaksIsotopes(long submissionId) {

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

        assignChildrenToParents(peaks, Peak::getSpectrum, spectra, Spectrum::setPeaks, Spectrum::getId);
        assignChildrenToParents(isotopes, Isotope::getSpectrum, spectra, Spectrum::setIsotopes, Spectrum::getId);
        assignChildrenToParents(spectra, Spectrum::getFile, files, File::setSpectra, File::getId);
        assignChildrenToParents(files, File::getSubmission, Collections.singletonList(submission), Submission::setFiles,
                Submission::getId);

        return submission;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Spectrum> getSpectraWithPeaksIsotopes(Set<Long> spectrumIds) {

        if (spectrumIds.stream().anyMatch(Objects::isNull))
            throw new IllegalStateException("Some if spectrum IDs are null: " + spectrumIds);

        List<Spectrum> spectra = entityManager
                .createQuery("select distinct s from Spectrum s left join fetch s.peaks where s.id in (:spectrumIds)", Spectrum.class)
                .setParameter("spectrumIds", spectrumIds)
                .getResultList();

//        List<Peak> peaks = entityManager
//                .createQuery("select p from Peak p where p.spectrum.id in (:spectrumIds)", Peak.class)
//                .setParameter("spectrumIds", spectrumIds)
//                .getResultList();

        List<Isotope> isotopes = entityManager
                .createQuery("select i from Isotope i where i.spectrum.id in (:spectrumIds)", Isotope.class)
                .setParameter("spectrumIds", spectrumIds)
                .getResultList();

//        assignChildrenToParents(peaks, Peak::getSpectrum, spectra, Spectrum::setPeaks, Spectrum::getId);
        assignChildrenToParents(isotopes, Isotope::getSpectrum, spectra, Spectrum::setIsotopes, Spectrum::getId);

        return spectra;
    }

    private <C, P> void assignChildrenToParents(List<C> children, Function<C, P> parentGetter,
                                                List<P> parents, BiConsumer<P, List<C>> childrenSetter,
                                                Function<P, Long> idGetter) {

        Map<Long, List<C>> parentToChildrenMap = new HashMap<>();
        for (C child : children) {
            P parent = parentGetter.apply(child);
            Long id = idGetter.apply(parent);
            parentToChildrenMap.computeIfAbsent(id, k -> new ArrayList<>())
                    .add(child);
        }

        for (P parent : parents) {
            childrenSetter.accept(parent, parentToChildrenMap.get(idGetter.apply(parent)));
        }
    }
}
