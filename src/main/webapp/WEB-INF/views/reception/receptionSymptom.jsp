<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>증상 선택</title>
</head>
<body>
<h2>증상 선택</h2>

<!-- 증상 선택 폼 -->
<form method="post" action="${pageContext.request.contextPath}/v1/reception/confirm">
    <!-- 이전 단계에서 전달받은 진료과 / 의사 ID 유지 -->
    <input type="hidden" name="departmentId" value="${departmentId}" />
    <input type="hidden" name="doctorId" value="${doctorId}" />

    <!-- 증상 목록 출력 -->
    <c:forEach var="s" items="${symptoms}">
        <label>
            <input type="checkbox" name="symptomIds" value="${s.symptomId}" />
            <c:out value="${s.name}" /> <!-- ✅ 한글 안전 출력 -->
        </label><br/>
    </c:forEach>

    <!-- 의사에게 전달사항 입력 -->
    <textarea name="noteToDoctor" placeholder="의사에게 전달하고 싶은 내용을 적어주세요."
              style="width:300px; height:80px;"></textarea><br/>

    <!-- 주의사항 동의 체크 -->
    <label>
        <input type="checkbox" name="consentNotice" value="true" required />
        주의사항에 동의합니다.
    </label><br/>

    <!-- 다음 단계 버튼 -->
    <button type="submit">다음</button>
</form>
</body>
</html>
