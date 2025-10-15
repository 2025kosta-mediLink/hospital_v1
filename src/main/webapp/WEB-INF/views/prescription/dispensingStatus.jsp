<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>조제 상황</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/prescription/dispensingStatus.css?v=20250115_modal">
    <script src="${pageContext.request.contextPath}/static/js/prescription/dispensingStatus.js?v=20250115_modal"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:set var="headerTitle" value="조제 상황" scope="request"/>

<div class="wrap">
    <jsp:include page="../common/header.jsp"/>

    <!-- 지도 및 경로 안내 -->
    <div class="map-container">
        <div class="map-placeholder">
            <div class="map-content">
                <!-- 지도 API가 들어갈 영역 -->
                <div class="map-mock">
                    <div class="route-info">
                        <div class="route-start">출발</div>
                        <div class="route-line"></div>
                        <div class="route-end">도착</div>
                    </div>
                </div>
            </div>
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
                    <div class="step ${'RECEIVED'.equals(status.status) ? 'current' : (status.receivedAt != null ? 'completed' : 'pending')}">
                        <div class="step-icon">
                            <c:choose>
                                <c:when test="${status.receivedAt != null}">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                        <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                                    </svg>
                                </c:when>
                                <c:otherwise>
                                    <div class="step-circle ${'RECEIVED'.equals(status.status) ? 'active' : ''}"></div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="step-content">
                            <div class="step-title">약국이 처방전을 수령했습니다.</div>
                            <c:if test="${status.receivedAt != null}">
                                <div class="step-time"><c:out value="${status.receivedAt}"/></div>
                            </c:if>
                        </div>
                    </div>

                    <!-- 2단계: 조제 중 -->
                    <div class="step ${'DISPENSING'.equals(status.status) ? 'current' : (('COMPLETED'.equals(status.status) || 'RECEIVED_BY_USER'.equals(status.status)) ? 'completed' : 'pending')}">
                        <div class="step-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                        </div>
                        <div class="step-content">
                            <div class="step-title">약을 제조 중입니다.</div>
                            <c:if test="${not empty status.dispenserName}">
                                <div class="step-detail">
                                    (제조자: <c:out value="${status.dispenserName}"/>)
                                </div>
                            </c:if>
                            <c:if test="${not empty status.estimatedCompletionTime}">
                                <div class="step-detail">
                                    예상 완료 시간: <c:out value="${status.estimatedCompletionTime}"/>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- 3단계: 조제 완료 -->
                    <div class="step ${'COMPLETED'.equals(status.status) ? 'current' : ('RECEIVED_BY_USER'.equals(status.status) ? 'completed' : 'pending')}">
                        <div class="step-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                                <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                            </svg>
                        </div>
                        <div class="step-content">
                            <div class="step-title">제조가 완료되었습니다.</div>
                            <c:if test="${status.completedAt != null}">
                                <div class="step-time">
                                    (<c:out value="${status.completedAt}"/>)
                                </div>
                            </c:if>
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
