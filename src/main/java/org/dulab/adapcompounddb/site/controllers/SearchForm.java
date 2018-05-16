package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.UserParameterType;
import org.dulab.adapcompounddb.site.services.UserPrincipalService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

public class SearchForm {

    private static final float THRESHOLD_FACTOR = 1000.0F;

    @Min(value = 0, message = "M/z tolerance must be positive.")
    private float mzTolerance;

    @Min(value = 1, message = "Maximum number of hits must be greater than or equal to one.")
    private int numHits;

    @Min(value = 0, message = "Matching score threshold must be between 0 and 1000.")
    @Max(value = 1000, message = "Matching score threshold must be between 0 and 1000.")
    private int scoreThreshold;

    private boolean chromatographyTypeCheck;

    private ChromatographyType chromatographyType;

    private boolean submissionCategoryCheck;

    private List<Long> submissionCategoryIds;

    public float getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(float mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public int getNumHits() {
        return numHits;
    }

    public void setNumHits(int numHits) {
        this.numHits = numHits;
    }

    public int getScoreThreshold() {
        return scoreThreshold;
    }

    public float getFloatScoreThreshold() {
        return scoreThreshold / THRESHOLD_FACTOR;
    }

    public void setScoreThreshold(int scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public boolean isChromatographyTypeCheck() {
        return chromatographyTypeCheck;
    }

    public void setChromatographyTypeCheck(boolean chromatographyTypeCheck) {
        this.chromatographyTypeCheck = chromatographyTypeCheck;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    public boolean isSubmissionCategoryCheck() {
        return submissionCategoryCheck;
    }

    public void setSubmissionCategoryCheck(boolean submissionCategoryCheck) {
        this.submissionCategoryCheck = submissionCategoryCheck;
    }

    public List<Long> getSubmissionCategoryIds() {
        return submissionCategoryIds;
    }

    public void setSubmissionCategoryIds(List<Long> submissionCategoryIds) {
        this.submissionCategoryIds = submissionCategoryIds;
    }

    @SuppressWarnings("unchecked")
    public void initialize(UserPrincipalService userPrincipalService, UserPrincipal user) {

        if (user == null) return;

        mzTolerance = (float) userPrincipalService
                .findParameter(user, SearchController.MZ_TOLERANCE_KEY)
                .getObject();

        numHits = (int) userPrincipalService
                .findParameter(user, SearchController.NUM_HITS_KEY)
                .getObject();

        scoreThreshold = Math.round(THRESHOLD_FACTOR * (float) userPrincipalService
                .findParameter(user, SearchController.SCORE_THRESHOLD_KEY)
                .getObject());

        chromatographyTypeCheck = (boolean) userPrincipalService
                .findParameter(user, SearchController.CHROMATOGRAPHY_TYPE_CHECK_KEY)
                .getObject();

        chromatographyType = (ChromatographyType) userPrincipalService
                .findParameter(user, SearchController.CHROMATOGRAPHY_TYPE_KEY)
                .getObject();

        submissionCategoryCheck = (boolean) userPrincipalService
                .findParameter(user, SearchController.SUBMISSION_CATEGORY_IDS_CHECK_KEY)
                .getObject();

        submissionCategoryIds = (List<Long>) userPrincipalService
                .findParameter(user, SearchController.SUBMISSION_CATEGORY_IDS_KEY)
                .getObject();
    }

    public void saveParameters(UserPrincipalService userPrincipalService, UserPrincipal user) {

        if (user == null) return;

        userPrincipalService.saveParameter(user,
                SearchController.MZ_TOLERANCE_KEY, UserParameterType.FLOAT, mzTolerance);

        userPrincipalService.saveParameter(user,
                SearchController.NUM_HITS_KEY, UserParameterType.INTEGER, numHits);

        userPrincipalService.saveParameter(user,
                SearchController.SCORE_THRESHOLD_KEY, UserParameterType.FLOAT, scoreThreshold / THRESHOLD_FACTOR);

        userPrincipalService.saveParameter(user,
                SearchController.CHROMATOGRAPHY_TYPE_CHECK_KEY, UserParameterType.BOOLEAN, chromatographyTypeCheck);

        userPrincipalService.saveParameter(user,
                SearchController.CHROMATOGRAPHY_TYPE_KEY, UserParameterType.CHROMATOGRAPHY_TYPE, chromatographyType);

        userPrincipalService.saveParameter(user,
                SearchController.SUBMISSION_CATEGORY_IDS_CHECK_KEY, UserParameterType.BOOLEAN, submissionCategoryCheck);

        userPrincipalService.saveParameter(user,
                SearchController.SUBMISSION_CATEGORY_IDS_KEY, UserParameterType.INTEGER_LIST, submissionCategoryIds);
    }
}
