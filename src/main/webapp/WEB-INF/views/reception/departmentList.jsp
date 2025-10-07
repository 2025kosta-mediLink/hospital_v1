<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>진료과 선택</title></head>
<body>

<h2>진료과 선택</h2>

<form method="get" action="${pageContext.request.contextPath}/v1/doctors">
    <!-- Controller에서 넘겨준 departments 목록 반복 출력 -->
    <c:forEach var="d" items="${departments}">
        <label>
            <!-- 사용자가 선택한 진료과 ID를 GET 파라미터로 전달 -->
            <input type="radio" name="departmentId" value="${d.departmentId}" required />
                ${d.name}
        </label><br/>
    </c:forEach>

    <!-- 다음 단계(의료진 선택)으로 이동 -->
    <button type="submit">다음</button>
</form>

</body>
</html>
