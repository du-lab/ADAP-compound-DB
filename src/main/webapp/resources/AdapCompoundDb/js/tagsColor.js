function spanColor(studyId, spanId) {

    const colors = ["#C2272D", "#009245", "#0193D9", "#0C04ED", "#612F90"];

    for (var i = 0; i < spanId; i++) {
        var n = i;
        while (n > 4) {
            n = n - 5;
        }
        if (document.getElementById( studyId + 'color' + i ) != null) {
            document.getElementById( studyId + 'color' + i ).style.color = colors[n];
        }

    }
}