<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>처방전 조회</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css">
    <link rel="stylesheet" href="${ctx}/static/css/prescription/prescriptionList.css">
    <style>
    /* 강제 네비바 업데이트 - 2025-01-15 */
    .nav-item.active .nav-icon {
        stroke: #3B82F6 !important;
        fill: #3B82F6 !important;
    }
    .nav-item.active .nav-label {
        color: #3B82F6 !important;
    }
    .nav-item.active {
        background: transparent !important;
    }
    </style>
</head>
<body>
<c:set var="headerTitle" value="처방전 조회" scope="request"/>

<div class="wrap">
    <jsp:include page="../common/header.jsp"/>

    <!-- Content -->
    <div class="container">
        <!-- Section Header -->
        <div class="section-header">
            <div class="section-title">출력 가능한 처방전 목록</div>
            <div class="divider"></div>
        </div>

        <!-- Prescription Cards -->
        <div class="prescription-cards">
            <c:choose>
                <c:when test="${not empty prescriptions}">
                    <c:forEach var="prescription" items="${prescriptions}" varStatus="status">
                        <div class="prescription-card">
                            <!-- Checkbox -->
                            <label class="prescription-checkbox">
                                <input type="checkbox" class="checkbox-input" 
                                       data-prescription-id="${prescription.prescriptionId}"
                                       ${prescription.canSelect && !prescription.completed && !(sessionScope.completedDispensingId != null && prescription.prescriptionId == 1) ? '' : 'disabled'}
                                       onchange="togglePrescriptionSelection(this)">
                                <div class="checkbox-custom ${prescription.completed || (sessionScope.completedDispensingId != null && prescription.prescriptionId == 1) ? 'completed' : ''}"></div>
                            </label>

                            <!-- Department Row -->
                            <div class="department-row">
                                <span class="label">진료과</span>
                                <span class="value"><c:out value="${prescription.departmentName}"/></span>
                            </div>

                            <!-- Doctor Row -->
                            <div class="doctor-row">
                                <span class="label">의사명</span>
                                <span class="value"><c:out value="${prescription.doctorName}"/></span>
                            </div>

                            <!-- Date Row -->
                            <div class="date-row">
                                <span class="label">진료일</span>
                                <span class="value">
                                    <c:choose>
                                        <c:when test="${prescription.treatmentDate != null}">
                                            <c:out value="${prescription.treatmentDate}"/>
                                        </c:when>
                                        <c:otherwise>
                                            -
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>

                            <!-- Actions Row -->
                            <div class="actions-row">
                                <div class="prescription-actions">
                                    <button class="btn-view" onclick="viewPrescription(${prescription.prescriptionId})">
                                        처방전 보기
                                    </button>
                                    <c:choose>
                                        <c:when test="${prescription.status == 'COMPLETED'}">
                                            <button class="btn-status" onclick="checkDispensingStatus(${prescription.prescriptionId})">
                                                조제 상황 확인
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn-status" onclick="viewPrescriptionDetails(${prescription.prescriptionId})">
                                                조제 상황 확인
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <!-- Completed Status Text (오른쪽에 정렬) -->
                                <c:if test="${prescription.completed || (sessionScope.completedDispensingId != null && prescription.prescriptionId == 1)}">
                                    <div class="completed-status-right">
                                        <div class="completed-date-right">
                                            <c:choose>
                                                <c:when test="${prescription.completed}">
                                                    <c:out value="${prescription.completedDate}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${sessionScope.completedDate}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="completed-pharmacy-right">
                                            <c:choose>
                                                <c:when test="${prescription.completed}">
                                                    <c:out value="${prescription.pharmacyName}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${sessionScope.completedPharmacyName}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="completed-status-text">조제 완료</div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="no-prescriptions">
                        <p>출력 가능한 처방전이 없습니다.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Find Pharmacy Button -->
    <div class="pharmacy-btn-container">
        <button class="find-pharmacy-btn" onclick="findPharmacies()" disabled>
            약국 찾기
        </button>
    </div>

    <!-- 공통 하단 네비게이션 -->
    <jsp:include page="../common/navigation.jsp"/>
</div>

<script>
    let selectedPrescriptions = new Set();

    function togglePrescriptionSelection(checkbox) {
        const prescriptionId = checkbox.dataset.prescriptionId;
        
        if (checkbox.checked) {
            selectedPrescriptions.add(prescriptionId);
        } else {
            selectedPrescriptions.delete(prescriptionId);
        }
        
        // 약국 찾기 버튼 활성화/비활성화
        const findPharmacyBtn = document.querySelector('.find-pharmacy-btn');
        if (selectedPrescriptions.size > 0) {
            findPharmacyBtn.disabled = false;
            findPharmacyBtn.style.opacity = '1';
        } else {
            findPharmacyBtn.disabled = true;
            findPharmacyBtn.style.opacity = '0.5';
        }
    }

    function findPharmacies() {
        if (selectedPrescriptions.size === 0) {
            alert('처방전을 선택해주세요.');
            return;
        }
        
        const prescriptionIds = Array.from(selectedPrescriptions);
        
        // 서버에 선택된 처방전 ID들을 전송
        fetch('${ctx}/v1/prescription/select', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'prescriptionIds=' + prescriptionIds.join(',')
        })
        .then(response => {
            if (response.ok) {
                window.location.href = '${ctx}/v1/pharmacy/search';
            } else {
                alert('처방전 선택 처리 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('처방전 선택 처리 중 오류가 발생했습니다.');
        });
    }

    function viewPrescription(prescriptionId) {
        // 처방전 상세 보기 로직
        console.log('View prescription:', prescriptionId);
        // window.location.href = '${ctx}/v1/prescription/' + prescriptionId + '/view';
    }

    function viewPrescriptionDetails(prescriptionId) {
        // 처방전 상세 정보 보기 로직
        console.log('View prescription details:', prescriptionId);
        // window.location.href = '${ctx}/v1/prescription/' + prescriptionId + '/details';
    }

    function checkDispensingStatus(prescriptionId) {
        // 조제 상황 확인 로직
        console.log('Check dispensing status:', prescriptionId);
        window.location.href = '${ctx}/v1/dispensing/status?prescriptionId=' + prescriptionId;
    }
</script>
</body>
</html>