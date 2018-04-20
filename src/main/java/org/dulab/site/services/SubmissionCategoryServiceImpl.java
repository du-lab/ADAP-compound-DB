package org.dulab.site.services;

import org.dulab.models.SubmissionCategory;
import org.dulab.site.repositories.SubmissionCategoryRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubmissionCategoryServiceImpl implements SubmissionCategoryService {

    private final SubmissionCategoryRespository respository;

    @Autowired
    public SubmissionCategoryServiceImpl(SubmissionCategoryRespository repository) {
        this.respository = repository;
    }

    @Override
    @Transactional
    public List<SubmissionCategory> findAll() {
        return ServiceUtils.toList(respository.findAll());
    }
}
