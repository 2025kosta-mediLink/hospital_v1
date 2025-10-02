<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>최종 확인</title></head>
<body>
<h2>최종 확인</h2>
<form method="post" action="${pageContext.request.contextPath}/v1/reception/done">
    <input type="hidden" name="departmentId" value="${departmentId}" />
    <input type="hidden" name="doctorId" value="${doctorId}" />
    <c:forEach var="sid" items="${symptomIds}">
        <input type="hidden" name="symptomIds" value="${sid}" />
    </c:forEach>
    <input type="hidden" name="noteToDoctor" value="${noteToDoctor}" />
    <input type="hidden" name="consentNotice" value="${consentNotice}" />

    <p>진료과: ${departmentId}</p>
    <p>의료진: ${doctorId}</p>
    <p>증상: ${symptomNames}</p>
    <p>전달사항: ${noteToDoctor}</p>
    <p>동의여부: ${consentNotice}</p>

    <button type="submit">바로 접수하기</button>
</form>
</body>
</html>

