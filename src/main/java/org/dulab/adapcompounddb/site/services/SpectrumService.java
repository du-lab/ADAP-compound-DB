package org.dulab.adapcompounddb.site.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.dto.SpectrumProperty;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.site.services.utils.DataUtils;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectrumService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumService.class);

    private final SpectrumRepository spectrumRepository;

    private final SubmissionRepository submissionRepository;



  private enum ColumnInformation {
        NAME(1, "name"), RETENTIONTIME(2, "retentionTime"),
        PRECURSOR(3, "precursor"), SIGNIFICANCE(4, "significance"),
        MASS(5, "mass"),
        CHROMATOGRAPHYTYPE(6, "chromatographyType");

        private final int position;
        private final String sortColumnName;

        ColumnInformation(final int position, final String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        public static String getColumnNameFromPosition(final int position) {
            String columnName = null;
            for (final ColumnInformation columnInformation : ColumnInformation.values()) {
                if (position == columnInformation.getPosition()) {
                    columnName = columnInformation.getSortColumnName();
                }
            }
            return columnName;
        }
    }

    @Autowired
    public SpectrumService(SpectrumRepository spectrumRepository,
                           SubmissionRepository submissionRepository) {

        this.spectrumRepository = spectrumRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public Spectrum find(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(() -> new EmptySearchResultException((id)));
    }

    @Transactional
    public DataTableResponse findSpectrumBySubmissionId(long submissionId, String search,
                                                        int start, int length, String column, String direction) {
        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
//        Pageable pageable;
//
////        final String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
//        if (sortColumn != null) {
//            final Sort sort = new Sort(Sort.Direction.fromString(orderDirection), sortColumn);
//            pageable = PageRequest.of(start / length, length, sort);
//        } else {
//            pageable = PageRequest.of(start / length, length);
//        }

        Pageable pageable = DataUtils.createPageable(start, length, column, direction);
        final Page<Spectrum> spectrumPage = spectrumRepository.findSpectrumBySubmissionId(submissionId, search,
                pageable);

        final List<SpectrumDTO> spectrumList = objectMapper.map(spectrumPage.getContent(), SpectrumDTO.class);
        final DataTableResponse response = new DataTableResponse(spectrumList);
        response.setRecordsTotal(spectrumPage.getTotalElements());
        response.setRecordsFiltered(spectrumPage.getTotalElements());

        return response;
    }

    public long countConsensusSpectra() {
        return spectrumRepository.countByConsensusTrue();
    }

    public long countReferenceSpectra() {
        return spectrumRepository.countByReferenceTrue();
    }

    @Transactional
    public void updateReferenceBySubmissionId(long submissionId, boolean reference) {
        spectrumRepository.updateReferenceBySubmissionId(submissionId, reference);
    }

    @Transactional
    public void updateClusterableBySubmissionId(long submissionId, boolean clusterable) {
        spectrumRepository.updateClusterableBySubmissionId(submissionId, clusterable);
       submissionRepository.updateClusterableBySubmissinoid(submissionId, clusterable);
    }

    public List<SpectrumProperty> findSpectrumPropertiesBySpectrumId(long... spectrumIds) {
        return new ArrayList<>();
    }

    public List<Spectrum> findConsensusSpectraByChromatographyType(
            ChromatographyType chromatographyType, MassSpectrometryType massSpectrometryType) {
        boolean integerMz = massSpectrometryType == MassSpectrometryType.LOW_RESOLUTION;
        Iterable<Spectrum> spectra = spectrumRepository
                .findAllByConsensusTrueAndChromatographyTypeAndIntegerMz(chromatographyType, integerMz);
        return MappingUtils.toList(spectra);
    }
    public List<Spectrum> getMatchesByUserAndSpectrumName(long id, String spectrumName) {
      return spectrumRepository.getMatchesByUserAndSpectrumName(id, spectrumName);
    }

}
