<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="countConsensusSpectra" type="java.lang.Long"--%>
<%--@elvariable id="countReferenceSpectra" type="java.lang.Long"--%>

<div align="center">
    <h1>Welcome to ADAP Spectral Library!</h1>
</div>

<section>
    <h1>Library</h1>

    <div align="center">
        <div align="left" class="text large-subsection">
            <img src="<c:url value="/resources/AdapCompoundDb/img/molecule.jpg"/>" style="float: left; margin-right: 30px;">
            <p>
                Our library is designed for tracking unknown compounds by sharing metabolite information across
                different
                labs
                and studies. Key features of our library include:
            <ul>
                <li>Collecting <strong>all</strong> (identified and unidentified) fragmentation spectra constructed by
                    the
                    data processing algorithms
                </li>
                <li>Collecting <strong>meta</strong> data for each spectrum, such as sample source, analyzed species,
                    their
                    treatment as well as the type of equipment and the data acquisition procedures.
                </li>
            </ul>

            After collecting the data, we group similar spectra together and construct consensus fragmentation spectra
            so that users can easily find compounds (identified or unidentified) that are statistically relevant to a
            specific category (i.e. sample source, species, or treatment).
            </p>
            <p>
                Currently, our library contains ${countConsensusSpectra} consensus and ${countReferenceSpectra}
                reference spectra.
            </p>
        </div>
    </div>
</section>

<section>
    <h1>Try It!</h1>
    <div align="center">
        <div align="left" class="text large-subsection">
            <img src="<c:url value="/resources/AdapCompoundDb/img/two_spectra.png"/>" style="float: right;">
            <p>
                To try out ADAP Spectral Library, perform the following steps:
            <ol>
                <li>Click <a href="/file/upload/">here</a> to upload MSP-file(s) with your GC/MS or LC/MS spectra,</li>
                <li>Browse the table of spectra read from the uploaded file(s),</li>
                <li>Search for matching library spectra by clicking the search icon
                    <i class="material-icons" style="font-size: 1em;">search</i> on the right from the spectrum name,
                </li>
                <li> (Optional) If you want to save your spectra to the library, <a href="/login/">register</a> yourself
                    as
                    a user and click the button <a href="/file/">Submit</a> below the spectrum table.
                </li>
            </ol>
            </p>
        </div>
    </div>
</section>

<section class="no-background">
    <h1>Contact Information</h1>
    <div align="center">
        <div align="left" class="text large-subsection">
            <p>
                This library is currently being actively developed by the Du-Lab team.
            </p>
            <p>
                If you encounter any issues
                (which is quite possible) or would like to provide a feedback on your experience using ADAP Spectral
                Library, please contact us through the email
                <a href="mailto:dulab.binf@gmail.com">dulab.binf@gmail.com</a>.
            </p>
        </div>
    </div>
</section>