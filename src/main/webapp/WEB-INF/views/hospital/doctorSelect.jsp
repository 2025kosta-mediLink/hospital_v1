<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>의료진 선택</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/hospital/doctorSelect.css">
</head>
<body class="screen">
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<!-- 공지사항 슬라이드 -->
<div class="doctor-notices card">
  <c:choose>
    <c:when test="${not empty doctorList}">
      <c:forEach var="doctor" items="${doctorList}">
        <c:forEach var="notice" items="${doctor.notices}">
          <div class="doctor-notice" data-doctor-id="${doctor.id}">
            <span class="doctor-name"><b>${doctor.name} 교수</b></span><br>
            <span class="notice-content">📢 ${notice.content}</span>
          </div>
        </c:forEach>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <div class="doctor-notice active">
        <span class="notice-icon">📢</span> 공지사항이 없습니다.
      </div>
    </c:otherwise>
  </c:choose>
</div>
<!-- 의사 이름 슬라이드 -->
<div class="doctor-names" id="doctorNames">
  <c:forEach var="doctor" items="${doctorList}" varStatus="s">
    <button
            type="button"
            class="doctor-chip ${s.first ? 'selected' : ''}"
            data-index="${s.index}"
            data-id="${doctor.id}">
        ${doctor.name}
    </button>
  </c:forEach>
</div>

<!-- 의사 카드 캐러셀 래퍼 -->
<div class="doctor-cards-wrap">
  <!-- 의사 카드 슬라이드 -->
  <div class="doctor-cards">
    <c:forEach var="doctor" items="${doctorList}" varStatus="s">
      <div class="doctor-card ${s.first ? 'selected' : ''}"
           data-index="${s.index}"
           data-id="${doctor.id}">
        <div class="card-top">
          <div class="name-area">
            <strong class="doctor-name-text">${doctor.name}</strong>
          </div>
          <div class="photo-area">
            <img src="${doctor.profileImageUrl != null ? doctor.profileImageUrl : '/static/images/default-doctor.png'}"
                 alt="프로필 이미지"
                 class="doctor-profile-image">
          </div>
        </div>

        <div class="card-bottom">
          <div class="card-subtitle">주간 진료시간</div>
          <table class="card-schedule">
            <thead>
            <tr>
              <th>시간</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td class="time-col">오전</td>
              <c:forEach var="day" items="${doctor.schedule}">
                <td><span class="${day.amFlag ? 'dot' : 'dash'}">${day.amFlag ? '●' : '–'}</span></td>
              </c:forEach>
            </tr>
            <tr>
              <td class="time-col">오후</td>
              <c:forEach var="day" items="${doctor.schedule}">
                <td><span class="${day.pmFlag ? 'dot' : 'dash'}">${day.pmFlag ? '●' : '–'}</span></td>
              </c:forEach>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </c:forEach>
  </div>

  <!-- 아래 화살표 네비(추가) -->
  <div class="cards-nav">
    <button type="button" class="cards-prev" aria-label="이전 카드">←</button>
    <button type="button" class="cards-next" aria-label="다음 카드">→</button>
  </div>
</div>


<!-- 예약 폼 -->
<form method="post">
  <input type="hidden" name="doctorId" value="${doctorList[0].id}">
  <button type="submit" class="btn-primary">다음</button>
</form>

<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>

<script defer src="${pageContext.request.contextPath}/static/js/common.js"></script>
<script defer src="${pageContext.request.contextPath}/static/js/hospital/doctorSelect.js"></script>
</body>
</html>
