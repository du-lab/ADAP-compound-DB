function select(element) {
    var allElements = document.getElementsByTagName(element.tagName);
    for (var i = 0; i < allElements.length; ++i)
        allElements[i].className = allElements[i].className.replace(/(?:^|\s)selected(?!\S)/g, '');
    element.className += ' selected';
}