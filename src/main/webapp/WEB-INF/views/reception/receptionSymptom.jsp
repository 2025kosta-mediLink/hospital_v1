<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>증상 입력</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionSymptom.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/receptionSymptom.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<main class="main">
    <form id="symptomForm" method="post" action="${pageContext.request.contextPath}/v1/reception/confirm">
        <!-- 이전 단계 값 유지 -->
        <input type="hidden" name="departmentId" value="${departmentId}" />
        <input type="hidden" name="doctorId" value="${doctorId}" />

        <!-- 카드 1: 증상 선택 -->
        <div class="card">
            <h3>
                발생 증상 선택
                <span class="badge">선택 <span id="symCount">0</span>개</span>
            </h3>

            <c:choose>
                <c:when test="${empty symptoms}">
                    <div class="empty">표시할 증상이 없습니다.</div>
                </c:when>
                <c:otherwise>
                    <div class="sym-list">
                        <c:forEach var="s" items="${symptoms}">
                            <label class="sym-item">
                                <input type="checkbox" name="symptomIds" value="${s.symptomId}" aria-label="${s.name}" />
                                <c:out value="${s.name}" />
                            </label>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- 카드 2: 전달 메모 -->
        <div class="card">
            <h3>의료진에게 할 말</h3>
            <textarea id="noteToDoctor" name="noteToDoctor" class="ta"
                      placeholder="의료진에게 할 말을 작성해주세요."></textarea>
            <div class="row">
                <div class="hint">최대 500자까지 입력 가능합니다.</div>
                <div id="noteCounter" class="counter">0 / 500</div>
            </div>
        </div>

        <!-- 제출 버튼 -->
        <div class="footer">
            <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
        </div>
    </form>
</main>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body>
</html>
