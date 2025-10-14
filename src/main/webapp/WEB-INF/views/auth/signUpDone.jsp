<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
  <title>회원가입 완료</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/common.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/auth/signUpDone.css">
  <script defer src="${pageContext.request.contextPath}/static/js/common.js"></script>
  <script defer src="${pageContext.request.contextPath}/static/js/auth/signUpDone.js"></script>
</head>
<body class="screen" data-ctx="${pageContext.request.contextPath}">
<% request.setAttribute("pageTitle", ""); %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="sign-done">
  <h2 class="done-title">회원가입이 완료되었습니다</h2>

  <a class="btn btn-primary done-btn" href="${pageContext.request.contextPath}/home">확인</a>
</div>

<% request.setAttribute("activeKey", "home"); %>
<jsp:include page="/WEB-INF/views/common/navigation.jsp"/>
</body>
</html>
