<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>의료진 선택</title></head>
<body>
<h2>의료진 선택</h2>

<form method="get" action="${pageContext.request.contextPath}/v1/reception/symptom">
    <c:forEach var="doc" items="${doctors}">
        <label>
            <input type="hidden" name="departmentId" value="${doc.departmentId}" />
            <input type="radio" name="doctorId" value="${doc.doctorId}" />
                ${doc.name} (${doc.departmentName})
        </label><br/>
    </c:forEach>
    <button type="submit">다음</button>
</form>

</body>
</html>
