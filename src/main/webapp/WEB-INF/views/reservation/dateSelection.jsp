<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // 이 줄을 추가해줌으로써 헤더에 클래스를 추가할 수 있게 돼
    request.setAttribute("isDateSelectionPage", true);
%>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>예약 날짜 선택</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/static/css/reservation/dateSelection.css">
</head>
<body class="screen">
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<main class="main">
    <!-- 달력 헤더 -->
    <section class="calendar">
        <div class="cal-head">
            <button class="cal-nav" id="prevMonth" aria-label="이전달">‹</button>
            <div class="cal-title"><span id="calYear"></span>.<span id="calMonth"></span></div>
            <button class="cal-nav" id="nextMonth" aria-label="다음달">›</button>
        </div>
        <div class="cal-grid" id="calGrid"></div>
    </section>

    <!-- 시간 선택 -->
    <section class="slot-section">
        <h3 class="slot-title">오전</h3>
        <div class="slot-row" id="amSlots"></div>
        <h3 class="slot-title">오후</h3>
        <div class="slot-row" id="pmSlots"></div>
    </section>
</main>

<!-- 예약하기 -->
<div class="sticky-footer">
    <button class="btn-primary" id="btnOpenConfirm" disabled>예약하기</button>
</div>

<!-- 예약 확인 모달 -->
<div class="modal" id="confirmModal" hidden>
    <div class="modal-backdrop"></div>
    <div class="modal-card">
        <div class="modal-title">예약 정보 확인</div>
        <div class="modal-row"><span
                class="label">진료과</span><span><strong>${doctor.departmentName}</strong></span>
        </div>
        <div class="modal-row"><span class="label">의료진</span><span><strong>${doctor.name} 교수</strong></span>
        </div>
        <div class="modal-row"><span class="label">예약 일시</span><strong><span
                id="modalWhen"></span></strong></div>
        <div class="notice-box">
            <div class="notice-header">
                <span class="notice-icon">ℹ️</span>
                <span>안내사항</span>
            </div>
            <div class="notice-content">도착 10분 전 내원해주세요</div>
        </div>
        <div class="modal-actions">
            <button class="btn-ghost" id="btnCancel">취소</button>
            <form method="post" action="${pageContext.request.contextPath}/v1/reservation/create">
                <input type="hidden" name="doctorId" value="${doctor.id}">
                <input type="hidden" name="appointmentAt" id="appointmentAt">
                <button class="btn-primary" type="submit">예약 확정</button>
            </form>
        </div>
    </div>
</div>

<script>
  window.__ctx = "${pageContext.request.contextPath}";
  window.__doctorId = "${doctor.id}";
</script>
<script defer
        src="${pageContext.request.contextPath}/static/js/reservation/dateSelection.js"></script>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body>
</html>
