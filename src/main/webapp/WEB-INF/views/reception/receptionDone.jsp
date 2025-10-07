<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>접수 완료</title></head>
<body>
<h2>접수 완료</h2>

<c:choose>
  <c:when test="${not empty reception}">
    <p><strong>접수번호:</strong> ${reception.receptionNo}</p>
    <p><strong>진료과:</strong> ${reception.departmentName}</p>
    <p><strong>의료진:</strong> ${reception.doctorName}</p>
    <p><strong>상태:</strong> ${reception.status}</p>
  </c:when>
  <c:otherwise>
    <p>접수 정보를 불러올 수 없습니다.</p>
  </c:otherwise>
</c:choose>

<a href="${pageContext.request.contextPath}/v1/reception/list">내 접수내역 보기</a>
</body>
</html>
