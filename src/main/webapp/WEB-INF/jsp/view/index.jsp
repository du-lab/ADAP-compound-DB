<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="countConsensusSpectra" type="java.lang.Long"--%>
<%--@elvariable id="countReferenceSpectra" type="java.lang.Long"--%>

<head>
    <meta name="keywords" content="ADAP, ADAP-KDB, Spectral Knowledgebase, Spectral Library, Du-Lab, Du Lab, Dulab"/>
    <meta name="author" content="Du Lab"/>
    <meta name="description"
          content="A spectral knowledgebase for searching, tracking, and prioritizing unknown spectra"/>
</head>

<script src="<c:url value="/resources/jQuery-3.6.3/jquery-3.6.3.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.13.2/jquery-ui.min.js"/>"></script>

<div class="container">

    <div class="row">
        <div class="col" style="text-align: center">
            <h1>Welcome to ADAP-KDB Spectral Knowledgebase!</h1>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    ADAP-KDB is a compound and mass spectra search engine. You can use it to Identify and prioritize spectra.
                </div>

                <div class="card-body">
                    <p>
                        Use this web app to search query spectra against both private and public compounds and mass spectra.
                    </p>
                    <p>Use it to prioritize both known and unknown mass spectra.</p>
                    <p>Use it as a search engine to identify spectra from libraries.</p>
                    <p style="margin-bottom: 0;">
                        If you find ADAP-KDB useful, please cite <br>
                        <a href="https://pubs.acs.org/doi/10.1021/acs.analchem.1c00355">
                            Smirnov A, Liao Y, Fahy E, Subramaniam S, Du X*: <b>ADAP-KDB: A Spectral Knowledgebase for Tracking and Prioritizing Unknown GC-MS Spectra in the NIH's Metabolomics Data Repository.</b>
                            <i>Anal Chem 2021</i>, 93(36):12213-12220
                        </a>
                    </p>
                    </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col-lg-4 col-sm-12">
            <div class="card">
                <div class="card-header card-header-single homepage-card-header">
                    Search against public libraries
                </div>
                <div class="card-body">
                    <div class="homepage-image-container">
                        <img class="align-self-center" style="height: auto;width: 100%;"
                             src="<c:url value="/resources/AdapCompoundDb/img/homepage-1.png"/>"
                             alt="Creative Commons">
                    </div>
                    <div class="card-information-container">
                        <ul style="padding-left: 18px;">
                            <li>Upload files</li>
                            <li>Search for similar spectra and compounds</li>
                            <li>Export results</li>
                        </ul>
                    </div>
                    <div class="homepage-button-container">
                        <a href="/libraries/">
                            <button class="homepage-button btn btn-primary btn-lg btn-block">
                                Search Public Libraries
                            </button>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-4 col-sm-12">
            <div class="card">
                <div class="card-header card-header-single homepage-card-header">
                    Upload and search against private libraries
                </div>
                <div class="card-body homepage-card">
                    <div class="homepage-image-container">
                        <img class="align-self-center"  style="height: auto;width: 100%;"
                             src="<c:url value="/resources/AdapCompoundDb/img/homepage-2.png"/>"
                             alt="Creative Commons">
                    </div>
                    <div class="card-information-container">
                        <ul style="padding-left: 18px;">
                            <li>Upload files</li>
                            <li>Save as a private library (user account required) </li>
                            <li>Search against your libraries</li>
                        </ul>
                    </div>
                    <div class="homepage-button-container">
                        <a href="/file/upload/">
                            <button class="homepage-button btn btn-primary btn-lg btn-block">
                                Upload Private Libraries
                            </button>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-4 col-sm-12">
            <div class="card">
                <div class="card-header card-header-single homepage-card-header">
                    Prioritize your spectra
                </div>
                <div class="card-body homepage-card">
                    <div class="homepage-image-container">
                        <img class="align-self-center" style="height: 120px;"
                             src="<c:url value="/resources/AdapCompoundDb/img/homepage-3.png"/>"
                             alt="Creative Commons">
                    </div>
                    <div class="card-information-container">
                        <ul style="padding-left: 18px;">
                            <li>Search against ADAP-KDB consensus spectra</li>
                            <li>Prioritize matches based on species/source/treatment distributions</li>
                            <li>Find linked studies from Metabolomics Workbench</li>
                        </ul>
                    </div>
                    <div class="homepage-button-container">
                        <a href="/file/upload/">
                            <button class="homepage-button btn btn-primary btn-lg btn-block">
                                Prioritize Spectra
                            </button>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Contact Us
                </div>
                <div class="card-body homepage-card">
                    <p style="text-align: center">
                        ADAP Spectral Knowledgebase is currently being actively developed by the Du-Lab team (
                        <a href="https://www.du-lab.org/" target="_blank"><strong>https://www.du-lab.org/</strong></a>).
                    </p>
                    <p style="text-align: center">
                        If you encounter any issues or would like to provide a feedback on your experience using ADAP
                        Spectral Knowledgebase click
                        <a href="https://forms.gle/zYPXt463DC1WjJMy8" target="_blank">here</a>,
                        or please contact us through the email
                        <a href="mailto:dulab.binf@gmail.com">dulab.binf@gmail.com</a>.
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#carousel').carousel({interval: 4000});
    })
</script>