<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/includes/header.jsp" />
	<jsp:include page="/WEB-INF/jsp/includes/column_left_home.jsp" />
    <decorator:body />
    <jsp:include page="/WEB-INF/jsp/includes/column_right_news.jsp" />
	<jsp:include page="/WEB-INF/jsp/includes/footer.jsp" />
</body></html>