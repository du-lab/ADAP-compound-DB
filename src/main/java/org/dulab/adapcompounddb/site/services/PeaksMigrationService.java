package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Identifier;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dulab.adapcompounddb.site.services.utils.ByteArrayUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PeaksMigrationService implements CommandLineRunner {

    private final SpectrumRepository spectrumRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public PeaksMigrationService(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        migratePeaks();
        migrateIdentifiers();

    }

    private void migrateIdentifiers() {
        System.out.println("🚀 STARTING Identifier migration");

        ObjectMapper mapper = new ObjectMapper();
        List<Long> spectrumIds = spectrumRepository.findAllSpectrumIds();
        System.out.println("Found " + spectrumIds.size() + " spectra to process");

        int batchSize = 100;
        int processedSpectra = 0;
        long startTime = System.currentTimeMillis();

        List<Long> batch = new ArrayList<>(batchSize);

        for (Long id : spectrumIds) {
            batch.add(id);

            if (batch.size() >= batchSize) {
                processIdentifierBatch(batch, mapper);
                batch.clear();
                processedSpectra += batchSize;

                long endTimeInSec = (System.currentTimeMillis() - startTime)/1000;
                System.out.printf("⚡️ Processed %d spectra in %d seconds (%.2f spectra/sec)%n",
                        processedSpectra, endTimeInSec,
                        processedSpectra/(double) endTimeInSec);
            }
        }

        // Remaining batch
        if (!batch.isEmpty()) {
            processIdentifierBatch(batch, mapper);
            processedSpectra += batch.size();
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf(String.format("🎉 Identifier migration completed: %d spectra in %d sec (%.2f spectra/sec)",
                processedSpectra, totalTime / 1000, processedSpectra * 1000.0 / totalTime));
    }


    @Transactional
    public void processIdentifierBatch(List<Long> batchIds, ObjectMapper mapper) {
        List<Spectrum> spectra = entityManager
                .createQuery("SELECT s FROM Spectrum s WHERE s.id IN :ids AND s.identifiersJson is NULL", Spectrum.class)
                .setParameter("ids", batchIds)
                .getResultList();
        if(spectra.isEmpty())
            return;
        List<Identifier> allIdentifiers = entityManager
                .createQuery("SELECT i FROM Identifier i WHERE i.spectrum.id IN :ids", Identifier.class)
                .setParameter("ids", batchIds)
                .getResultList();

        Map<Long, List<Identifier>> grouped = allIdentifiers.stream()
                .collect(Collectors.groupingBy(i -> i.getSpectrum().getId()));

        for (Spectrum spectrum : spectra) {
            List<Identifier> ids = grouped.get(spectrum.getId());
            if (ids == null || ids.isEmpty()) {
                spectrum.setIdentifiersJson(null);
                continue;
            }

            Map<String, String> jsonMap = new LinkedHashMap<>();
            for (Identifier i : ids) {
                jsonMap.put(i.getType().toString(), i.getValue());
            }

            try {
                String json = mapper.writeValueAsString(jsonMap);
                spectrum.setIdentifiersJson(json);
            } catch (Exception e) {
                System.err.println("Failed to serialize identifiers for Spectrum " + spectrum.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        entityManager.flush();
        entityManager.clear();
    }

    private void migratePeaks() throws Exception {
        System.out.println("🚀 STARTING migration");
        List<Long> spectrumIds = spectrumRepository.findAllSpectrumIds();
        System.out.println("Found " + spectrumIds.size() + " spectra to process");

        int processedSpectra = 0;
        int batchSize = 100; // Smaller batch size for more frequent commits
        long startTime = System.currentTimeMillis();


        List<Long> batch = new ArrayList<>(batchSize);
        for (Long id : spectrumIds) {
            batch.add(id);
            // Process every 100 batches
            if (batch.size() >= batchSize) {
                processedSpectra += processBatch(batch);
                batch.clear();

                long endTimeInSec = (System.currentTimeMillis() - startTime)/1000;
                System.out.printf("⚡️ Processed %d spectra in %d seconds (%.2f spectra/sec)%n",
                        processedSpectra, endTimeInSec,
                        processedSpectra/(double) endTimeInSec);

                // Clear persistence context to prevent memory growth
                entityManager.flush();
                entityManager.clear();
            }
        }

        // Process remaining
        if (!batch.isEmpty()) {
            processedSpectra += processBatch(batch);
        }

        long totalTime = (System.currentTimeMillis() - startTime)/1000;
        System.out.printf("✅ Migration completed: %d spectra in %d seconds (%.2f spectra/sec)%n",
                processedSpectra, totalTime,
                (processedSpectra) / (double) totalTime);
    }


    public int processBatch(List<Long> batchIds) throws Exception {
        int spectraCount = 0;

        // Fetch all relevant spectra
        String query = "SELECT s FROM Spectrum s WHERE s.id IN :ids AND s.peakDataEncoded IS NULL";
        List<Spectrum> spectra = entityManager.createQuery(query, Spectrum.class)
                .setParameter("ids", batchIds)
                .getResultList();

        if (spectra.isEmpty()) return 0;

        // Fetch all peaks in one go
        List<Peak> allPeaks = spectrumRepository.findPeakListBySpectrumIds(batchIds);

        // Group peaks by spectrum ID
        Map<Long, List<Peak>> peaksBySpectrumId = allPeaks.stream()
                .collect(Collectors.groupingBy(p -> p.getSpectrum().getId()));

        for (Spectrum spectrum : spectra) {
            // Parse peaks
            List<Peak> peaks = peaksBySpectrumId.get(spectrum.getId());
            if (peaks == null || peaks.isEmpty()) continue;

            double[] mz = new double[peaks.size()];
            double[] intensity = new double[peaks.size()];
            for (int i = 0; i < peaks.size(); i++) {
                mz[i] = peaks.get(i).getMz();
                intensity[i] = peaks.get(i).getIntensity();
            }

            String encoded = ByteArrayUtils.compressDoubleArrays(mz, intensity);
            spectrum.setPeakDataEncoded(encoded);
            spectraCount++;
        }

        return spectraCount;
    }

}
