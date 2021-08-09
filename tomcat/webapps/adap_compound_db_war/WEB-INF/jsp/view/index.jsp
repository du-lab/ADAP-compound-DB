<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="countConsensusSpectra" type="java.lang.Long"--%>
<%--@elvariable id="countReferenceSpectra" type="java.lang.Long"--%>

<script src="<c:url value="/resources/jQuery-3.2.1/jquery-3.2.1.min.js"/>"></script>
<script src="<c:url value="/resources/DataTables-1.10.16/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/resources/jquery-ui-1.12.1/jquery-ui.min.js"/>"></script>

<div class="container">

    <div class="row">
        <div class="col" style="text-align: center">
            <h1>Welcome to ADAP Spectral Knowledgebase!</h1>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    ADAP Spectral Knowledgebase
                </div>

                <div align="center" class="card-body">
                    <div align="left" class="text large-subsection">
                        <img src="<c:url value="/resources/AdapCompoundDb/img/molecule.jpg"/>"
                             style="float: left; margin-right: 30px;">
                        <p>
                            Our Knowledgebase is designed for tracking unknown compounds by sharing metabolite
                            information across
                            different
                            labs
                            and studies. Key features of our Knowledgebase include:
                        <ul>
                            <li>Collecting <strong>all</strong> (identified and unidentified) fragmentation spectra
                                constructed by
                                the
                                data processing algorithms
                            </li>
                            <li>Collecting <strong>meta</strong> data for each spectrum, such as sample source, analyzed
                                species,
                                their
                                treatment as well as the type of equipment and the data acquisition procedures.
                            </li>
                        </ul>

                        After collecting the data, we group similar spectra together and construct consensus
                        fragmentation spectra
                        so that users can easily find compounds (identified or unidentified) that are statistically
                        relevant to a
                        specific category (i.e. sample source, species, or treatment).
                        </p>
                        <p>
                            Currently, our knowledgebase contains ${countConsensusSpectra} consensus
                            and ${countReferenceSpectra}
                            reference spectra.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Try It!
                </div>
                <div class="card-body">
                    <div align="left" class="col text large-subsection">
                        <img src="<c:url value="/resources/AdapCompoundDb/img/two_spectra.png"/>" style="float: right;">
                        <p>
                            To try out ADAP Spectral Knowledgebase, perform the following steps:
                        <ol>
                            <li>Click <a href="/file/upload/">here</a> to upload MSP-file(s) with your GC/MS or LC/MS
                                spectra,
                            </li>
                            <li>Browse the table of spectra read from the uploaded file(s),</li>
                            <li>Search for matching knowledgebase spectra by clicking the search icon
                                <i class="material-icons" style="font-size: 1em;">search</i> on the right from the
                                spectrum
                                name,
                            </li>
                            <li> (Optional) If you want to save your spectra to the Knowledgebase, <a
                                    href="/login/">register</a>
                                yourself
                                as
                                a user and click the button <a href="/file/">Submit</a> below the spectrum table.
                            </li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row row-content">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">
                    Contact Information
                </div>
                <div class="card-body">
                    <div class="row row-content">
                        ADAP Spectral knowledgebase is currently being actively developed by the Du-Lab team (
                        <a href="https://www.du-lab.org/"
                           target="_blank"><strong>https://www.du-lab.org/</strong></a>
                        ).
                    </div>
                    <div class="row row-content">
                        If you encounter any issues (which is quite possible) or would like to provide a feedback on
                        your experience using ADAP Spectral Knowledgebase click
                        <a href="https://forms.gle/zYPXt463DC1WjJMy8" target="_blank">here</a>,
                        or please contact us through the email
                        <a href="mailto:dulab.binf@gmail.com">dulab.binf@gmail.com</a>.
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>