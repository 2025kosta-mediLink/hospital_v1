<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>진료과 선택</title></head>
<body>
<h2>진료과 선택</h2>
<form method="get" action="${pageContext.request.contextPath}/v1/doctors">
    <c:forEach var="d" items="${departments}">
        <label>
            <input type="radio" name="departmentId" value="${d.departmentId}" />
                ${d.name}
        </label><br/>
    </c:forEach>
    <button type="submit">다음</button>
</form>
</body>
</html>
