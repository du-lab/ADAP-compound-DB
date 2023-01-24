package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.dto.SpectrumDTO;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.BaseController;
import org.dulab.adapcompounddb.site.services.search.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.search.IndividualSearchService;
import org.dulab.adapcompounddb.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/spectrum")
public class SpectrumRestController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumRestController.class);

    private enum ColumnInformation {
        ID(0, null), NAME(1, "name"), RETENTION_TIME(2, "retentionTime"),
        PRECURSOR(3, "precursor"), PRECURSOR_TYPE(4, "precursorType"),
        SIGNIFICANCE(5, "significance"), MASS(6, "mass"),
        CHROMATOGRAPHY_TYPE(7, "chromatographyType");

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

        public static String getColumnNameFromPosition(int position) {
            for (ColumnInformation columnInformation : ColumnInformation.values())
                if (position == columnInformation.getPosition())
                    return columnInformation.getSortColumnName();
            return null;
        }
    }

    private final SpectrumService spectrumService;
    private final SpectrumMatchService spectrumMatchService;
    private final IndividualSearchService individualSearchService;

    @Autowired
    public SpectrumRestController(SpectrumService spectrumService,
                                  SpectrumMatchService spectrumMatchService,
                                  IndividualSearchService individualSearchService) {  // @Qualifier("spectrumSearchServiceImpl")
        this.spectrumService = spectrumService;
        this.spectrumMatchService = spectrumMatchService;
        this.individualSearchService = individualSearchService;
    }

    @RequestMapping(value = "/findSpectrumBySubmissionId.json", produces = "application/json")
    public String findSpectrumBySubmissionId(@RequestParam("submissionId") final Long submissionId,
                                             @RequestParam("start") final Integer start, @RequestParam("length") final Integer length,
                                             @RequestParam("column") final Integer column, @RequestParam("sortDirection") final String sortDirection,
                                             @RequestParam("search") final String searchStr, final HttpServletRequest request, final HttpSession session)
            throws JsonProcessingException {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        DataTableResponse response;
        if (submissionId > 0) {
            response = spectrumService.findSpectrumBySubmissionId(submissionId, searchStr, start,
                    length, ColumnInformation.getColumnNameFromPosition(column), sortDirection);
        } else {
            response = paginate(Submission.from(session), searchStr, start, length, column, sortDirection);
        }

        return objectMapper.writeValueAsString(response);
    }

    @RequestMapping(value = "/findClusters.json", produces = "application/json")
    public String findClusters(@RequestParam("start") final Integer start,
                               @RequestParam("length") final Integer length,
                               @RequestParam("column") final Integer column,
                               @RequestParam("sortDirection") final String sortDirection,
                               @RequestParam("chromatographyType") final String chromatographyTypeString,
                               @RequestParam("search") final String search,
                               @RequestParam("species") final String species,
                               @RequestParam("source") final String source,
                               @RequestParam("disease") final String disease)
            throws JsonProcessingException {

        /*final ObjectMapperUtils objectMapper = new ObjectMapperUtils();
        objectMapper.map(spectrumMatchService.getAllClusters(), SpectrumDTO.class);*/

        ChromatographyType chromatographyType =
                (chromatographyTypeString == null || chromatographyTypeString.equalsIgnoreCase("all"))
                        ? null : ChromatographyType.valueOf(chromatographyTypeString);

        final DataTableResponse response;
        try {
            response = spectrumMatchService.findAllClusters(this.getCurrentUserPrincipal(),
                    chromatographyType, search, species, source, disease,
                    start, length, column, sortDirection);
        } catch (Throwable t) {
            LOGGER.error("Error during cluster listing: " + t.getMessage(), t);
            throw t;
        }

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper.writeValueAsString(response);
    }

    private DataTableResponse paginate(final Submission submission, final String search,
                                       final Integer start, final Integer length, final Integer column,
                                       final String orderDirection) {

        final List<Spectrum> spectrumList = submission.getFiles().stream()
                .map(File::getSpectra).filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        final ObjectMapperUtils objectMapper = new ObjectMapperUtils();

        if (StringUtils.isNotBlank(search)) {
            final List<Spectrum> tempList = spectrumList.stream()
                    .filter(s -> StringUtils.containsIgnoreCase(s.getName(), search)
                            || StringUtils.containsIgnoreCase(s.getChromatographyType().getLabel(), search))
                    .collect(Collectors.toList());
            spectrumList.clear();
            spectrumList.addAll(tempList);
        }

        final String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
        if (sortColumn != null) {
            Comparator<Object> comparator;
            if (Sort.Direction.fromString(orderDirection).equals(Sort.Direction.ASC))
                comparator = new BeanComparator<>(sortColumn, new NullComparator(true));
            else {
                comparator = new BeanComparator<>(sortColumn, new NullComparator(false)).reversed();
            }
            spectrumList.sort(comparator);
        }

        final int totalSize = spectrumList.size();
        final List<SpectrumDTO> subList = IntStream.range(start, start + length)
                .filter(i -> i < totalSize)
                .mapToObj(i -> {
                    final Spectrum spectrum = spectrumList.get(i);
                    final SpectrumDTO dto = objectMapper.map(spectrum, SpectrumDTO.class);
                    final int fileIndex = submission.getFiles().indexOf(spectrum.getFile());
                    dto.setFileIndex(fileIndex);
                    dto.setSpectrumIndex(submission.getFiles().get(fileIndex).getSpectra().indexOf(spectrum));
                    return dto;
                })
                .collect(Collectors.toList()); //spectrumList.subList(start, start + length);

        final DataTableResponse response = new DataTableResponse(objectMapper.map(subList, SpectrumDTO.class));
        response.setRecordsTotal((long) totalSize);
        response.setRecordsFiltered((long) totalSize);

        return response;
    }
}
