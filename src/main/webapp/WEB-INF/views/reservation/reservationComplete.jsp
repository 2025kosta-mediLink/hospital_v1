<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>예약 완료</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reservation/reservationComplete.css" />
</head>
<body class="screen">
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<main class="main">
    <section class="reservation-complete">
        <h2>예약이 완료되었습니다</h2>

        <!-- 예약 정보 출력 -->
        <div class="reservation-info">
            <p class="info-item"><strong>의료진:</strong> ${doctor.name}</p>
            <p class="info-item"><strong>진료과:</strong> ${doctor.departmentName}</p>
            <p class="info-item"><strong>예약일시:</strong> ${reservation.appointmentAt}</p>
            <p class="info-item"><strong>예약 번호:</strong> ${reservation.reservationNo}</p> <!-- 예약 번호 추가 -->
        </div>

        <!-- 안내사항 -->
        <div class="notice-box">
            <span class="notice-icon">⚠️</span>
            도착 10분 전 내원해주세요.
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/v1/home" class="btn-primary">홈으로</a>
            <button id="addAnotherReservation" class="btn">추가 예약하기</button>
            <button id="viewAllReservations" class="btn">예약 내역 보기</button>
        </div>
    </section>
</main>

<script src="${pageContext.request.contextPath}/static/js/reservation/reservationComplete.js"></script>

<jsp:include page="/WEB-INF/views/common/navigation.jsp" />
</body>
</html>
