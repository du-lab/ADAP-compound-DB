package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.search.CriteriaBlock;
import org.dulab.adapcompounddb.models.Hit;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpectrumServiceImpl implements SpectrumService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;

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
    public List<SpectrumDTO> findSpectrumBySubmissionId(Long submissionId, int start, int length, String column, String orderDirection) {
		ObjectMapperUtils objectMapper = new ObjectMapperUtils();

		Sort sort = new Sort(Sort.Direction.fromString(orderDirection), column);
		Pageable pageable = new PageRequest(1 + start/length, length, sort);
		
    	List<SpectrumDTO> spectrumDTOList = objectMapper.map(spectrumRepository.findSpectrumBySubmissionId(submissionId, pageable), SpectrumDTO.class);
		return spectrumDTOList;
	}
}
