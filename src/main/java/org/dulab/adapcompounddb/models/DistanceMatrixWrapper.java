package org.dulab.adapcompounddb.models;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.jsparcehc.Matrix;
import org.dulab.jsparcehc.MatrixElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class DistanceMatrixWrapper implements Matrix {

    private static final int PAGE_SIZE = 100000;
    private final Function<Pageable, Page<SpectrumMatch>> function;
    private final Map<Long, Integer> spectrumIndexMap;

    private Page<SpectrumMatch> page;
    private Iterator<SpectrumMatch> iterator;

    public DistanceMatrixWrapper(final Function<Pageable, Page<SpectrumMatch>> function, final List<Spectrum> spectra) {
        this.function = function;
        spectrumIndexMap = IntStream.range(0, spectra.size())
                .boxed()
                .collect(Collectors.toMap(i -> spectra.get(i).getId(), i -> i));
        init();
    }

    @Override
    public void init() {
        page = function.apply(PageRequest.of(0, PAGE_SIZE));
        iterator = page.iterator();
    }

    @Override
    public int getDimension() {
        return spectrumIndexMap.size();
    }

    @Override
    public int getNumElements() {
        return (int) page.getTotalElements();
    }

    @Override
    public MatrixElement getNext() {

        // If iterator has reached the end and there exists a next page, get that next page
        if (!iterator.hasNext() && page.hasNext()) {
            page = function.apply(page.nextPageable());
            iterator = page.iterator();
        }

        // If iterator has a next element, return that element
        if (iterator.hasNext()) {
            final SpectrumMatch match = iterator.next();
            final Integer row = spectrumIndexMap.get(
                    match.getMatchSpectrum().getId());
            final Integer col = spectrumIndexMap.get(
                    match.getQuerySpectrum().getId());
            if(row != null) {
                float distance = (float) (1.0 - match.getScore());
                distance = Math.max(0F, distance);
                distance = Math.min(1F, distance);

                return new MatrixElement(row, col, distance);
            }
        }

        // Otherwise, return null
        return null;
    }
}
