package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;

public interface SearchParametersService {
    void updateUserSearchParameters(final SearchParametersDTO searchParameters, final long userId);
    SearchParametersDTO getUserSearchParameters(final long userId);
}
