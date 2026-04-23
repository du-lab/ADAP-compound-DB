<%--
  Created by IntelliJ IDEA.
  User: Gorleshanmukh
  Date: 12/14/2022
  Time: 2:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="errorMessage" type="java.lang.String"--%>
<%--@elvariable id="successMessage" type="java.lang.String"--%>
<div class="alert-danger" style="margin-bottom: 5px;">${errorMessage}</div>
<div class="alert-success" style="margin-bottom: 5px;">${successMessage}</div>
<div class="row">
  <div class="col-md-12">
    <p class="errors" style="color: black;">You can change the default value of each parameter on the
      <a style="color:black;text-decoration: underline;" href="<c:url value="/account/"/>">Account</a> page.
      Leave parameters blank to use the default values.
    </p>
  </div>
</div>
<div class="form-group row">
  <form:label id="scoreThreshold${param.PARAM_FOR}" path="scoreThreshold${param.PARAM_FOR}"
              cssClass="col-md-4 col-form-label">Score Threshold (1 - 1000):</form:label>
  <div class="col-md-8">
    <form:input path="scoreThreshold${param.PARAM_FOR}" type="number" step="1" cssClass="form-control"
                value="${param.SCORE_THRESHOLD}"
                id="scoreThreshold${param.PARAM_FOR}"/>
    <small class="form-text text-muted">
      Minimum similarity score required for a match to be considered valid. Higher values make matching
      more stringent (fewer false positives, but may miss weaker true matches).
    </small>
  </div>
</div>

<div class="form-group row">
  <form:label path="retentionIndexTolerance${param.PARAM_FOR}"
              cssClass="col-md-4 col-form-label">Retention Index Tolerance:</form:label>
  <div class="col-md-8">
    <form:input id="$retentionIndexTolerance${param.PARAM_FOR}"
                path="retentionIndexTolerance${param.PARAM_FOR}" type="number" cssClass="form-control"
                value="${param.RETENTION_INDEX_TOLERANCE}"/>
    <small class="form-text text-muted">
      Maximum allowed difference between the experimental retention index and the library retention index.
      Smaller values enforce stricter chromatographic agreement.
    </small>
  </div>
</div>

<div class="form-group row">
  <form:label path="retentionIndexMatch${param.PARAM_FOR}"
              cssClass="col-md-4 col-form-label">Retention Index Match:</form:label>
  <div class="col-md-8">
    <form:select id="retention" path="retentionIndexMatch${param.PARAM_FOR}" cssClass="form-control">
      <form:option value="IGNORE_MATCH"
                   selected="${param.RETENTION_INDEX_MATCH == 'IGNORE_MATCH' ? 'selected' : ''}"
      >Ignore Retention Index</form:option>
      <form:option
              value="PENALIZE_NO_MATCH_STRONG"
              selected="${param.RETENTION_INDEX_MATCH == 'PENALIZE_NO_MATCH_STRONG' ? 'selected' : ''}"
      >Penalize matches without Retention Index (Strong)</form:option>
      <form:option
              value="PENALIZE_NO_MATCH_AVERAGE"
              selected="${param.RETENTION_INDEX_MATCH == 'PENALIZE_NO_MATCH_AVERAGE' ? 'selected' : ''}"
      >Penalize matches without Retention Index (Average)</form:option>
      <form:option
              value="PENALIZE_NO_MATCH_WEAK"
              selected="${param.RETENTION_INDEX_MATCH == 'PENALIZE_NO_MATCH_WEAK' ? 'selected' : ''}"
      >Penalize matches without Retention Index (Weak)</form:option>
      <form:option value="ALWAYS_MATCH"
                   selected="${param.RETENTION_INDEX_MATCH == 'ALWAYS_MATCH' ? 'selected' : ''}"
      >Always match Retention Index</form:option>
    </form:select>
    <small class="form-text text-muted">
      Determines how retention index information is used during matching (for example required, optional,
      or weighted in scoring depending on the selected mode). Ignore Retention Index means retention
      index is not used during matching and only spectral similarity is considered.
    </small>
  </div>
</div>

<div class="form-group row">
  <form:label path="mzTolerance${param.PARAM_FOR}"
              cssClass="col-md-4 col-form-label">m/z tolerance</form:label>
  <div class="input-group col-md-8">
    <form:input path="mzTolerance${param.PARAM_FOR}" type="number" step="0.001"
                cssClass="form-control"
                id="mzTolerance${param.PARAM_FOR}"
                value="${param.MZ_TOLERANCE}"/>
    <div class="input-group-append">
      <form:select path="mzToleranceType${param.PARAM_FOR}" cssClass="input-group-text" id="mzToleranceType${param.PARAM_FOR}">
        <form:option value="DA"
                     selected="${param.MZ_TOLERANCE_TYPE == 'DA' ? 'selected' : ''}"
        >Da</form:option>
        <form:option value="PPM"
                     selected="${param.MZ_TOLERANCE_TYPE == 'PPM' ? 'selected' : ''}"
        >ppm</form:option>
      </form:select>
    </div>
  </div>
  <div class="col-md-8 offset-md-4">
    <small class="form-text text-muted">
      Allowed mass error when comparing fragment ions between spectra. Smaller tolerance values increase
      precision but may miss matches if mass calibration is slightly off. Also used when excluding the
      precursor ion peak from an MS/MS spectrum: any peak within plus or minus this tolerance around the
      precursor m/z is removed.
    </small>
  </div>
</div>

<div class="form-group row">
  <form:label path="limit${param.PARAM_FOR}"
              cssClass="col-md-4 col-form-label">Matches per Spectrum</form:label>
  <div class="col-md-8">
    <form:input id="limit${param.PARAM_FOR}" path="limit${param.PARAM_FOR}" type="number" cssClass="form-control"
                value="${param.MATCHES_PER_SPECTRUM}"/>
    <small class="form-text text-muted">
      Maximum number of candidate matches returned for each spectrum. Higher values provide more
      possibilities but may include lower-confidence hits.
    </small>
  </div>
</div>
