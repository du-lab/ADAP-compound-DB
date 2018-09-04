package org.dulab.adapcompounddb.site.services;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SpectrumServiceImpl implements SpectrumService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;

    private static enum ColumnInformation {
        NAME(1, "name"), RETENTIONTIME(2, "retentionTime"), PRECURSOR(3, "precursor"), SIGNIFICANCE(4, "significance"),
        CHROMATOGRAPHYTYPE(5, "chromatographyType");

        private int position;
        private String sortColumnName;

        private ColumnInformation(final int position, final String sortColumnName) {
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
    public SpectrumServiceImpl(final SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public Spectrum find(final long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id).orElseThrow(EmptySearchResultException::new);
    }

    @Override
    @Transactional
    public DataTableResponse findSpectrumBySubmissionId(final Long submissionId, final String searchStr,
            final Integer start, final Integer length, final Integer column, final String orderDirection) {
        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        Pageable pageable = null;

        final String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
        if (sortColumn != null) {
            final Sort sort = new Sort(Sort.Direction.fromString(orderDirection), sortColumn);
            pageable = PageRequest.of(start / length, length, sort);
        } else {
            pageable = PageRequest.of(start / length, length);
        }

        final Page<Spectrum> spectrumPage = spectrumRepository.findSpectrumBySubmissionId(submissionId, searchStr,
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
    public Boolean updateReferenceOfAllSpectraOfSubmission(final Long submissionId, final boolean value) {
        return spectrumRepository.updateReferenceOfAllSpectraOfSubmission(submissionId, value) == 1;
    }
}
