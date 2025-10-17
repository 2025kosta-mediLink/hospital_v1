<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>홈</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/home.css">
</head>
<!-- 컨트롤러에서 setAttribute -->
<body class="screen" data-ctx="${pageContext.request.contextPath}" data-is-logged-in="${isLoggedIn}" data-login-url="${loginUrl}">
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<main class="main">
    <div class="hero" aria-hidden="true"></div>

    <div class="container">
        <!-- 오늘의 예약일정 -->
        <section class="panel panel-today" aria-labelledby="today-heading">
            <!-- 상단: 아이콘 + 타이틀 (가로 가운데 정렬) -->
            <div class="title-center">
                <img class="icon" src="${pageContext.request.contextPath}/static/images/icons/calendar_blue.png" alt="">
                <h2 id="today-heading" class="title">오늘의 예약일정</h2>
            </div>

            <!-- 진료과 / 의사 / 시간 -->
            <div class="info-line">
                <c:choose>
                    <c:when test="${not empty summary.appointmentAt}">
                        <c:out value="${summary.departmentName}"/>
                        &nbsp;•&nbsp;
                        <c:out value="${summary.doctorName}"/> 교수
                        &nbsp;•&nbsp;
                        <fmt:formatDate value="${summary.appointmentAt}" pattern="a hh:mm"/>
                    </c:when>
                    <c:otherwise>
                        오늘 예약된 일정이 없습니다.
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- 구분선 -->
            <div class="divider" role="separator" aria-hidden="true"></div>

            <!-- 대기 순번 -->
            <div class="queue-row">
                <div class="queue-label">대기 순번</div>

                <c:choose>
                    <c:when test="${not empty summary.myQueueNo}">
                        <span class="queue-badge"><c:out value="${summary.myQueueNo}"/></span>
                    </c:when>
                    <c:otherwise>
                        <span class="queue-badge">-</span>
                    </c:otherwise>
                </c:choose>
            </div>

        </section>

        <!-- 퀵 액션 (모두 보호: 비로그인 시 클릭 -> 로그인으로) -->
        <section class="panel" aria-label="빠른 실행">
            <div class="grid3">
                <a class="quick need-login" href="${ctx}/v1/reservation/departments">진료예약</a>
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
</main>
    <jsp:include page="/WEB-INF/views/common/navigation.jsp"/>

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
