<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="navbar">
    <a href="${pageContext.request.contextPath}/v1/reservation"
       class="nav-item ${activeKey eq 'reserve' ? 'active' : ''}">예약</a>

    <a href="${pageContext.request.contextPath}/v1/reception"
       class="nav-item ${activeKey eq 'reception' ? 'active' : ''}">접수</a>

    <a href="${pageContext.request.contextPath}/home"
       class="nav-item ${activeKey eq 'home' ? 'active' : ''}">홈</a>

    <a href="${pageContext.request.contextPath}/v1/prescription"
       class="nav-item ${activeKey eq 'prescription' ? 'active' : ''}">처방전</a>

    <a href="${pageContext.request.contextPath}/v1/mypage"
       class="nav-item ${activeKey eq 'mypage' ? 'active' : ''}">마이페이지</a>
</div>
