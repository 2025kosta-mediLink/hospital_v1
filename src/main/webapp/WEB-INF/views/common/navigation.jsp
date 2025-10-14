<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- 공통 네비게이션 CSS -->
<c:if test="${empty commonCssLoaded}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css?v=20250115_001">
    <c:set var="commonCssLoaded" value="true" scope="request"/>
</c:if>

<!-- 공통 하단 네비게이션 -->
<div class="bottom-nav-container">
    <div class="bottom-nav">
        <div class="nav-items">
            <div class="nav-item" onclick="window.location.href='${ctx}/v1/reservation/departments'">
                <div class="nav-icon">
                    <img src="${ctx}/static/images/icons/calendar_gray.png" alt="예약" class="nav-icon-img">
                </div>
                <span class="nav-label">예약</span>
            </div>
            <div class="nav-item" onclick="window.location.href='${ctx}/v1/reception/departments'">
                <div class="nav-icon">
                    <img src="${ctx}/static/images/icons/clipboard_gray.png" alt="접수" class="nav-icon-img">
                </div>
                <span class="nav-label">접수</span>
            </div>
            <div class="nav-item" onclick="window.location.href='${ctx}/v1/home'">
                <div class="nav-icon">
                    <img src="${ctx}/static/images/icons/home_gray.png" alt="홈" class="nav-icon-img">
                </div>
                <span class="nav-label">홈</span>
            </div>
            <div class="nav-item ${pageContext.request.servletPath.contains('prescription') ? 'active' : ''}" onclick="window.location.href='${ctx}/v1/prescription'">
                <div class="nav-icon">
                    <img src="${ctx}/static/images/icons/pill_blue.png" alt="처방전" class="nav-icon-img">
                </div>
                <span class="nav-label">처방전</span>
            </div>
            <div class="nav-item" onclick="window.location.href='${ctx}/v1/reception/list'">
                <div class="nav-icon">
                    <img src="${ctx}/static/images/icons/person_gray.png" alt="마이페이지" class="nav-icon-img">
                </div>
                <span class="nav-label">마이페이지</span>
            </div>
        </div>
    </div>
</div>

<script>
// 네비게이션 아이콘 호버 효과
document.addEventListener('DOMContentLoaded', function() {
    const navItems = document.querySelectorAll('.nav-item:not(.active)');
    
    navItems.forEach(item => {
        const img = item.querySelector('.nav-icon-img');
        if (!img) return;
        
        const currentSrc = img.src;
        const blueSrc = currentSrc.replace('_gray.png', '_blue.png');
        
        item.addEventListener('mouseenter', function() {
            img.src = blueSrc;
        });
        
        item.addEventListener('mouseleave', function() {
            img.src = currentSrc;
        });
    });
});
</script>
