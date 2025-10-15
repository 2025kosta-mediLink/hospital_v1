<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>주변 약국 찾기</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/prescription/pharmacySearch.css?v=20250115_004">
    
    <!-- 카카오 지도 API 로드 (JavaScript 키 사용) -->
    <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=371a027cd1dac68dce2424d2ac0fd3ca"></script>
    
    <!-- API 설정 및 지도 서비스 -->
    <script src="${pageContext.request.contextPath}/static/js/config/api-config.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/map/kakao-map-service.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/prescription/pharmacySearch.js?v=20250115_001"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="wrap">
    <!-- 공통 헤더 -->
    <jsp:include page="../common/header.jsp"/>

    <!-- 필터 바 -->
    <div class="filter-bar">
        <div class="filter-dropdown">추천순 ∨</div>
        <div class="filter-tag-dropdown">거리순</div>
        <div class="filter-tag active">영업중</div>
    </div>

    <!-- 전체 화면 지도 -->
    <div class="map-container">
        <div id="map" class="map-fullscreen">
            <!-- 카카오맵 API가 들어갈 영역 -->
        </div>
    </div>

    <!-- 약국 리스트 오버레이 -->
    <div class="pharmacy-list-overlay">
        <div class="list-header">
            <div class="location-selector">현재 지도 중심 ∨</div>
            <div class="sort-selector">관련도순 ∨</div>
        </div>

        <div class="pharmacy-list">
            <!-- 카카오 API에서 로드된 약국 데이터가 여기에 동적으로 추가됩니다 -->
            <div class="loading-message">
                <div style="text-align: center; padding: 20px; color: #666;">
                    <div style="font-size: 18px; margin-bottom: 10px;">🔍</div>
                    <div>주변 약국을 검색 중입니다...</div>
                </div>
            </div>
        </div>
    </div>

    <!-- 약국 상세 정보 모달 -->
    <div class="pharmacy-detail-modal" id="pharmacyDetailModal" style="display: none;">
        <div class="modal-handle"></div>
        <div class="modal-content">
            <div class="modal-header">
                <div class="pharmacy-title-section">
                    <h3 class="pharmacy-name" id="modalPharmacyName"></h3>
                    <span class="status-badge open" id="modalStatus">영업중</span>
                </div>
                <div class="pharmacy-hours">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                        <polyline points="12,6 12,12 16,14" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    <span id="modalHours">목 08:30-21:00</span>
                </div>
                <div class="pharmacy-location">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" stroke="currentColor" stroke-width="2"/>
                        <circle cx="12" cy="10" r="3" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    <span id="modalDistance"></span>
                    <span id="modalAddress"></span>
                </div>
            </div>
            
            <div class="modal-actions">
                <button class="btn-phone" onclick="callPharmacy()">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" stroke="currentColor" stroke-width="2"/>
                    </svg>
                </button>
                <button class="btn-copy" onclick="copyAddress()">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2" stroke="currentColor" stroke-width="2"/>
                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" stroke="currentColor" stroke-width="2"/>
                    </svg>
                </button>
            </div>
            
            <div class="modal-main-action">
                <form id="sendPrescriptionForm" method="post" action="${ctx}/v1/pharmacy/send">
                    <input type="hidden" name="pharmacyId" id="modalPharmacyId">
                    <button type="submit" class="btn-send-prescription">
                        처방전 전달하기
                    </button>
                </form>
            </div>
        </div>
    </div>

    <!-- 확인 모달 -->
    <div class="confirm-modal" id="confirmModal" style="display: none;">
        <div class="confirm-overlay" onclick="closeConfirmModal()"></div>
        <div class="confirm-content">
            <div class="confirm-header">
                <h3>처방전 전달 확인</h3>
            </div>
            <div class="confirm-body">
                <p id="confirmMessage"></p>
            </div>
            <div class="confirm-actions">
                <button class="btn-cancel" onclick="closeConfirmModal()">취소</button>
                <button class="btn-confirm" onclick="confirmSendPrescription()">전달하기</button>
            </div>
        </div>
    </div>

    <!-- 공통 하단 네비게이션 -->
    <jsp:include page="../common/navigation.jsp"/>
</div>

</body>
</html>