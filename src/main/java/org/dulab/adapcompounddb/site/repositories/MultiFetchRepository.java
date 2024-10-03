package org.dulab.adapcompounddb.site.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.entities.*;
import org.hibernate.annotations.QueryHints;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiFetchRepository.class);

    // Add Extended to speed up queries
    @PersistenceContext()  // type = PersistenceContextType.EXTENDED
            EntityManager entityManager;

    public void resetEntityManager() {
//        try {
//            entityManager.clear();
//            LOGGER.info("Cleared entity manager");
//        } catch (Exception e) {
//            LOGGER.warn("Cannot clear entity manager");
//        }
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

    public Submission getSubmissionWithFilesSpectra(long submissionId) {
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

        assignChildrenToParents(spectra, Spectrum::getFile, files, File::setSpectra, File::getId);
        assignChildrenToParents(files, File::getSubmission, Collections.singletonList(submission), Submission::setFiles,
                Submission::getId);

        return submission;
    }

//    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Spectrum> getSpectraWithPeaksIsotopes(Set<Long> spectrumIds) {

        if (spectrumIds.stream().anyMatch(Objects::isNull))
            throw new IllegalStateException("Some of spectrum IDs are null: " + spectrumIds);

//        long time = System.currentTimeMillis();
        List<Spectrum> spectra = entityManager
                .createQuery("select s from Spectrum s where s.id in (:spectrumIds)", Spectrum.class)
                .setParameter("spectrumIds", spectrumIds)
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();
//        LOGGER.info("Spectra query time: " + (System.currentTimeMillis() - time) + " ms");

//        time = System.currentTimeMillis();
        List<Peak> peaks = entityManager
                .createQuery("select p from Peak p where p.spectrum.id in (:spectrumIds)", Peak.class)
                .setParameter("spectrumIds", spectrumIds)
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();
//        LOGGER.info("Peaks query time: " + (System.currentTimeMillis() - time) + " ms");

//        time = System.currentTimeMillis();
        List<Isotope> isotopes = entityManager
                .createQuery("select i from Isotope i where i.spectrum.id in (:spectrumIds)", Isotope.class)
                .setParameter("spectrumIds", spectrumIds)
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();
//        LOGGER.info("Isotopes query time: " + (System.currentTimeMillis() - time) + " ms");

//        time = System.currentTimeMillis();
        List<Identifier> identifiers = entityManager
                .createQuery("select i from Identifier i where i.spectrum.id in (:spectrumIds)", Identifier.class)
                .setParameter("spectrumIds", spectrumIds)
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();
//        LOGGER.info("Identifiers query time: " + (System.currentTimeMillis() - time) + " ms");

        assignChildrenToParents(peaks, Peak::getSpectrum, spectra, Spectrum::setPeaks, Spectrum::getId);
        assignChildrenToParents(isotopes, Isotope::getSpectrum, spectra, Spectrum::setIsotopes, Spectrum::getId);
        assignChildrenToParents(identifiers, Identifier::getSpectrum, spectra, Spectrum::setIdentifiers, Spectrum::getId);  // identifiers

        return spectra;
    }

    public List<SpectrumMatch> getMatchesWithQueryAndMatchSpectraAndIdentifications(long userId, long submissionId) {

        List<Spectrum> querySpectra = entityManager
                .createQuery("select s from Spectrum s where s.file.submission.id = :submissionId", Spectrum.class)
                .setParameter("submissionId", submissionId)
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();

        List<SpectrumMatch> matches = entityManager
                .createQuery("select sm from SpectrumMatch sm where sm.userPrincipalId = :userId and sm.querySpectrum.id in (:spectrumIds)", SpectrumMatch.class)
                .setParameter("userId", userId)
                .setParameter("spectrumIds", querySpectra.stream().map(Spectrum::getId).collect(Collectors.toList()))
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();

        List<Spectrum> matchSpectra = entityManager
                .createQuery("select s from Spectrum s where s.id in (:spectrumIds)", Spectrum.class)
                .setParameter("spectrumIds", matches.stream()
                        .map(SpectrumMatch::getMatchSpectrum).filter(Objects::nonNull)
                        .map(Spectrum::getId).collect(Collectors.toList()))
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();

        List<Identifier> matchIdentifiers = entityManager
                .createQuery("select i from Identifier i where i.spectrum.id in (:spectrumIds)", Identifier.class)
                .setParameter("spectrumIds", matchSpectra.stream().map(Spectrum::getId).collect(Collectors.toList()))
                .setHint(QueryHints.READ_ONLY, true)
                .getResultList();

        assignChildrenToParents(matches, SpectrumMatch::getQuerySpectrum, querySpectra, Spectrum::setMatches, Spectrum::getId);
        assignChildrenToParents(matches, SpectrumMatch::getMatchSpectrum, matchSpectra, Spectrum::setMatches, Spectrum::getId);
        assignChildrenToParents(matchIdentifiers, Identifier::getSpectrum, matchSpectra, Spectrum::setIdentifiers, Spectrum::getId);

        expandMatches(matches, querySpectra);

        return matches;
    }

    /**
     * Adds a SpectrumMatch with a null match spectrum for each query spectrum that does not have a match.
     * @param matches Spectrum matches
     * @param querySpectra Query spectra
     */
    private void expandMatches(List<SpectrumMatch> matches, List<Spectrum> querySpectra) {
        Set<Long> matchedQueryIds = matches.stream()
                .map(SpectrumMatch::getQuerySpectrum)
                .map(Spectrum::getId)
                .collect(Collectors.toSet());

        for (Spectrum querySpectrum : querySpectra) {
            if (!matchedQueryIds.contains(querySpectrum.getId())) {
                SpectrumMatch match = new SpectrumMatch();
                match.setQuerySpectrum(querySpectrum);
                matches.add(match);
            }
        }

        matches.sort(Comparator.comparingLong(m -> m.getQuerySpectrum().getId()));
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
