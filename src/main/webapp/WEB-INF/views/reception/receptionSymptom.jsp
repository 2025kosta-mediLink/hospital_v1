<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>증상 입력</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/reception/receptionSymptom.css">
    <script defer src="${pageContext.request.contextPath}/static/js/reception/receptionSymptom.js"></script>
</head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="wrap">

    <div class="topbar">
        <button class="back" type="button"
                onclick="goBackOr('${pageContext.request.contextPath}/v1/doctors?departmentId=${departmentId}')"
                aria-label="이전으로">
            <span class="chev">←</span>
        </button>
        <div class="title">증상 입력</div>
    </div>

    <div class="sub">증상을 선택하고, 의료진에게 전달할 내용을 적어주세요.</div>

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
            <h3>의료진에게 알림</h3>
            <textarea id="noteToDoctor" name="noteToDoctor" class="ta"
                      placeholder="예) 3일째 열이 나고, 기침과 발열이 심해졌습니다. 약은 이부프로펜을 복용 중입니다."></textarea>
            <div class="row">
                <div class="hint">최대 500자까지 입력 가능합니다.</div>
                <div id="noteCounter" class="counter">0 / 500</div>
            </div>
        </div>

        <!-- 카드 3: 동의 -->
        <div class="card">
            <label class="consent">
                <input id="consentNotice" type="checkbox" name="consentNotice" value="true" required />
                주의사항에 동의합니다.
            </label>
            <div class="hint" style="margin-top:6px">
                개인정보 수집·이용 및 안내사항에 동의해야 접수가 가능합니다.
            </div>
        </div>

        <!-- 제출 버튼 -->
        <div class="footer">
            <button id="nextBtn" class="btn-primary" type="submit" disabled>다음</button>
        </div>
    </form>

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
