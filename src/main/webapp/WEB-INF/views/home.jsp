<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>홈</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/home.css">
    <script defer src="${pageContext.request.contextPath}/static/js/home.js"></script>
</head>
<!-- 컨트롤러에서 setAttribute -->
<body data-is-logged-in="${isLoggedIn}" data-login-url="${loginUrl}">

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="wrap">
    <!-- 히어로: 가로 꽉차고 이미지가 넘치면 중앙 기준으로 잘림 -->
    <div class="hero" aria-hidden="true"></div>

    <div class="container">
        <!-- 오늘의 예약일정 -->
        <section class="panel" aria-labelledby="today-heading">
            <div class="row">
                <h2 id="today-heading" class="h1">오늘의 예약일정</h2>
                <span class="chip">📅 <fmt:formatDate value="<%= new java.util.Date() %>" pattern="yyyy.MM.dd (E)"/></span>
            </div>

            <div class="list">
                <c:choose>
                    <c:when test="${not empty summary.appointmentAt}">
                        <div class="muted">
                            <c:out value="${summary.departmentName}"/> 진료과
                            <span class="dot"></span>
                            의료진: <c:out value="${summary.doctorName}"/>
                        </div>
                        <div class="big"><fmt:formatDate value="${summary.appointmentAt}" pattern="a hh:mm"/></div>
                    </c:when>
                    <c:otherwise>
                        <div class="muted">오늘 예약된 일정이 없습니다.</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="row" style="margin-top:12px;">
                <div class="muted">대기 순번</div>
                <div class="big"><c:out value="${empty summary.myQueueNo ? '-' : summary.myQueueNo}"/></div>
            </div>
        </section>

        <!-- 퀵 액션 (모두 보호: 비로그인 시 클릭 -> 로그인으로) -->
        <section class="panel" aria-label="빠른 실행">
            <div class="grid3">
                <a class="quick need-login" href="${ctx}/v1/reservations/departments">진료예약</a>
                <a class="quick need-login" href="${ctx}/v1/reception/departments">진료접수</a>
                <a class="quick need-login" href="${ctx}/v1/prescription">처방전</a>
            </div>
        </section>

        <!-- 진료시간 안내 (공개 정보) -->
        <section class="panel" aria-labelledby="hours-heading">
            <h2 id="hours-heading" class="h1">진료시간 안내</h2>
            <div class="list" role="list">
                <div role="listitem">월~금&nbsp;&nbsp;오전 9:00 ~ 오후 6:00</div>
                <div role="listitem">토&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;오전 9:00 ~ 오후 1:00</div>
                <div class="muted" role="listitem">점심시간&nbsp; 오후 12:30 ~ 오후 1:30</div>
            </div>
            <div class="list" style="margin-top:10px;">
                <div class="muted">문의: 1588-1234 (09:00~18:00)</div>
            </div>
        </section>
    </div>

    <!-- 하단 탭바 -->
    <nav class="nav" aria-label="하단 내비게이션">
        <a class="need-login" href="${ctx}/v1/reservation/departments">예약</a>
        <a class="need-login" href="${ctx}/v1/reception/departments">접수</a>
        <a class="active" href="${ctx}/v1/home">홈</a> <!-- 홈은 공개 -->
        <a class="need-login" href="${ctx}/v1/prescription">처방전</a>
        <a class="need-login" href="${ctx}/v1/reception/list">마이페이지</a>
    </nav>
</div>

<!-- 비로그인 클릭 가드 -->
<script>
    (function () {
        var isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
        var loginUrl   = document.body.getAttribute('data-login-url');

        if (isLoggedIn || !loginUrl) return;

        // 보호가 필요한 링크에 대해서만 가드
        document.querySelectorAll('a.need-login').forEach(function (a) {
            a.addEventListener('click', function (e) {
                e.preventDefault();
                window.location.href = loginUrl;
            });
        });
    })();
</script>

</body>
</html>
