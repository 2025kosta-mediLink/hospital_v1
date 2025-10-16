<!-- /src/main/webapp/WEB-INF/views/reception/receptionToday.jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>오늘 예약 내역</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionToday.css">
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="main">
        <section class="section-head">
            <h2 class="title">오늘 예약 내역</h2>
            <p class="date"><fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd (E)"/></p>
        </section>
            <c:choose>
                <c:when test="${empty reservations}">
                    <div class="empty">
                        <p>오늘 예약된 일정이 없습니다.</p>
                        <a class="primary-ghost" href="${ctx}/v1/reception/department">진료 접수하러 가기</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="res" items="${reservations}">
                        <article class="card">
                            <div class="time">${res.timeText}</div>
                            <div class="meta">
                                <div class="row">
                                    <span class="label">진료과:</span><span class="value">${res.departmentName}</span>
                                    <span class="sep">|</span>
                                    <span class="label">의료진:</span><span class="value">${res.doctorName}</span>
                                </div>
                                <div class="row sub">
                                    <span class="label">예약번호:</span><span class="value code">${res.reservationNo}</span>
                                </div>
                            </div>
                            <form class="act" method="post" action="${ctx}/v1/reception/from-reservation">
                                <input type="hidden" name="reservationId" value="${res.reservationId}"/>
                                <button type="submit" class="primary">접수하기</button>
                            </form>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </main>
    <jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body>
</html>
