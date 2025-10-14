<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 공통 헤더 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css?v=20250115_001">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common2.css?v=20250115_001">

<!-- 공통 헤더 -->
<header class="header">
    <button class="btn-back" onclick="history.back()">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
    </button>
    <h1><c:if test="${not empty headerTitle}">${headerTitle}</c:if></h1>
    <div class="nav-actions">
        <button class="btn-close" onclick="location.href='${pageContext.request.contextPath}/v1/home'">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </button>
    </div>
</header>