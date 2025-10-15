<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>의료진 선택</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/doctorList.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/doctorList.js"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="wrap">

    <!-- 헤더 -->
    <div class="topbar">
        <button class="back" type="button"
                onclick="goBackOr('${pageContext.request.contextPath}/v1/reception/departments')">
            <span class="chev">←</span>
        </button>
        <div class="title">의료진 선택</div>
    </div>
    <div class="sub">원하시는 의료진을 선택해 주세요.</div>

    <!-- 진료과 안내 바 -->
    <c:if test="${not empty doctors}">
        <div class="info">
            진료과: <c:out value="${doctors[0].departmentName}" />
        </div>
    </c:if>

    <!-- 선택 폼 -->
    <form id="doctorForm" method="get" action="${pageContext.request.contextPath}/v1/reception/symptom">

        <!-- departmentId는 한 번만 전송 -->
        <c:if test="${not empty doctors}">
            <input type="hidden" name="departmentId" value="${doctors[0].departmentId}" />
        </c:if>

        <c:choose>
            <c:when test="${empty doctors}">
                <div class="empty">표시할 의료진이 없습니다.</div>
                <div class="footer">
                    <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
                </div>
            </c:when>

            <c:otherwise>
                <!-- 상단 이름 칩 -->
                <div class="chips">
                    <c:forEach var="doc" items="${doctors}">
                        <label class="chip" data-doc="${doc.doctorId}">
                            <input type="radio" name="doctorId" value="${doc.doctorId}" />
                            <span><c:out value="${doc.name}"/></span>
                        </label>
                    </c:forEach>
                </div>

                <!-- 카드 리스트 -->
                <div class="grid">
                    <c:forEach var="doc" items="${doctors}">
                        <label class="item card-wrap" data-doc="${doc.doctorId}">
                            <input class="radio" type="radio" name="doctorId"
                                   value="${doc.doctorId}" required aria-label="${doc.name}" />
                            <div class="card">
                                <div class="avatar"><c:out value="${fn:substring(doc.name,0,1)}" /></div>
                                <div class="meta">
                                    <div class="name"><c:out value="${doc.name}" /></div>
                                    <div class="dept"><c:out value="${doc.departmentName}" /></div>
                                </div>
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
