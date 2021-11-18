<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container">
    <div class="row">
        <div class="col">
            <div class="card">
                <div class="card-header card-header-single">Downloads</div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-5">
                            <ul>
                                <li>
                                    <a href="<c:url value="/ajax/download/lr_gcms"/>" download>Consensus GC-MS spectra
                                        (Low-Res)</a>
                                </li>
                                <li>
                                    <a href="<c:url value="/ajax/download/hr_gcms"/>" download>Consensus GC-MS spectra
                                        (High-Res)</a>
                                </li>
                                <li>
                                    <a href="<c:url value="/ajax/download/lcmsms_pos"/>" download>Consensus LC-MS/MS
                                        spectra
                                        (Positive)</a>
                                </li>
                                <li>
                                    <a href="<c:url value="/ajax/download/lcmsms_neg"/>" download>Consensus LC-MS/MS
                                        spectra
                                        (Negative)</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-md-7">
                            <div class="media">
                                <div class="media-body small">
                                    <p><strong>Licence:</strong> Libraries of the ADAP-KDB consensus spectra are
                                        distributed under the CC0 1.0 Universal license (Public Domain Dedication).</p>
                                    <p>You can copy, modify, and distribute these files, even for commercial purposes,
                                        all without asking permission. See the
                                        <a href="https://creativecommons.org/publicdomain/zero/1.0/"
                                           target="_blank">summary</a> and the
                                        <a href="https://creativecommons.org/publicdomain/zero/1.0/legalcode"
                                           target="_blank">full text</a> of the CC0 licence.</p>
                                </div>
                                <a href="https://creativecommons.org/" target="_blank">
                                    <img class="d-flex img-thumbnail ml-3 align-self-center" width="48"
                                         src="<c:url value="/resources/AdapCompoundDb/img/cc.svg"/>"
                                         alt="Creative Commons">
                                </a>
                                <a href="https://creativecommons.org/publicdomain/zero/1.0/" target="_blank">
                                    <img class="d-flex img-thumbnail ml-1 align-self-center" width="48"
                                         src="<c:url value="/resources/AdapCompoundDb/img/zero.svg"/>"
                                         alt="Zero">
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
