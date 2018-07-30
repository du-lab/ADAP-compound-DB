package org.dulab.adapcompounddb.site.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumTableResponse;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.SpectrumServiceImpl.ColumnInformation;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class SpectrumServiceOtherImpl implements SpectrumService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;

    public SpectrumServiceOtherImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public Spectrum find(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(EmptySearchResultException::new);
    }

    private static float compare(Spectrum spectrum1, Spectrum spectrum2, float mzTolerance) {

        float sum = 0F;
        for (Peak peak1 : spectrum1.getPeaks())
            for (Peak peak2 : spectrum2.getPeaks())
                if (Math.abs(peak1.getMz() - peak2.getMz()) < mzTolerance)
                    sum += Math.sqrt(peak1.getIntensity() * peak2.getIntensity());

        return sum * sum;
    }

    @Override
    @Transactional
    public SpectrumTableResponse findSpectrumBySubmissionId(Long submissionId, Integer start, Integer length, Integer column, String orderDirection) {
		ObjectMapperUtils objectMapper = new ObjectMapperUtils();
		Pageable pageable = null;

		String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
		if(sortColumn != null) {
			Sort sort = new Sort(Sort.Direction.fromString(orderDirection), sortColumn);
			pageable = PageRequest.of(start/length, length, sort);
		} else {
			pageable = PageRequest.of(start/length, length);
		}

    	Page<Spectrum> spectrumPage = spectrumRepository.findSpectrumBySubmissionId(submissionId, pageable);

		List<SpectrumDTO> spectrumList = objectMapper.map(spectrumPage.getContent(), SpectrumDTO.class);
		SpectrumTableResponse response = new SpectrumTableResponse(spectrumList);
		response.setRecordsTotal(spectrumPage.getTotalElements());
		response.setRecordsFiltered(spectrumPage.getTotalElements());

		return response;
	}
}
