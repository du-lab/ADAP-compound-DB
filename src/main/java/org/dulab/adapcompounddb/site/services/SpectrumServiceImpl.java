package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.dulab.adapcompounddb.site.repositories.SpectrumPropertyRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.utils.DataUtils;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectrumServiceImpl implements SpectrumService {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumServiceImpl.class);

    private final SpectrumRepository spectrumRepository;
    private final SpectrumPropertyRepository spectrumPropertyRepository;


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
    public SpectrumServiceImpl(SpectrumRepository spectrumRepository,
                               SpectrumPropertyRepository spectrumPropertyRepository) {

        this.spectrumRepository = spectrumRepository;
        this.spectrumPropertyRepository = spectrumPropertyRepository;
    }

    @Override
    @Transactional
    public Spectrum find(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id).orElseThrow(EmptySearchResultException::new);
    }

    @Override
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

    @Override
    public long countConsensusSpectra() {
        return spectrumRepository.countByConsensusTrue();
    }

    @Override
    public long countReferenceSpectra() {
        return spectrumRepository.countByReferenceTrue();
    }

    @Override
    @Transactional
    public void updateReferenceBySubmissionId(long submissionId, boolean reference) {
        spectrumRepository.updateReferenceBySubmissionId(submissionId, reference);
    }

    @Override
    @Transactional
    public void updateClusterableBySubmissionId(long submissionId, boolean clusterable) {
        spectrumRepository.updateClusterableBySubmissionId(submissionId, clusterable);
    }

    @Override
    public List<SpectrumProperty> findSpectrumPropertiesBySpectrumId(long... spectrumIds) {
        return MappingUtils.toList(spectrumPropertyRepository.findBySpectrumId(spectrumIds));
    }
}
