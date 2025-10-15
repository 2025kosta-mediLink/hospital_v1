<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>접수하기</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionConfirm.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/receptionConfirm.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<!-- 목록 -->
<main class="main">
    <!-- 폼 시작 -->
    <form method="post" action="${pageContext.request.contextPath}/v1/reception/done">
        <!-- 숨김값 전달 -->
        <input type="hidden" name="departmentId" value="${departmentId}" />
        <input type="hidden" name="doctorId" value="${doctorId}" />
        <c:forEach var="sid" items="${symptomIds}">
            <input type="hidden" name="symptomIds" value="${sid}" />
        </c:forEach>
        <input type="hidden" name="noteToDoctor" value="${noteToDoctor}" />

        <!-- 접수 확인 정보 -->
        <div class="card">
            <h3>🗓️ 접수 확인 정보</h3>
            <div class="kv">
                <div class="k">진료과</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${not empty departmentName}"><c:out value="${departmentName}"/></c:when>
                        <c:otherwise>#<c:out value="${departmentId}"/></c:otherwise>
                    </c:choose>
                </div>

                <div class="k">의료진</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${not empty doctorName}"><c:out value="${doctorName}"/></c:when>
                        <c:otherwise>#<c:out value="${doctorId}"/></c:otherwise>
                    </c:choose>
                </div>

                <div class="k">증상</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${empty symptomNames}">
                            <span class="muted">선택 없음</span>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="nm" items="${symptomNames}">
                                <span class="pill"><c:out value="${nm}"/></span>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="k">전달사항</div>
                <div class="v note" title="${fn:escapeXml(noteToDoctor)}">
                    <c:out value="${noteToDoctor}" />
                </div>
            </div>
        </div>

        <!-- 주의사항 -->
        <div class="card alert">
            <h3>⚠️ 주의사항 확인</h3>
            <ul>
                <li>호명 후 <strong>10분</strong> 지나면 접수 취소될 수 있습니다.</li>
                <li>반드시 <strong>알림</strong>을 확인 바랍니다.</li>
                <li><strong>신분증</strong>을 반드시 지참해 주세요.</li>
                <li>대기 시간은 상황에 따라 변동될 수 있습니다.</li>
            </ul>
        </div>

        <!-- 동의 체크 (서버 검증은 이미 진행됨) -->
        <div class="card">
            <label class="consent">
                <input id="consentNotice" name="consentNotice" type="checkbox" value="true" checked />
                주의사항을 숙지하고 동의합니다.
            </label>
        </div>

        <!-- 제출 버튼 -->
        <div class="actions">
            <button id="submitBtn" class="btn-primary" type="submit">바로 접수하기</button>
        </div>
    </form>
</main>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body>
</html>
