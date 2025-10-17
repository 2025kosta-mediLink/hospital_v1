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
        <section class="panel panel-quick" aria-label="빠른 실행">
            <a class="quick need-login" href="${ctx}/v1/reservation/departments">
                <img class="quick-icon" src="${ctx}/static/images/icons/calendar_blue.png" alt="">
                <span class="quick-label">진료예약</span>
            </a>
            <a class="quick need-login" href="${ctx}/v1/reception/departments">
                <img class="quick-icon" src="${ctx}/static/images/icons/clipboard_blue.png" alt="">
                <span class="quick-label">진료접수</span>
            </a>
            <a class="quick need-login" href="${ctx}/v1/prescription">
                <img class="quick-icon" src="${ctx}/static/images/icons/pill_blue.png" alt="">
                <span class="quick-label">처방전</span>
            </a>
        </section>

        <!-- 진료시간 안내 -->
        <section class="panel panel-hours" aria-labelledby="hours-heading">
            <div class="hours-title-row">
                <h2 id="hours-heading" class="hours-title">진료시간 안내</h2>
            </div>

            <div class="hours-row">
                <span class="hours-label">월~금</span>
                <span class="hours-value">오전 9:00 ~ 오후 6:00</span>
            </div>

            <div class="hours-row">
                <span class="hours-label">토</span>
                <span class="hours-value">오전 9:00 ~ 오후 1:00</span>
            </div>

            <div class="hours-row">
                <span class="hours-label">점심시간</span>
                <span class="hours-value">오후 12:30 ~ 오후 1:30</span>
            </div>
        </section>
    </div>
    <!-- 문의 푸터 -->
    <div class="contact-footer" aria-label="문의 안내">
        <div class="cf-text">문의사항이 있으면 언제든 연락주세요</div>

        <div class="cf-row">
            <a class="cf-phone" href="tel:15881234">
                <!-- phone icon (blue) -->
                <svg class="cf-ico" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                    <path d="M22 16.92v2a2 2 0 0 1-2.18 2 19.86 19.86 0 0 1-8.63-3.07A19.5 19.5 0 0 1 3.15 10.8 19.86 19.86 0 0 1 .08 2.18 2 2 0 0 1 2.06 0h2a2 2 0 0 1 2 1.72c.13.98.36 1.94.68 2.85a2 2 0 0 1-.45 2.11L5.1 8.9a16 16 0 0 0 6 6l2.22-1.19a2 2 0 0 1 2.11.45c.91.32 1.87.55 2.85.68A2 2 0 0 1 22 16.92Z"
                          stroke="#3B82F6" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>1588-1234</span>
            </a>

            <span class="cf-sep" aria-hidden="true"></span>

            <span class="cf-hours">
      <!-- clock icon (muted) -->
      <svg class="cf-ico" viewBox="0 0 24 24" fill="none" aria-hidden="true">
        <circle cx="12" cy="12" r="9" stroke="#64748B" stroke-width="1.6"/>
        <path d="M12 7v5l3 2" stroke="#64748B" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <span>평일 09:00-18:00</span>
    </span>
        </div>
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
