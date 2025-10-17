<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                    <img class="icon"
                         src="${pageContext.request.contextPath}/static/images/icons/calendar_blue.png"
                         alt="달력 아이콘" />
                    <span>예약 정보</span>
                </div>

                <div class="info-row">
                    <span class="label">예약번호</span>
                    <span class="value">${reservation.reservationNo}</span>
                </div>

                <div class="info-row">
                    <span class="label">진료과</span>
                    <span class="value">${doctor.departmentName}</span>
                </div>

                <div class="info-row">
                    <span class="label">담당의사</span>
                    <span class="value">${doctor.name}</span>
                </div>

                <div class="info-row">
                    <span class="label">예약일시</span>
                    <span class="datetime">
    <!-- appointmentAt을 문자열/Date 모두 안전하게 처리 -->
    <c:set var="__rawDate" value="${reservation.appointmentAt}" />

                        <!-- 1차: 'yyyy-MM-dd HH:mm:ss' 시도 -->
    <c:catch var="__parseErr1">
        <fmt:parseDate value="${__rawDate}" pattern="yyyy-MM-dd HH:mm:ss" var="__dateObj"/>
    </c:catch>

                        <!-- 2차: 'yyyy-MM-dd' 시도 (1차 실패 또는 null인 경우) -->
    <c:if test="${__dateObj == null}">
        <c:catch var="__parseErr2">
            <fmt:parseDate value="${__rawDate}" pattern="yyyy-MM-dd" var="__dateObj"/>
        </c:catch>
    </c:if>

    <span class="date">
      <c:choose>
          <c:when test="${__dateObj != null}">
              <fmt:formatDate value="${__dateObj}" pattern="yyyy년 M월 d일 (E)"/>
          </c:when>
          <c:otherwise>
              <!-- 파싱 실패 시 원문 출력 -->
              <c:out value="${reservation.appointmentAt}"/>
          </c:otherwise>
      </c:choose>
    </span>

                        <!-- 한 줄 띄우고 시간(파란색) -->
    <span class="time">
      <c:out value="${reservation.timeLabel}"/>
    </span>
  </span>
                </div>
            </div>
        </div>

        <!-- 안내사항 -->
        <div class="notice-box">
            <div class="notice-header">
                <span class="notice-icon">ℹ️</span>
                <span>진료 안내사항</span>
            </div>
            <div class="notice-inner">
                <div class="notice-content">&nbsp;•&nbsp;진료 30분 전까지 접수를 완료해 주세요</div>
                <div class="notice-content">&nbsp;•&nbsp;신분증과 건강보험증을 지참해 주세요</div>
                <div class="notice-content">&nbsp;•&nbsp;예약 변경은 진료 1일 전까지 가능합니다</div>
            </div>
        </div>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/v1/reservation/departments" class="btn-primary">추가 예약하기</a>
            <a href="${pageContext.request.contextPath}/v1/reservation/list" class="btn-primary">예약 내역 보기</a>
        </div>
    </section>
</main>

<script src="${pageContext.request.contextPath}/static/js/reservation/reservationComplete.js"></script>

<jsp:include page="/WEB-INF/views/common/navigation.jsp" />
</body>
</html>
