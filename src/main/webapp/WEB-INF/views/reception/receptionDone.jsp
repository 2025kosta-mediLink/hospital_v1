<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>접수 완료</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionDone.css">
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="main">
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

                <div class="actions">
<%--                    <a class="btn-primary" href="${pageContext.request.contextPath}/v1/waiting/${reception.receptionId}">대기현황 보기</a>--%>
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
</main>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</html>
