<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>진료과 선택</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/departmentList.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/departmentList.js"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="wrap">

    <div class="topbar">
        <button class="back" type="button"
                onclick="goBackOr('${pageContext.request.contextPath}/')">
            <span class="chev">←</span>
        </button>
        <div class="title">진료과 선택</div>
    </div>
    <div class="desc">원하시는 진료과를 선택해 주세요.</div>

    <div class="search">
        <input type="text" placeholder="진료과명을 입력하세요" aria-label="진료과 검색(형태만 제공)"/>
        <button type="button">검색</button>
    </div>

    <form id="deptForm" method="get" action="${pageContext.request.contextPath}/v1/reception/doctors">
        <c:choose>
            <c:when test="${empty departments}">
                <div class="empty">표시할 진료과가 없습니다.</div>
                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="grid">
                    <c:forEach var="d" items="${departments}">
                        <label class="item">
                            <input class="radio" type="radio" name="departmentId"
                                   value="${d.departmentId}" aria-label="${d.name}" required />
                            <div class="card">
                                <div class="icon"><c:out value="${fn:substring(d.name,0,1)}" /></div>
                                <div class="name"><c:out value="${d.name}" /></div>
                            </div>
                        </label>
                    </c:forEach>
                </div>

                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
                <div class="hint">선택 후 “다음”을 눌러 의료진 선택으로 이동합니다.</div>
            </c:otherwise>
        </c:choose>
    </form>

    <!-- 하단 탭바 -->
    <nav class="nav" aria-label="하단 내비게이션">
        <a href="${ctx}/v1/hospital/departments">예약</a>
        <a class="active" href="${ctx}/v1/reception/departments">접수</a>
        <a href="${ctx}/v1/home">홈</a>
        <a href="${ctx}/v1/prescription">처방전</a>
        <a href="${ctx}/v1/reception/list">마이페이지</a>
    </nav>
</div>
</body>
</html>
