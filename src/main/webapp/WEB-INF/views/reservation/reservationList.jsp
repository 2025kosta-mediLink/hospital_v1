<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>예약 내역</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reservation/reservationList.css">
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<!-- 탭 (마이페이지 공통) -->
<nav class="mypage-tabs" role="tablist" aria-label="내 내역 종류">
    <a class="tab-item active" href="${pageContext.request.contextPath}/v1/reservation/list">예약내역</a>
    <a class="tab-item" href="${pageContext.request.contextPath}/v1/reception/list">접수내역</a>
</nav>

<!-- 필터 영역 -->
<section class="history-header">
    <div class="filters">
        <!-- 월 필터 (SSR: 폼 전송) -->
        <div class="pill-select" id="monthSelect" data-current="${selectedMonth != null ? selectedMonth : 'ALL'}">
            <button type="button" class="pill-trigger" aria-haspopup="listbox" aria-expanded="false">
        <span class="pill-label" data-value="${selectedMonth != null ? selectedMonth : 'ALL'}">
          <c:choose>
              <c:when test="${empty selectedMonth}">전체</c:when>
              <c:otherwise>
                  ${fn:substring(selectedMonth,0,4)}년 ${fn:substring(selectedMonth,5,7)}월
              </c:otherwise>
          </c:choose>
        </span>
                <span class="pill-caret">▾</span>
            </button>
            <ul class="pill-menu" role="listbox" tabindex="-1" hidden>
                <li role="option" data-value="ALL" ${empty selectedMonth ? 'aria-selected="true"' : ''}>전체</li>
                <c:forEach var="opt" items="${monthOptions}">
                    <li role="option"
                        data-value="${opt.value}">
                            ${opt.label}
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- 상태 필터 (SSR: 폼 전송) -->
        <div class="pill-select" id="statusSelect" data-current="${selectedStatus != null ? selectedStatus : 'ALL'}">
            <button type="button" class="pill-trigger" aria-haspopup="listbox" aria-expanded="false">
        <span class="pill-label" data-value="${selectedStatus != null ? selectedStatus : 'ALL'}">
          <c:choose>
              <c:when test="${selectedStatus == 'RESERVED'}">예약완료</c:when>
              <c:when test="${selectedStatus == 'DONE'}">접수완료</c:when>
              <c:when test="${selectedStatus == 'CANCELLED'}">취소</c:when>
              <c:otherwise>전체</c:otherwise>
          </c:choose>
        </span>
                <span class="pill-caret">▾</span>
            </button>
            <ul class="pill-menu" role="listbox" tabindex="-1" hidden>
                <li role="option" data-value="ALL" ${selectedStatus == 'ALL' || empty selectedStatus ? 'aria-selected="true"' : ''}>전체</li>
                <li role="option" data-value="RESERVED" ${selectedStatus == 'RESERVED' ? 'aria-selected="true"' : ''}>예약완료</li>
                <li role="option" data-value="DONE" ${selectedStatus == 'DONE' ? 'aria-selected="true"' : ''}>접수완료</li>
                <li role="option" data-value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'aria-selected="true"' : ''}>취소</li>
            </ul>
        </div>
    </div>

    <!-- 필터 전송용 폼 (보이지 않음) -->
    <form id="filterForm" method="get" action="${pageContext.request.contextPath}/v1/reservation/list" hidden>
        <input type="hidden" name="month"  value="${selectedMonth}">
        <input type="hidden" name="status" value="${selectedStatus}">
    </form>
</section>

<!-- 목록 -->
<main class="main">
    <section id="groupsContainer" class="groups">
        <c:if test="${empty grouped}">
            <div class="empty">표시할 예약 내역이 없습니다.</div>
        </c:if>

        <!-- grouped는 Map<String, List<ReservationListItemDTO>> (key: YYYY-MM) -->
        <c:forEach var="entry" items="${grouped}">
            <div class="month-group">
                <div class="group-label">
                        ${fn:substring(entry.key,0,4)}년 ${fn:substring(entry.key,5,7)}월
                </div>

                <div class="cards">
                    <c:forEach var="it" items="${entry.value}">
                        <article class="card">
                            <div class="card-top">
                                <div class="title">
                                    <span class="dept">${it.departmentName}</span>
                                    <span class="dot">·</span>
                                    <span class="doctor">${it.doctorName}</span>
                                </div>

                                <span class="status-chip
                  ${it.status == 'RESERVED' ? 'status-RESERVED' : ''}
                  ${it.status == 'DONE'     ? 'status-DONE'     : ''}
                  ${it.status == 'CANCELLED'? 'status-CANCELLED': ''}">
                  <c:choose>
                      <c:when test="${it.status=='RESERVED'}">예약완료</c:when>
                      <c:when test="${it.status=='DONE'}">접수완료</c:when>
                      <c:otherwise>취소</c:otherwise>
                  </c:choose>
                </span>
                            </div>

                            <div class="sub-id">${it.reservationNo}</div>

                            <div class="meta">
                                <div class="row">
                                    <span class="icon">📅</span>
                                    <span class="date">${it.dateLabel}</span>
                                </div>
                                <div class="row">
                                    <span class="icon">⏰</span>
                                    <span class="time">${it.timeLabel}</span>
                                </div>
                            </div>

                            <div class="actions">
                                <c:choose>
                                    <c:when test="${it.status=='RESERVED'}">
                                        <button type="button" class="btn-ghost js-share" data-id="${it.reservationId}">카카오톡 공유</button>
                                        <button type="button" class="btn-ghost js-cancel" data-id="${it.reservationId}">취소하기</button>
                                    </c:when>
                                    <c:when test="${it.status=='DONE'}">
                                        <button type="button" class="btn-ghost js-share" data-id="${it.reservationId}">카카오톡 공유</button>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- 취소 상태: 버튼 없음 -->
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </section>
</main>

<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>

<script defer src="${pageContext.request.contextPath}/static/js/reservation/reservationList.js"></script>
</body>
</html>
