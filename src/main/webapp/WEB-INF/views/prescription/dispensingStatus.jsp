<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>조제 상황</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/prescription/dispensingStatus.css?v=20250115_modal">
    
    <!-- 카카오 지도 API 로드 (길찾기 서비스 포함) -->
    <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=371a027cd1dac68dce2424d2ac0fd3ca&libraries=services"></script>
    
    <!-- API 설정 및 지도 서비스 -->
    <script src="${pageContext.request.contextPath}/static/js/config/api-config.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/map/route-service.js?v=20250115_001"></script>
    <script src="${pageContext.request.contextPath}/static/js/prescription/dispensingStatus.js?v=20250115_modal"></script>
    
    <!-- 약국 정보를 JavaScript로 전달 -->
    <c:if test="${not empty pharmacyInfo}">
        <div data-pharmacy-info='${pharmacyInfo}' style="display: none;"></div>
    </c:if>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:set var="headerTitle" value="조제 상황" scope="request"/>

<div class="wrap">
    <jsp:include page="../common/header.jsp"/>

    <!-- 지도 및 경로 안내 -->
    <div class="map-container">
        <!-- 경로 정보 표시 (지도 상단) -->
        <div class="route-info-header">
            <span class="route-path">강북삼성병원 외래동 → <span id="pharmacyName">약국명</span></span>
            <div class="route-details" id="routeDetails">
                <span class="route-distance" id="routeDistance">-</span>
                <span class="route-duration" id="routeDuration">-</span>
            </div>
        </div>
        
        <div id="routeMap" class="route-map">
            <!-- 카카오맵 길찾기가 들어갈 영역 -->
        </div>
    </div>


    <!-- 조제 현황 -->
    <div class="dispensing-status">
        <c:if test="${not empty error}">
            <div class="error-message">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <c:if test="${not empty status}">
            <div class="status-container">
                <!-- 약국 정보 제거됨 - 지도에서 길안내 제공 -->

                <!-- 조제 진행 상황 -->
                <div class="progress-steps">
                    <!-- 1단계: 약국이 처방전을 수령 -->
                    <div class="step completed">
                        <div class="step-icon">
                            <div class="step-circle completed"></div>
                        </div>
                        <div class="step-content">
                            <div class="step-title">약국이 처방전을 수령했습니다.</div>
                        </div>
                    </div>

                    <!-- 2단계: 조제 중 (현재 진행 중) -->
                    <div class="step current" id="secondStep">
                        <div class="step-icon">
                            <div class="step-circle active"></div>
                        </div>
                        <div class="step-content">
                            <div class="step-title">약을 조제 중입니다.</div>
                            <div class="step-detail">
                                (조제자 : 윤민지, 예상 완료 시간: 14:26)
                            </div>
                        </div>
                    </div>

                    <!-- 3단계: 조제 대기 (비활성) -->
                    <div class="step" id="completionStep">
                        <div class="step-icon">
                            <div class="step-circle"></div>
                        </div>
                        <div class="step-content">
                            <div class="step-title">조제가 완료되었습니다.</div>
                        </div>
                    </div>
                </div>


                <!-- 액션 버튼 -->
                <div class="action-buttons">
                    <c:choose>
                        <c:when test="${'COMPLETED'.equals(status.status)}">
                            <button class="btn-complete-receipt" onclick="completeReceipt('${status.dispensingId}')">
                                수령 완료
                            </button>
                        </c:when>
                        <c:when test="${'RECEIVED_BY_USER'.equals(status.status)}">
                            <div class="completed-message">
                                수령이 완료되었습니다.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="waiting-message">
                                조제가 완료되면 알림을 보내드립니다.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
    </div>



    <!-- 공통 하단 네비게이션 -->
    <jsp:include page="../common/navigation.jsp"/>
</div>

<!-- 조제 완료 알림 모달 -->
<div class="completion-notification-modal" id="completionNotificationModal" style="display: none;">
    <div class="completion-notification-overlay"></div>
    <div class="completion-notification-content">
        <div class="completion-notification-character">
            <img src="/static/images/icons/pharmacist.png" alt="약사" class="pharmacist-image">
        </div>
        <div class="completion-notification-time">25.09.26.14:20</div>
        <div class="completion-notification-message">약 조제가 완료되었습니다!</div>
        <button class="completion-notification-btn" onclick="closeCompletionNotification()">확인</button>
    </div>
</div>

<!-- 수령 완료 확인 모달 -->
<div class="receipt-confirm-modal" id="receiptConfirmModal" style="display: none;">
    <div class="receipt-confirm-overlay" onclick="closeReceiptConfirmModal()"></div>
    <div class="receipt-confirm-content">
        <div class="receipt-confirm-header">
            <h3>수령 완료 확인</h3>
        </div>
        <div class="receipt-confirm-body">
            <p>수령을 완료하시겠습니까?</p>
        </div>
        <div class="receipt-confirm-actions">
            <button class="btn-cancel" onclick="closeReceiptConfirmModal()">취소</button>
            <button class="btn-confirm" onclick="confirmReceiptComplete()">완료</button>
        </div>
    </div>
</div>

<script>
    // 조제 완료 알림 시뮬레이션 (실제로는 Firebase/WebSocket으로 처리)
    // 자동 팝업 표시 제거됨 - 실제 서버 상태에 따라만 표시
    
    // 팝업 관련 함수 완전 제거
    
    // updateDispensingStatus 함수 제거됨
    
    // JavaScript 함수들은 외부 dispensingStatus.js 파일에서 처리됨
    
    // 팝업 관련 함수 제거됨
</script>

<!-- 팝업창 제거됨 -->

<script src="${pageContext.request.contextPath}/static/js/prescription/dispensingStatus.js?v=20250114_1550"></script>
</body>
</html>