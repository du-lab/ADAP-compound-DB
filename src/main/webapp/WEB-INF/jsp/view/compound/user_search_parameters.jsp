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
<div class="row">
  <div class="col-md-8 offset-md-2">
    <p>Leave parameters blanks to use the default values.</p>
  </div>
</div>
<div class="form-group row">
  <form:label id="scoreThreshold" path="scoreThreshold"
              cssClass="col-md-4 col-form-label">Score Threshold (1 - 1000):</form:label>
  <div class="col-md-8">
    <form:input path="scoreThreshold" type="number" step="1" cssClass="form-control"
                value="${param.SCORE_THRESHOLD}"
                id="scorethreshold"/>
  </div>
</div>

<div class="form-group row">
  <form:label path="retentionIndexTolerance"
              cssClass="col-md-4 col-form-label">Retention Index Tolerance:</form:label>
  <div class="col-md-8">
    <form:input id = "retentionIndexTolerance" path="retentionIndexTolerance" type="number" cssClass="form-control"
                value="${param.RETENTION_INDEX_TOLERANCE}"/>
  </div>
</div>

<div class="form-group row">
  <form:label path="retentionIndexMatch"
              cssClass="col-md-4 col-form-label">Retention Index Match:</form:label>
  <div class="col-md-8">
    <form:select id="retention" path="retentionIndexMatch" cssClass="form-control">
      <form:option value="IGNORE_MATCH" selected="${param.RETENTION_INDEX_MATCH == 'IGNORE_MATCH' ? 'selected' : ''}"
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
  </div>
</div>

<div class="form-group row">
  <form:label path="mzTolerance"
              cssClass="col-md-4 col-form-label">m/z tolerance</form:label>
  <div class="input-group col-md-8">
    <form:input path="mzTolerance" type="number" step="0.001"
                cssClass="form-control"
                id="mzTolerance"
                value="${param.MZ_TOLERANCE}"/>
    <div class="input-group-append">
      <form:select path="mzToleranceType" cssClass="input-group-text" id="mzToleranceType">
        <form:option value="DA"
                     selected="${param.MZ_TOLERANCE_TYPE == 'DA' ? 'selected' : ''}"
        >Da</form:option>
        <form:option value="PPM"
                     selected="${param.MZ_TOLERANCE_TYPE == 'PPM' ? 'selected' : ''}"
        >ppm</form:option>
      </form:select>
    </div>
  </div>
</div>

<div class="form-group row">
  <form:label path="limit"
              cssClass="col-md-4 col-form-label">Matches per Spectrum</form:label>
  <div class="col-md-8">
    <form:input id="limit" path="limit" type="number" cssClass="form-control" value="${param.MATCHES_PER_SPECTRUM}"/>
  </div>
</div>
