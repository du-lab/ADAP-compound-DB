$(document).ready(function () {
  $('ul.nav-tabs  a').click(function (e) {
    e.preventDefault();
    $(this).tab('show');
  });
  $("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
    var id = $(e.target).attr("href");
    localStorage.setItem('selectedTab', id)
  });
  var selectedTab = localStorage.getItem('selectedTab');
  /* Keep current tab when page is refreshed */
  $('ul.nav-tabs a[href="' + selectedTab + '"]').tab('show');
});


