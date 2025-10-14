<!-- /src/main/webapp/WEB-INF/views/reception/receptionToday.jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>오늘 예약 내역</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionToday.css">
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="viewport">
    <div class="frame">
        <header class="topbar">
            <a class="icon-btn" href="javascript:history.back()" aria-label="뒤로가기">←</a>
            <h1 class="title">오늘 예약 내역</h1>
            <button class="icon-btn" type="button" onclick="location.href='${ctx}/v1/home'">✕</button>
        </header>

        <section class="section-head">
            <p class="date"><fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy-MM-dd (E)"/></p>
        </section>

        <main class="list" role="main">
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

        <nav class="tabbar">
            <a href="${ctx}/v1/reservation" class="tab">예약</a>
            <a href="${ctx}/v1/reception/entry" class="tab active">접수</a>
            <a href="${ctx}/v1/home" class="tab">홈</a>
            <a href="${ctx}/v1/prescription" class="tab">처방전</a>
            <a href="${ctx}/v1/mypage" class="tab">마이페이지</a>
        </nav>
    </div>
</div>

</body>
</html>
