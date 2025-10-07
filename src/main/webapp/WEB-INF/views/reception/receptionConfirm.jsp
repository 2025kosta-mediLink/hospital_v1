<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>최종 확인</title>
</head>
<body>
<h2>최종 확인</h2>

<!--
  ✅ 사용자가 선택한 진료과 / 의사 / 증상 / 동의정보를
     다시 한 번 요약해서 보여주고, [바로 접수하기] 버튼으로 DB에 저장.
-->
<form method="post" action="${pageContext.request.contextPath}/v1/reception/done">

    <!-- 이전 단계 데이터 유지 (hidden 전송) -->
    <input type="hidden" name="departmentId" value="${departmentId}" />
    <input type="hidden" name="doctorId" value="${doctorId}" />
    <c:forEach var="sid" items="${symptomIds}">
        <input type="hidden" name="symptomIds" value="${sid}" />
    </c:forEach>
    <input type="hidden" name="noteToDoctor" value="${noteToDoctor}" />
    <input type="hidden" name="consentNotice" value="${consentNotice}" />

    <!-- 이름으로 표시 -->
    <p><strong>진료과:</strong> <c:out value="${departmentName}" /></p>
    <p><strong>의료진:</strong> <c:out value="${doctorName}" /></p>

    <!-- 증상명 목록 -->
    <p><strong>증상:</strong></p>
    <c:choose>
        <c:when test="${empty symptomNames}">
            선택한 증상이 없습니다.
        </c:when>
        <c:otherwise>
            <ul>
                <c:forEach var="nm" items="${symptomNames}">
                    <li><c:out value="${nm}" /></li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>

    <!-- 전달사항 -->
    <p><strong>전달사항:</strong> <c:out value="${noteToDoctor}" /></p>

    <!-- 동의 여부 -->
    <p><strong>동의여부:</strong>
        <c:choose>
            <c:when test="${consentNotice}">동의함</c:when>
            <c:otherwise>동의하지 않음</c:otherwise>
        </c:choose>
    </p>

    <button type="submit">바로 접수하기</button>
</form>
</body>
</html>
