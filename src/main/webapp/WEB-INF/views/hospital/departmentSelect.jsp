<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover">
    <title>진료과 선택</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/hospital/departmentSelect.css">
    <script defer src="${pageContext.request.contextPath}/static/js/common.js"></script>
    <script defer
            src="${pageContext.request.contextPath}/static/js/hospital/departmentSelect.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="wrap">

    <!-- 진료과 검색 -->
    <div class="search">
        <input type="text" id="searchTerm" placeholder="진료과명을 입력하세요" aria-label="진료과 검색"
               value="${param.searchTerm}"/>
        <button type="button" onclick="searchDepartments()">검색</button>
    </div>

    <!-- 진료과 목록 -->
    <form id="deptForm" method="get">
        <c:choose>
            <c:when test="${empty departmentSelect or empty departmentSelect.departments}">
                <div class="empty">표시할 진료과가 없습니다.</div>
                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="grid">
                    <c:forEach var="d" items="${departmentSelect.departments}">
                        <label class="item">
                            <input class="radio" type="radio" name="departmentId" value="${d.id}"
                                   required/>
                            <div class="card">
                                <div class="icon"><c:out
                                        value="${fn:substring(d.name, 0, 1)}"/></div>
                                <div class="name"><c:out value="${d.name}"/></div>
                            </div>
                        </label>
                    </c:forEach>
                </div>
                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:otherwise>
        </c:choose>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>

</body>
</html>
