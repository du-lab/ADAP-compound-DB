<%--
  Created by IntelliJ IDEA.
  User: Gorleshanmukh
  Date: 12/14/2022
  Time: 2:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="row">
  <div class="col-md-8 offset-md-2">
    <p>Leave parameters blanks to use the default values.</p>
  </div>
</div>
<div class="form-group row">
  <form:label id="scoreThreshold" path="scoreThreshold"
              cssClass="col-md-4 col-form-label">Score Threshold (1 - 1000):</form:label>
  <div class="col-md-8">
    <form:input path="scoreThreshold" type="number" step="1" cssClass="form-control"/>
  </div>
</div>

<div class="form-group row">
  <form:label path="retentionIndexTolerance"
              cssClass="col-md-4 col-form-label">Retention Index Tolerance:</form:label>
  <div class="col-md-8">
    <form:input path="retentionIndexTolerance" type="number" cssClass="form-control"/>
  </div>
</div>

<div class="form-group row">
  <form:label path="retentionIndexMatch"
              cssClass="col-md-4 col-form-label">Retention Index Match:</form:label>
  <div class="col-md-8">
    <form:select id="retention" path="retentionIndexMatch" cssClass="form-control">
      <form:option value="IGNORE_MATCH">Ignore Retention Index</form:option>
      <form:option
              value="PENALIZE_NO_MATCH_STRONG">Penalize matches without Retention Index (Strong)</form:option>
      <form:option
              value="PENALIZE_NO_MATCH_AVERAGE">Penalize matches without Retention Index (Average)</form:option>
      <form:option
              value="PENALIZE_NO_MATCH_WEAK">Penalize matches without Retention Index (Weak)</form:option>
      <form:option value="ALWAYS_MATCH">Always match Retention Index</form:option>
    </form:select>
  </div>
</div>

<div class="form-group row">
  <form:label path="mzTolerance"
              cssClass="col-md-4 col-form-label">m/z tolerance</form:label>
  <div class="input-group col-md-8">
    <form:input path="mzTolerance" type="number" step="0.001"
                cssClass="form-control"/>
    <div class="input-group-append">
      <form:select path="mzToleranceType" cssClass="input-group-text">
        <form:option value="DA">Da</form:option>
        <form:option value="PPM">ppm</form:option>
      </form:select>
    </div>
  </div>
</div>

<div class="form-group row">
  <form:label path="limit"
              cssClass="col-md-4 col-form-label">Matches per Spectrum</form:label>
  <div class="col-md-8">
    <form:input id="limit" path="limit" type="number" cssClass="form-control"/>
  </div>
</div>
