<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="countConsensusSpectra" type="java.lang.Long"--%>
<%--@elvariable id="countReferenceSpectra" type="java.lang.Long"--%>

<head>
    <meta name="keywords" content="ADAP, ADAP-KDB, Spectral Knowledgebase, Spectral Library, Du-Lab, Du Lab, Dulab"/>
    <meta name="author" content="Du Lab"/>
    <meta name="description"
          content="A spectral knowledgebase for searching, tracking, and prioritizing unknown spectra"/>
</head>

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

                <div class="card-body">

                    <div class="media">
                        <a href="https://pubs.acs.org/doi/10.1021/acs.analchem.1c00355" target="_blank">
                            <img class="d-flex img-thumbnail mr-3 align-self-center"
                                 src="${pageContext.request.contextPath}/resources/AdapCompoundDb/img/analytical_chemistry_paper_300.png"
                                 alt="Analytical Chemistry">
                        </a>
                        <div class="media-body">
                            <p>
                                The number of metabolomics studies in NIH’s Metabolomics Data Repository (NMDR)
                                acquiring untargeted data from the liquid chromatography (LC-) and gas chromatography
                                coupled to mass spectrometry (GC-MS) analytical platforms has been steadily growing.
                                Accompanying this growth is the enormous number of known and unknown compounds contained
                                in that data, providing an invaluable opportunity to harness the power of big data and
                                allow for cross-species, cross-diseases, and cross-sample source analysis. Toward this
                                end, we have developed ADAP-KDB, a mass spectral knowledgebase that contains consensus
                                GC-MS and LC-MS/MS spectra extracted from untargeted metabolomics data in NMDR. ADAP-KDB
                                enables efficient sharing and aggregation of information about both known and unknown
                                compounds across studies and laboratories and makes those compounds easily findable.
                            <p/>

                            <div class="border p-3">
                                <a href="https://pubs.acs.org/doi/10.1021/acs.analchem.1c00355" target="_blank">
                                    ADAP-KDB: A Spectral Knowledgebase for Tracking and
                                    Prioritizing Unknown GC–MS Spectra in the NIH’s Metabolomics Data Repository</a>,
                                Aleksandr Smirnov, Yunfei Liao, Eoin Fahy, Shankar Subramaniam, and Xiuxia Du,
                                <em>Analytical Chemistry</em> 2021 93 (36), 12213-12220
                            </div>
                        </div>
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
                    <div id="carousel" class="carousel slide" data-ride="carousel">
                        <div class="carousel-inner" role="listbox" style="height: 525px">
                            <div class="carousel-item active">
                                <h3>Explore NMDR studies</h3>
                                <div class="media">
                                    <div class="media-body">
                                        Our users can browse all consensus spectra and find the studies where those
                                        spectra come from. For instance, to search for the studies with significant
                                        changes in glucose:
                                        <ul>
                                            <li>Browse all consensus spectra by clicking <strong>Libraries</strong>, and
                                                then <strong>View All Consensus Spectra</strong>;
                                            </li>
                                            <li>Filter the consensus spectra by clicking <strong>Filter</strong>,
                                                entering "glucose" to the <strong>Name Search</strong> box, and clicking
                                                <strong>Filter</strong> again;
                                            </li>
                                            <li>Sort the consensus spectra clicking <strong>Average P-value</strong>
                                                column header;
                                            </li>
                                        </ul>
                                        The produced list will contain all consensus spectra with "glucose" in their
                                        name, sorted by p-values of the in-study ANOVA tests. Clicking a consensus
                                        spectrum (e.g. <em>d-Glucose, 2,3,4,5,6-pentakis-O-(trimethylsilyl)-,
                                        o-methyloxyme, (1E)-</em>) will take you to the details page, where you can
                                        browse the consensus spectra, the distributions of meta information, and the
                                        associated studies. To see the studies, click <strong>Spectrum List</strong>
                                        tab, and find the corresponding study under each constituent spectrum.
                                    </div>
                                    <img class="d-flex img-thumbnail ml-3 align-self-center"
                                         src="${pageContext.request.contextPath}/resources/AdapCompoundDb/img/glucose_300.png"
                                         alt="Glucose"/>
                                </div>
                                Such studies for <em>d-Glucose, 2,3,4,5,6-pentakis-O-(trimethylsilyl)-,
                                o-methyloxyme, (1E)-</em> include
                                <ul>
                                    <li>ST000419 (“Impact Of High Sugar Diet On L-Arginine Metabolism In The Lung”),
                                    </li>
                                    <li>ST000390 (“Metabolomic markers of altered nucleotide metabolism in early
                                        stage adenocarcinoma”),
                                    </li>
                                    <li>ST000402 (“Impact of glucose on the central metabolome of C. minutissima”),</li>
                                    <li>and others</li>
                                </ul>
                            </div>

                            <div class="carousel-item">
                                <h3>Search against ADAP-KDB Library</h3>
                                <div class="media">
                                    <img class="d-flex img-thumbnail mr-3 align-self-center"
                                         src="${pageContext.request.contextPath}/resources/AdapCompoundDb/img/library_matching_300.png"
                                         alt="Library Search"/>
                                    <div class="media-body">
                                        <ul>
                                            <li>Click <strong>Upload</strong> to upload MSP files with spectra, CSV
                                                files with feature information, mzML or mzXML files with raw MS/MS
                                                spectra, or use direct links from the Metabolomics Workbench to upload
                                                raw MS/MS data. In addition, you can combine information from MSP and
                                                CSV files.
                                            </li>
                                            <li>Click <strong>Search</strong> dropdown menu and select one of the
                                                following options:
                                                <ul>
                                                    <li><strong>Search for similar spectra</strong> for matching against
                                                        library spectra based on the spectral similarity;
                                                    </li>
                                                    <li><strong>Search for similar spectra (with Ontology
                                                        Levels)</strong> for matching against library spectra based on
                                                        the spectral similarity, neutral mass, and retention time
                                                        (MS/MS only);
                                                    </li>
                                                    <li><strong>Search for similar studies</strong> for matching the
                                                        uploaded spectra directly against the Metabolomics Workbench
                                                        studies (Experimental).
                                                    </li>
                                                </ul>
                                            </li>
                                            <li>Click <strong>Filter</strong> button to select the libraries used for
                                                the matching, and select specific species, sample source, or disease.
                                            </li>
                                            <li>Click <strong>Export</strong> button to export the library matching
                                                results.
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div class="carousel-item">
                                <h3>Search against In-House library</h3>
                                <div class="media">
                                    <img class="d-flex img-thumbnail mr-3 align-self-center"
                                         src="${pageContext.request.contextPath}/resources/AdapCompoundDb/img/in_house_library_300.png"
                                         alt="In-House Library"/>
                                    <div class="media-body">
                                        Our users can search spectra against their own private in-house libraries:
                                        <ul>
                                            <li>Create an account on ADAP-KDB by clicking <strong>Log In / Sign
                                                Up</strong>.
                                            </li>
                                            <li>Upload MSP and CSV files with the library spectra/features.</li>
                                            <li>Save them as private in-house library spectra. <strong>Private</strong>
                                                means that they will not be accessible to other users,
                                                <strong>in-house</strong> means that the corresponding matches will have
                                                a higher confidence level when searched with the ontology levels, and
                                                <strong>library</strong> means that the spectra will be used for
                                                searching against.
                                            </li>
                                            <li>Search experimental spectra against the saved library.</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <ol class="carousel-indicators mb-0">
                            <li data-target="#carousel" data-slide-to="0" class="bg-primary active"
                                style="height: 10px"></li>
                            <li data-target="#carousel" data-slide-to="1" class="bg-primary" style="height: 10px"></li>
                            <li data-target="#carousel" data-slide-to="2" class="bg-primary" style="height: 10px"></li>
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
                    <p>
                        ADAP Spectral Knowledgebase is currently being actively developed by the Du-Lab team (
                        <a href="https://www.du-lab.org/" target="_blank"><strong>https://www.du-lab.org/</strong></a>).
                    </p>
                    <p>
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
</div>
</div>

<script src="<c:url value="/resources/npm/node_modules/jquery/dist/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/popper.js/dist/umd/popper.min.js"/>"></script>
<script src="<c:url value="/resources/npm/node_modules/bootstrap/dist/js/bootstrap.min.js"/>"></script>
<script>
    $(document).ready(function () {
        $('#carousel').carousel({interval: 5000});
    })
</script>