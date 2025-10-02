<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>증상 선택</title></head>
<body>
<h2>증상 선택</h2>
<form method="post" action="${pageContext.request.contextPath}/v1/reception/confirm">
    <input type="hidden" name="departmentId" value="${departmentId}" />
    <input type="hidden" name="doctorId" value="${doctorId}" />

    <c:forEach var="s" items="${symptoms}">
        <label>
            <input type="checkbox" name="symptomIds" value="${s.symptomId}" />
                ${s.name}
        </label><br/>
    </c:forEach>

    <textarea name="noteToDoctor"></textarea><br/>
    <label><input type="checkbox" name="consentNotice" value="true" required/> 동의</label><br/>
    <button type="submit">다음</button>
</form>

</body>
</html>