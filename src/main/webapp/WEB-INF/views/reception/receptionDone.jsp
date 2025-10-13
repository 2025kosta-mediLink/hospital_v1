<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>접수 완료</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionDone.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/receptionDone.js"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="wrap">

    <!-- 상단바 -->
    <div class="topbar">
        <button class="btn" type="button" onclick="goBackOr('${pageContext.request.contextPath}/v1/reception/list')">
            <span class="chev">←</span>
        </button>
        <div class="title">접수 완료</div>
        <a class="btn" href="${pageContext.request.contextPath}/v1/reception/departments">닫기</a>
    </div>

    <c:choose>
        <c:when test="${not empty reception}">
            <div class="card">
                <div class="ok">✓</div>
                <div class="headline">접수가 완료되었습니다.</div>
                <div class="sub">정상적으로 접수처리 되었습니다.</div>

                <div class="num-wrap">
                    <div class="num-label">접수번호</div>
                    <span class="num"><c:out value="${reception.receptionNo}" /></span>
                </div>

                <ul class="bullets">
                    <li>앱으로 알림을 드립니다.</li>
                    <li>환자 호명 후 10분 지나면 접수가 취소됩니다.</li>
                    <li>대기 현황은 실시간으로 업데이트 됩니다.</li>
                </ul>

                <div class="meta">
                    <div class="k">진료과</div>
                    <div class="v"><c:out value="${reception.departmentName}" /></div>
                    <div class="k">의료진</div>
                    <div class="v"><c:out value="${reception.doctorName}" /></div>
                    <div class="k">상태</div>
                    <div class="v"><c:out value="${reception.status}" /></div>
                </div>

                <div class="actions">
                    <a class="btn-primary" href="${pageContext.request.contextPath}/v1/waiting/${reception.receptionId}">대기현황 보기</a>
                    <a class="btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">내 접수내역 보기</a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="empty">
                접수 정보를 불러올 수 없습니다.
                <div style="margin-top:12px">
                    <a class="btn-ghost" href="${pageContext.request.contextPath}/v1/reception/list">내 접수내역 보기</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- 하단 탭바 -->
    <nav class="nav" aria-label="하단 내비게이션">
        <a href="${ctx}/v1/reservation/departments">예약</a>
        <a class="active" href="${ctx}/v1/reception/departments">접수</a>
        <a href="${ctx}/v1/home">홈</a>
        <a href="${ctx}/v1/prescription">처방전</a>
        <a href="${ctx}/v1/reception/list">마이페이지</a>
    </nav>
</div>
</body>
</html>
