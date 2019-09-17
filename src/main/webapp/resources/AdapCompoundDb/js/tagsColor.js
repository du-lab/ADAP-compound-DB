function spanColor(studyId, spanId) {

    const colors = ["#C2272D", "#F8931F", "#009245", "#0193D9", "#0C04ED", "#612F90"];

    for (var i = 0; i < spanId; i++) {
        var n = i;
        while (n > 5) {
            n = n - 6;
        }
        document.getElementById( studyId + 'color' + i ).style.color = colors[n];
    }
}