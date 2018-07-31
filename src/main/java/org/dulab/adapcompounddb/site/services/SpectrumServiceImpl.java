package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumTableResponse;
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

    public static enum ColumnInformation {
    	NAME(1, "name"),
    	RETENTIONTIME(2, "retentionTime"),
    	PRECURSOR(3, "precursor"),
    	CHROMATOGRAPHYTYPE(4, "chromatographyType"),
    	FILE(5, "file.name");

    	private int position;
		private String sortColumnName;

    	private ColumnInformation(int position, String sortColumnName) {
    		this.position = position;
    		this.sortColumnName = sortColumnName;
    	}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public String getSortColumnName() {
			return sortColumnName;
		}

		public void setSortColumnName(String sortColumnName) {
			this.sortColumnName = sortColumnName;
		}

		public static String getColumnNameFromPosition(int position) {
			String columnName = null;
			for(ColumnInformation columnInformation : ColumnInformation.values()) {
				if(position == columnInformation.getPosition()) {
					columnName = columnInformation.getSortColumnName();
				}
			}
			return columnName;
		}
    }

    @Autowired
    public SpectrumServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public Spectrum find(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(EmptySearchResultException::new);
    }

    @Override
    @Transactional
    public SpectrumTableResponse findSpectrumBySubmissionId(Long submissionId, String searchStr, Integer start, Integer length, Integer column, String orderDirection) {
		ObjectMapperUtils objectMapper = new ObjectMapperUtils();
		Pageable pageable = null;

		String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
		if(sortColumn != null) {
			Sort sort = new Sort(Sort.Direction.fromString(orderDirection), sortColumn);
			pageable = PageRequest.of(start/length, length, sort);
		} else {
			pageable = PageRequest.of(start/length, length);
		}

    	Page<Spectrum> spectrumPage = spectrumRepository.findSpectrumBySubmissionId(submissionId, searchStr, pageable);

		List<SpectrumDTO> spectrumList = objectMapper.map(spectrumPage.getContent(), SpectrumDTO.class);
		SpectrumTableResponse response = new SpectrumTableResponse(spectrumList);
		response.setRecordsTotal(spectrumPage.getTotalElements());
		response.setRecordsFiltered(spectrumPage.getTotalElements());

		return response;
	}
}
