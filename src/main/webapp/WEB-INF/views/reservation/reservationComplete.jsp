<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>예약 완료</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reservation/reservationComplete.css" />
</head>
<body class="screen">
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<main class="main">
    <section class="reservation-complete">
        <!-- Success Icon -->
        <div class="success-icon">
            <img src="${pageContext.request.contextPath}/static/images/check-icon.png" alt="체크 아이콘" />
        </div>

        <!-- 예약 완료 메시지 -->
        <div class="reservation-complete-text">
            예약이 완료되었습니다
        </div>

        <!-- 예약 정보 출력 -->
        <div class="reservation-info">
            <div class="reservation-info-card">
                <div class="reservation-info-title">
                    <i class="fas fa-calendar-alt"></i> <!-- 달력 아이콘 -->
                    <span>예약 정보</span>
                </div>
                <div class="info-item">
                    <span>예약 번호:</span>
                    <span>${reservation.reservationNo}</span>
                </div>
                <div class="info-item">
                    <span>진료과:</span>
                    <span>${doctor.departmentName}</span>
                </div>
                <div class="info-item">
                    <span>담당의사:</span>
                    <span>${doctor.name}</span>
                </div>
                <div class="info-item">
                    <span>예약일시:</span>
                    <span class="date-label">${reservation.appointmentAt}</span>
                </div>
                <div class="info-item">
                    <span>예약 시간:</span>
                    <span class="time-label">${reservation.timeLabel}</span>
                </div>
            </div>
        </div>

        <!-- 안내사항 -->
        <div class="notice-box">
            <span class="notice-icon">⚠️</span>
            도착 10분 전 내원해주세요.
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/v1/home" class="btn-primary">홈으로</a>
            <a href="${pageContext.request.contextPath}/v1/reservation/departments" class="btn-primary">추가 예약하기</a>
            <a href="${pageContext.request.contextPath}/v1/reservation/list" class="btn-primary">예약 내역 보기</a>
        </div>
    </section>
</main>

<script src="${pageContext.request.contextPath}/static/js/reservation/reservationComplete.js"></script>

<jsp:include page="/WEB-INF/views/common/navigation.jsp" />
</body>
</html>
